package com.center.academipro.controller.admin.teacherManagement;

import com.center.academipro.models.Course;
import com.center.academipro.models.Teacher;
import com.center.academipro.utils.DBConnection;
import com.center.academipro.utils.SceneSwitch;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.collections.ObservableList;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class EditTeacherController implements Initializable {

    @FXML
    private TextField fullName;
    @FXML
    private TextField username;
    @FXML
    private TextField email;
    @FXML
    private DatePicker birthday;
    @FXML
    private TextField phone;
    @FXML
    private ListView<Course> courseListView; // hoặc ListView<Course> nếu có custom cell

    private int userId; // ID của user đang được chỉnh sửa

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        courseListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    // Hàm khởi tạo dữ liệu giáo viên
    public void setTeacher(Teacher teacher) {
        if (teacher == null) return;

        this.userId = teacher.getUserId();
        fullName.setText(teacher.getFullname());
        username.setText(teacher.getUsername());
        email.setText(teacher.getEmail());
        birthday.setValue(teacher.getBirthday());
        phone.setText(teacher.getPhone());

        // Gán danh sách khóa học đang có trong hệ thống
        loadCourses();

        // Chọn các khóa học mà giáo viên đang dạy
        ObservableList<Course> selectedCourses = teacher.getCourseList();
        for (Course course : selectedCourses) {
            for (Course item : courseListView.getItems()) {
                if (item.getId() == course.getId()) {
                    courseListView.getSelectionModel().select(item);
                    break;
                }
            }
        }
    }

    @FXML
    private void updateTeacher(ActionEvent actionEvent) {
        String fullnameStr = fullName.getText();
        String usernameStr = username.getText();
        String emailStr = email.getText();
        LocalDate birthdayDate = birthday.getValue();
        String phoneStr = phone.getText();

        ObservableList<Course> selectedCourses = courseListView.getSelectionModel().getSelectedItems(); // hoặc getItems nếu bạn muốn update toàn bộ

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Begin transaction

            // 0. Check if email already exists and is used by another user
            String checkEmailSQL = "SELECT id FROM users WHERE email = ? AND id != ?";
            boolean emailExists = false;
            try (PreparedStatement checkPs = conn.prepareStatement(checkEmailSQL)) {
                checkPs.setString(1, emailStr);
                checkPs.setInt(2, userId); // chính xác phải khác ID hiện tại
                try (ResultSet rs = checkPs.executeQuery()) {
                    if (rs.next()) {
                        int existingId = rs.getInt("id");
                        System.out.println("Trùng email với userId: " + existingId);
                        emailExists = true;
                    }
                }
            }
            if (emailExists) {
                showAlert("Email is already in use by another user!", Alert.AlertType.ERROR);
                return;
            }

            // 1. Update user table
            String sqlUser = "UPDATE users SET fullname = ?, username = ?, email = ? WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlUser)) {
                ps.setString(1, fullnameStr);
                ps.setString(2, usernameStr);
                ps.setString(3, emailStr);
                ps.setInt(4, userId);
                ps.executeUpdate();
            }

            // 2. Update teacher table
            String sqlTeacher = "UPDATE teachers SET birthday = ?, phone = ? WHERE user_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlTeacher)) {
                ps.setDate(1, birthdayDate != null ? Date.valueOf(birthdayDate) : null);
                ps.setString(2, phoneStr);
                ps.setInt(3, userId);
                ps.executeUpdate();
            }

            // 3. Update teacher_courses
            // First delete old course mappings
            int teacherId = getTeacherIdByUserId(conn, userId);
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM teacher_courses WHERE teacher_id = ?")) {
                ps.setInt(1, teacherId);
                ps.executeUpdate();
            }

            // Then insert new course mappings
            String insertCourseSQL = "INSERT INTO teacher_courses (teacher_id, course_id) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertCourseSQL)) {
                for (Course courseName : selectedCourses) {
                    int courseId = getCourseIdByName(conn, String.valueOf(courseName));
                    if (courseId != -1) {
                        ps.setInt(1, teacherId);
                        ps.setInt(2, courseId);
                        ps.addBatch();
                    }
                }
                ps.executeBatch();
            }

            conn.commit(); // Commit all updates
            showAlert("Teacher updated successfully!", Alert.AlertType.INFORMATION);
            SceneSwitch.returnToView(actionEvent, "view/admin/teacherManagement/teacher-view.fxml");

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Failed to update teacher: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private int getTeacherIdByUserId(Connection conn, int userId) throws SQLException {
        String sql = "SELECT id FROM teachers WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("id");
            }
        }
        return -1;
    }

    private int getCourseIdByName(Connection conn, String name) throws SQLException {
        String sql = "SELECT id FROM courses WHERE course_name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("id");
            }
        }
        return -1;
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
            showAlert("Could not load course list.", Alert.AlertType.ERROR);
        }
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

    public void handleCancel(ActionEvent actionEvent) {
        SceneSwitch.returnToView(actionEvent, "view/admin/teacherManagement/teacher-view.fxml");
    }
}
