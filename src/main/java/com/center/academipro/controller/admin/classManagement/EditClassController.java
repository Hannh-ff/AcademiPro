package com.center.academipro.controller.admin.classManagement;

import com.center.academipro.models.Class;
import com.center.academipro.models.Course;
import com.center.academipro.models.Teacher;
import com.center.academipro.utils.DBConnection;
import com.center.academipro.utils.SceneSwitch;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class EditClassController implements Initializable {

    @FXML public TextField txtClassName;
    @FXML public ComboBox<Teacher> teacherName;
    @FXML public ComboBox<Course> courseName;

    private int classId; // để lưu id của lớp đang sửa

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadCourses();
        loadTeachers();

    }

    private void loadTeachers() {
        String query = "SELECT t.id, u.fullname " +
                "FROM teachers t " +
                "JOIN users u ON t.user_id = u.id";
        try (Connection conn = DBConnection.getConn();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Teacher teacher = new Teacher();
                teacher.setId(rs.getInt("id"));
                teacher.setFullname(rs.getString("fullname"));
                teacherName.getItems().add(teacher);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadCourses() {
        try (Connection conn = DBConnection.getConn();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM courses")) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("course_name");
                Course c = new Course(id, name);
                courseName.getItems().add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setClass(Class classes){
        if (classes != null) {
            this.classId = classes.getId();
            txtClassName.setText(classes.getClassName());

            // Nếu bạn muốn chọn đúng course hiện tại (nếu đã có danh sách courses trong ComboBox)
            // Bạn cần load danh sách Course vào ComboBox trước khi setCourse.
            for (Course course : courseName.getItems()) {
                if (course.getCourseName().equals(classes.getCourseName())) {
                    courseName.setValue(course);
                    break;
                }
            }

            for (Teacher teacher : teacherName.getItems()) {
                if (teacher.getFullname().equals(classes.getTeacherName())) {
                    teacherName.setValue(teacher);
                    break;
                }
            }

        }

    }

    public void updateClass(ActionEvent actionEvent) {
        String newClassName = txtClassName.getText().trim();
        Teacher selectedTeacher = teacherName.getSelectionModel().getSelectedItem();
        Course selectedCourse = courseName.getSelectionModel().getSelectedItem();

        if (newClassName.isEmpty() || selectedCourse == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all required fields.");
            return;
        }

        try (Connection conn = DBConnection.getConn()) {
            int courseId = selectedCourse.getId();

            Integer teacherId = null;
            if (selectedTeacher != null) {
                teacherId = selectedTeacher.getId(); // Lấy trực tiếp ID của Teacher từ ComboBox
            }

            String updateQuery = "UPDATE classes SET class_name = ?, course_id = ?, teacher_id = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
                pstmt.setString(1, newClassName);
                pstmt.setInt(2, courseId);
                if (teacherId != null) {
                    pstmt.setInt(3, teacherId);
                } else {
                    pstmt.setNull(3, java.sql.Types.INTEGER);
                }
                pstmt.setInt(4, classId);

                int rowsUpdated = pstmt.executeUpdate();
                if (rowsUpdated > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Class updated successfully!");
                    SceneSwitch.returnToView(actionEvent, "view/admin/classManagement/class-management.fxml");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to update class.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Database error occurred.");
        }
    }

    public void clearForm(ActionEvent actionEvent) {
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
