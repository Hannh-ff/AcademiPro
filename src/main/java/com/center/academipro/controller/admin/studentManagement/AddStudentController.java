package com.center.academipro.controller.admin.studentManagement;

import com.center.academipro.models.Course;
import com.center.academipro.models.Class;
import com.center.academipro.utils.DBConnection;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AddStudentController implements Initializable {

    @FXML
    private TextField fullName, username, email, phone;

    @FXML
    private DatePicker birthday;

    @FXML
    private ListView<Course> courseListView;
    @FXML
    private ComboBox<Class> className;

    @Override
    public void initialize(java.net.URL location, ResourceBundle resources) {
        courseListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        loadCourses();
        loadClasses();
        restrictFutureDate();
    }

    private void loadCourses() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement("SELECT id, course_name FROM courses");
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("course_name");
                Course c = new Course(id, name);
                courseListView.getItems().add(c);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Could not load course list.");
        }
    }
    private void loadClasses() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement("SELECT id, class_name FROM classes");
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("class_name");
                Class c = new Class(id, name);
                className.getItems().add(c);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Could not load class list.");
        }
    }


    @FXML
    private void addStudent() {
        String fullNameValue = fullName.getText().trim();
        String usernameValue = username.getText().trim();
        String emailValue = email.getText().trim();
        String phoneValue = phone.getText().trim();
        LocalDate birthdayValue = birthday.getValue();
        Class selectedClass = className.getValue(); // Lấy class_id từ phone1 field

        if (fullNameValue.isEmpty() || usernameValue.isEmpty() || emailValue.isEmpty() || selectedClass==null) {
            showAlert(Alert.AlertType.ERROR, "Please fill in all required fields.");
            return;
        }
        if (birthdayValue != null && birthdayValue.isAfter(LocalDate.now())) {
            showAlert(Alert.AlertType.ERROR, "Birthday cannot be in the future.");
            return;
        }

        int classId= selectedClass.getId(); // Lấy class_id từ selectedClass


        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            // 1. Thêm vào bảng users
            String insertUser = "INSERT INTO users(fullname, username, email, password, role) VALUES (?, ?, ?, ?, 'Student')";
            PreparedStatement pstUser = conn.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS);
            pstUser.setString(1, fullNameValue);
            pstUser.setString(2, usernameValue);
            pstUser.setString(3, emailValue);
            pstUser.setString(4, "123456"); // mật khẩu mặc định

            pstUser.executeUpdate();
            ResultSet rsUser = pstUser.getGeneratedKeys();
            int userId = -1;
            if (rsUser.next()) {
                userId = rsUser.getInt(1);
            }

            // 2. Thêm vào bảng students
            String insertStudent = "INSERT INTO students(user_id, birthday, phone) VALUES (?, ?, ?)";
            PreparedStatement pstStudent = conn.prepareStatement(insertStudent, Statement.RETURN_GENERATED_KEYS);
            pstStudent.setInt(1, userId);
            pstStudent.setObject(2, birthdayValue != null ? birthdayValue : null);
            pstStudent.setString(3, phoneValue);
            pstStudent.executeUpdate();

            ResultSet rsStudent = pstStudent.getGeneratedKeys();
            int studentId = -1;
            if (rsStudent.next()) {
                studentId = rsStudent.getInt(1);
            }

            // 3. Thêm vào bảng student_classes
            String insertSC = "INSERT INTO student_classes(student_id, class_id) VALUES (?, ?)";
            PreparedStatement pstSC = conn.prepareStatement(insertSC);
            pstSC.setInt(1, studentId);
            pstSC.setInt(2, classId);
            pstSC.executeUpdate();

            conn.commit();
            showAlert(Alert.AlertType.INFORMATION, "Student added successfully.");
            clearStudent();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error occurred while adding student.");
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(type.name());
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void clearStudent() {
        fullName.clear();
        username.clear();
        email.clear();
        phone.clear();
        className.getSelectionModel().clearSelection();
        birthday.setValue(null);
        courseListView.getSelectionModel().clearSelection();
    }
    private void restrictFutureDate() {
        birthday.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isAfter(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;"); // tuỳ chọn: highlight ngày bị chặn
                }
            }
        });
    }
}
