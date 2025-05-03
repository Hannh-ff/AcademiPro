package com.center.academipro.controller.teacher;

import com.center.academipro.models.Class;
import com.center.academipro.session.SessionManager;
import com.center.academipro.utils.DBConnection;
import com.center.academipro.models.Assignment;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class AssignmentController {

    @FXML
    private TextField title;

    @FXML
    private TextArea description;

    @FXML
    private DatePicker deadline;

    @FXML
    private ComboBox<Class> classCombo;

    private int teacherId = SessionManager.getInstance().getUserId();

    @FXML
    private void initialize() {
        loadClassList();
        System.out.println("Teacher ID: " + teacherId);
    }

    private void loadClassList() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT id, class_name FROM classes WHERE teacher_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, teacherId);
            var rs = stmt.executeQuery();
            while (rs.next()) {
                int classId = rs.getInt("id");
                String className = rs.getString("class_name");
                Class c = new Class(classId, className);
                classCombo.getItems().add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void handleAddAssignment() {
        String titleText = title.getText();
        String desc = description.getText();
        LocalDate localDate = deadline.getValue();
        Class selectedClass = classCombo.getValue();

        if (titleText.isEmpty() || localDate == null || selectedClass == null) {
            showAlert(Alert.AlertType.ERROR, "Please fill all required fields.");
            return;
        }

        int classId = selectedClass.getId();
        Date deadlineDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        int userId = SessionManager.getInstance().getUserId();
        Integer teacherId = getTeacherIdFromUserId(userId);

        if (teacherId == null) {
            showAlert(Alert.AlertType.ERROR, "Cannot find teacher information for current user.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO assignments (class_id, teacher_id, title, description, deadline) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, classId);
            stmt.setInt(2, teacherId);
            stmt.setString(3, titleText);
            stmt.setString(4, desc);
            stmt.setTimestamp(5, new java.sql.Timestamp(deadlineDate.getTime()));

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Assignment added successfully!");
                clearForm();
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed to add assignment.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database error: " + e.getMessage());
        }
    }

    public void clearForm() {
        title.clear();
        description.clear();
        deadline.setValue(null);
        classCombo.setValue(null);
    }
    private Integer getTeacherIdFromUserId(int userId) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT id FROM teachers WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(type.name());
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
