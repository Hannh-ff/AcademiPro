package com.center.academipro.controller.admin.teacherManagement;

import com.center.academipro.models.Course;
import com.center.academipro.utils.DBConnection;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AddTeacherController implements Initializable {
    @FXML
    private TextField fullName, username, email, phone;

    @FXML
    private DatePicker birthday;

    @FXML
    private ListView<Course> courseListView;

    @FXML
    private void addTeacher() {
        String fullNameValue = fullName.getText().trim();
        String usernameValue = username.getText().trim();
        String emailValue = email.getText().trim();
        String phoneValue = phone.getText().trim();
        LocalDate birthdayValue = birthday.getValue();

        if (fullNameValue.isEmpty() || usernameValue.isEmpty() || emailValue.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please fill in all required fields.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            // 1. Thêm vào bảng users
            String insertUser = "INSERT INTO users(fullname, username, email, password, role) VALUES (?, ?, ?, ?, 'Teacher')";
            PreparedStatement pstUser = conn.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS);
            pstUser.setString(1, fullNameValue);
            pstUser.setString(2, usernameValue);
            pstUser.setString(3, emailValue);
            pstUser.setString(4, "123456"); // mật khẩu mặc định (bạn nên mã hóa với BCrypt)

            pstUser.executeUpdate();
            ResultSet rsUser = pstUser.getGeneratedKeys();
            int userId = -1;
            if (rsUser.next()) {
                userId = rsUser.getInt(1);
            }

            // 2. Thêm vào bảng teachers
            String insertTeacher = "INSERT INTO teachers(user_id, birthday, phone) VALUES (?, ?, ?)";
            PreparedStatement pstTeacher = conn.prepareStatement(insertTeacher, Statement.RETURN_GENERATED_KEYS);
            pstTeacher.setInt(1, userId);
            pstTeacher.setObject(2, birthdayValue != null ? birthdayValue : null);
            pstTeacher.setString(3, phoneValue);
            pstTeacher.executeUpdate();

            ResultSet rsTeacher = pstTeacher.getGeneratedKeys();
            int teacherId = -1;
            if (rsTeacher.next()) {
                teacherId = rsTeacher.getInt(1);
            }

            // 3. Thêm vào bảng teacher_courses
            String insertTC = "INSERT INTO teacher_courses(teacher_id, course_id) VALUES (?, ?)";
            PreparedStatement pstTC = conn.prepareStatement(insertTC);
            for (Course c : courseListView.getSelectionModel().getSelectedItems()) {
                pstTC.setInt(1, teacherId);
                pstTC.setInt(2, c.getId()); // Course cần có phương thức getId()
                pstTC.addBatch();
            }
            pstTC.executeBatch();

            conn.commit();
            showAlert(Alert.AlertType.INFORMATION, "Teacher added successfully.");
            clearTeacher();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error occurred while adding teacher.");
        }
    }

    private void showAlert( Alert.AlertType type,String message) {
        Alert alert = new Alert(type);
        alert.setTitle(type.name());
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void clearTeacher() {
        fullName.clear();
        username.clear();
        email.clear();
        phone.clear();
        birthday.setValue(null);
        courseListView.getSelectionModel().clearSelection();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Cho phép chọn nhiều dòng trong ListView
        courseListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Gọi method để load danh sách khóa học từ database
        loadCourses();
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

}
