package com.center.academipro.controller.admin.classManagement;

import com.center.academipro.models.Class;
import com.center.academipro.models.Course;
import com.center.academipro.models.Teacher;
import com.center.academipro.utils.DBConnection;
import com.center.academipro.utils.SceneSwitch;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;

public class AddClassController {

    @FXML
    private TextField txtClassName;
    @FXML
    private ComboBox<Teacher> teacherName;
    @FXML
    private ComboBox<Course> courseName;

    @FXML
    public void initialize() {
        loadCourse();
        loadTeacher();
    }

    public void loadCourse() {
        String sql = "SELECT id, course_name FROM courses";

        try (Connection conn = DBConnection.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("course_name");
                this.courseName.getItems().add(new Course(id, name)); // Thêm object Course(id, name)
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadTeacher() {
        String sql = "SELECT id, fullname FROM users WHERE role = 'Teacher'";

        try (Connection conn = DBConnection.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("fullname");
                this.teacherName.getItems().add(new Teacher(id, name)); // Thêm object Teacher(id, name)
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addClass(ActionEvent actionEvent) {
        // Lấy thông tin từ các trường đầu vào
        String className = txtClassName.getText().trim();
        String selectedTeacherName = teacherName.getValue() != null ? teacherName.getValue().getFullname() : null;
        String selectedCourseName = courseName.getValue() != null ? courseName.getValue().getCourseName() : null;

        // Kiểm tra xem các trường thông tin có đầy đủ không
        if (className.isEmpty() || selectedCourseName == null || selectedTeacherName == null || selectedTeacherName.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all required fields.");
            return;
        }

        try (Connection conn = DBConnection.getConn()) {
            // Lấy ID của khóa học
            int courseId = courseName.getValue().getId();
            // Lấy ID của giáo viên từ bảng users
            int teacherId = getTeacherIdByName(conn, selectedTeacherName);

            if (teacherId == -1) {
                showAlert(Alert.AlertType.ERROR, "Error", "Teacher not found or not valid.");
                return;
            }

            // Kiểm tra trùng tên lớp
            if (isClassNameExists(conn, className)) {
                showAlert(Alert.AlertType.ERROR, "Error", "Class name already exists.");
                return;
            }

            // Thêm lớp mới vào bảng classes
            if (insertNewClass(conn, className, courseId, teacherId)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Class added successfully!");
                clearForm();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add class.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Database error occurred.");
        }
    }

    // ✅ Hàm kiểm tra tên lớp đã tồn tại hay chưa
    private boolean isClassNameExists(Connection conn, String className) throws SQLException {
        String query = "SELECT COUNT(*) FROM classes WHERE class_name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, className);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Lấy teacher_id từ fullname của giáo viên.
     */
    private int getTeacherIdByName(Connection conn, String teacherName) throws SQLException {
        String query = "SELECT id FROM users WHERE fullname = ? AND role = 'Teacher'";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, teacherName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
                return -1; // Trả về -1 nếu không tìm thấy giáo viên
            }
        }
    }

    /**
     * Thêm lớp mới vào bảng classes.
     */
    private boolean insertNewClass(Connection conn, String className, int courseId, int teacherId) throws SQLException {
        String insertQuery = "INSERT INTO classes (class_name, course_id, teacher_id) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
            pstmt.setString(1, className);
            pstmt.setInt(2, courseId);
            if (teacherId != -1) {
                pstmt.setInt(3, teacherId);
            } else {
                pstmt.setNull(3, java.sql.Types.INTEGER);
            }

            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0;
        }
    }

    public void clearForm() {
        txtClassName.clear();
        teacherName.getSelectionModel().clearSelection();
        courseName.getSelectionModel().clearSelection();
    }

    public void handleCancel(ActionEvent actionEvent) {
        SceneSwitch.returnToView(actionEvent, "view/admin/classManagement/class-management.fxml");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
