package com.center.academipro.controller.admin.studentManagement;

import com.center.academipro.models.Class;
import com.center.academipro.models.Course;
import com.center.academipro.models.Student;
import com.center.academipro.utils.DBConnection;
import com.center.academipro.utils.SceneSwitch;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class EditStudentController implements Initializable {
    @FXML
    private TextField fullName, username, email, phone;

    @FXML
    private DatePicker birthday;

    @FXML
    private ListView<Course> courseListView;
    @FXML
    private ComboBox<Class> className;
    private int userId; // ID của user đang được chỉnh sửa

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        courseListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        loadClasses();
    }

    public void setStudent(Student student) {
        if (student == null) return;

        this.userId = student.getUserId();
        fullName.setText(student.getFullName());
        username.setText(student.getUsername());
        email.setText(student.getEmail());
        birthday.setValue(student.getBirthday());
        phone.setText(student.getPhone());

        loadClasses();
        for (Class c : className.getItems()) {
            if (c.getClassName().equals(student.getClassName())) { // so sánh tên lớp
                className.setValue(c);
                break;
            }
        }

        loadCourses();
        for (Course c : courseListView.getItems()) {
            if (student.getCourse().contains(c.getCourseName())) {
                courseListView.getSelectionModel().select(c);
            }
        }
    }

    @FXML
    private void updateStudent(ActionEvent actionEvent) {
        String fullNameStr = fullName.getText();
        String usernameStr = username.getText();
        String emailStr = email.getText();
        String phoneStr = phone.getText();
        LocalDate birthdayVal = birthday.getValue();
        Class selectedClassObj = className.getSelectionModel().getSelectedItem();
        ObservableList<Course> selectedCourses = courseListView.getSelectionModel().getSelectedItems();

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            // Cập nhật bảng users
            String sqlUser = "UPDATE users SET fullname = ?, username = ?, email = ? WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlUser)) {
                ps.setString(1, fullNameStr);
                ps.setString(2, usernameStr);
                ps.setString(3, emailStr);
                ps.setInt(4, userId);
                ps.executeUpdate();
            }

            // Cập nhật bảng students
            String sqlStudent = "UPDATE students SET birthday = ?, phone = ?, class_id = ? WHERE user_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlStudent)) {
                ps.setDate(1, birthdayVal != null ? Date.valueOf(birthdayVal) : null);
                ps.setString(2, phoneStr);
                ps.setInt(3, selectedClassObj.getId()); // class_id từ Class object
                ps.setInt(4, userId);
                ps.executeUpdate();
            }

            // Cập nhật bảng student_courses
            int studentId = getStudentIdByUserId(conn, userId);
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM student_courses WHERE student_id = ?")) {
                ps.setInt(1, studentId);
                ps.executeUpdate();
            }

            String insertSQL = "INSERT INTO student_courses (student_id, course_id) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertSQL)) {
                for (Course c : selectedCourses) {
                    ps.setInt(1, studentId);
                    ps.setInt(2, c.getId());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit();
            showAlert(Alert.AlertType.INFORMATION,"Update student successfully.");
            SceneSwitch.returnToView(actionEvent, "view/admin/studentManagement/student-view.fxml");


        } catch (SQLException e) {
            e.printStackTrace();
            showAlert( Alert.AlertType.ERROR,"Error update student: " + e.getMessage());
        }
    }

    private int getStudentIdByUserId(Connection conn, int userId) throws SQLException {
        String sql = "SELECT id FROM students WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("id");
            }
        }
        return -1;
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

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(type.name());
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void clearStudent(ActionEvent actionEvent) {
        fullName.clear();
        username.clear();
        email.clear();
        phone.clear();
        birthday.setValue(null);
        className.getSelectionModel().clearSelection();
        courseListView.getSelectionModel().clearSelection();
    }

    public void handleCancel(ActionEvent actionEvent) {
        SceneSwitch.returnToView(actionEvent, "view/admin/studentManagement/student-view.fxml");
    }
}
