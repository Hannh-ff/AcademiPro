package com.center.academipro.controller.teacher;

import com.center.academipro.models.Assignment;
import com.center.academipro.utils.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AssignmentController {
    @FXML private ComboBox<String> classCombox;
    @FXML private TextField title;
    @FXML private TextArea description;
    @FXML private DatePicker deadline;

    @FXML
    public void initialize() {
        loadClasses();
    }

    private void loadClasses() {
        try (Connection conn = DBConnection.getConn();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT class_name FROM classes ORDER BY class_name")) {

            ResultSet rs = stmt.executeQuery();
            classCombox.getItems().clear();
            while (rs.next()) {
                classCombox.getItems().add(rs.getString("class_name"));
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load classes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddAssignment() {
        String selectedClass = classCombox.getValue();
        String assignmentTitle = title.getText();
        String assignmentDesc = description.getText();
        LocalDate dueDate = deadline.getValue();

        if (selectedClass == null || assignmentTitle.isEmpty() || dueDate == null) {
            showAlert("Validation Error", "Please fill all required fields");
            return;
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConn();
            conn.setAutoCommit(false); // Bắt đầu transaction

            // 1. Lấy class_id
            int classId = getClassId(conn, selectedClass);
            int teacherId = getCurrentTeacherId();

            // 2. Tạo assignment
            String query = "INSERT INTO assignments (class_id, teacher_id, title, description, deadline) " +
                    "VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, classId);
                stmt.setInt(2, teacherId);
                stmt.setString(3, assignmentTitle);
                stmt.setString(4, assignmentDesc);
                stmt.setTimestamp(5, Timestamp.valueOf(dueDate.atStartOfDay()));

                int affectedRows = stmt.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Creating assignment failed, no rows affected.");
                }

                // 3. Tạo bản ghi submissions cho tất cả sinh viên
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int assignmentId = generatedKeys.getInt(1);
                        initializeSubmissions(conn, assignmentId, classId);
                    } else {
                        throw new SQLException("Creating assignment failed, no ID obtained.");
                    }
                }

                conn.commit(); // Commit transaction nếu thành công
                showAlert("Success", "Assignment created successfully");
                clearForm();
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback nếu có lỗi
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            showAlert("Database Error", "Failed to create assignment: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset auto-commit
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private int getClassId(Connection conn, String className) throws SQLException {
        String sql = "SELECT id FROM classes WHERE class_name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, className);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    throw new SQLException("Class not found: " + className);
                }
            }
        }
    }
    private void initializeSubmissions(Connection conn, int assignmentId, int classId) throws SQLException {
        String sql = "INSERT INTO submissions (assignment_id, student_id, status) " +
                "SELECT ?, s.user_id, 'Not Submitted' " +
                "FROM students s " +
                "JOIN student_classes sc ON s.user_id = sc.student_id " +
                "WHERE sc.class_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, assignmentId);
            stmt.setInt(2, classId);
            stmt.executeUpdate();
        }
    }
    private int getCurrentTeacherId() {
        // Implement your authentication logic here
        return 1; // Temporary - replace with actual teacher ID from session
    }

    private void clearForm() {
        title.clear();
        description.clear();
        deadline.setValue(null);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}