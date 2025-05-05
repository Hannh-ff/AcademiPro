package com.center.academipro.controller.student.assignment;

import com.center.academipro.session.SessionManager;
import com.center.academipro.utils.Alerts;
import com.center.academipro.utils.DBConnection;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class SubmitAssignmentController implements Initializable {
    @FXML private Label assignmentTitleLabel;
    @FXML private Label assignmentDescriptionLabel;
    @FXML private Label deadlineLabel;
    @FXML private Label fileNameLabel;
    @FXML private TextArea commentField;

    private int assignmentId;
    private File selectedFile;
    private Timestamp deadline;

    public void setAssignmentData(int assignmentId, String title, String description, Timestamp deadline) {
        this.assignmentId = assignmentId;
        this.deadline = deadline;
        assignmentTitleLabel.setText(title);
        assignmentDescriptionLabel.setText(description);
        deadlineLabel.setText("Hạn chót: " + deadline.toString());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // No action needed here
    }

    @FXML
    private void handleChooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn file bài làm");
        selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            fileNameLabel.setText(selectedFile.getName());
        } else {
            fileNameLabel.setText("Chưa chọn tệp");
        }
    }

    @FXML
    private void handleSubmit() {
        if (selectedFile == null) {
            Alerts.alertError("Error", "Please select a file to submit.");
            return;
        }

        String fileLink = selectedFile.getAbsolutePath(); // Hoặc upload lên server và lưu link
        String comment = commentField.getText();
        int userId  = SessionManager.getInstance().getUserId();
        String status = LocalDateTime.now().isAfter(deadline.toLocalDateTime()) ? "Done Late" : "Done";

        String sqlGetStudentId = "SELECT id FROM students WHERE user_id = ?";
        String sqlSubmit  = """
            INSERT INTO submissions (assignment_id, student_id, submitted_at, status, file_link, comment)
            VALUES (?, ?, CURRENT_TIMESTAMP, ?, ?, ?)
            ON DUPLICATE KEY UPDATE submitted_at=CURRENT_TIMESTAMP, status=?, file_link=?, comment=?
        """;

        try (Connection conn = DBConnection.getConnection()) {
            int studentId = -1;

            // Lấy student_id từ user_id
            try (PreparedStatement ps = conn.prepareStatement(sqlGetStudentId)) {
                ps.setInt(1, userId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    studentId = rs.getInt("id");
                } else {
                    Alerts.alertError("Error", "Student not found.");
                    return;
                }
            }

            // Thực hiện ghi dữ liệu nộp bài
            try (PreparedStatement stmt = conn.prepareStatement(sqlSubmit)) {
                stmt.setInt(1, assignmentId);
                stmt.setInt(2, studentId);
                stmt.setString(3, status);
                stmt.setString(4, fileLink);
                stmt.setString(5, comment);

                stmt.setString(6, status);
                stmt.setString(7, fileLink);
                stmt.setString(8, comment);

                stmt.executeUpdate();
                Alerts.alertInfo("Successfully", "The assignment has been submitted successfully.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Alerts.alertError("Error", "An error occurred while submitting the assignment.");
        }
    }

}
