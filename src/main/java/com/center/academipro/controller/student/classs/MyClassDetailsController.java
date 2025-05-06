package com.center.academipro.controller.student.classs;

import com.center.academipro.controller.student.assignment.SubmitAssignmentController;
import com.center.academipro.session.SessionCourse;
import com.center.academipro.session.SessionManager;
import com.center.academipro.utils.DBConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

public class MyClassDetailsController implements Initializable {


    @FXML
    public Label courseNameLabel;
    @FXML
    public Label classNameLabel;
    @FXML
    public Label teacherNameLabel;
    @FXML
    public VBox assignmentListVBox;

    private int classId = SessionCourse.getClassId();

    private int studentId = SessionManager.getInstance().getUserId();

    public void setClassAndStudentId(int classId, int studentId) {
        this.classId = classId;
        this.studentId = studentId;

        System.out.println("Đã gán classId: " + classId + ", studentId: " + studentId);

        // GỌI SAU KHI GÁN
        loadClassInfo();
        loadAssignments();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    private void loadClassInfo() {

        String sql = "SELECT c.class_name, u.fullname AS teacher_name, co.course_name \n" +
                "FROM classes c \n" +
                "JOIN users u ON c.teacher_id = u.id \n" +
                "JOIN courses co ON c.course_id = co.id \n" +
                "WHERE c.id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, classId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                courseNameLabel.setText("Course: " + rs.getString("course_name"));
                classNameLabel.setText("Class: " + rs.getString("class_name"));
                teacherNameLabel.setText("Teacher: " + rs.getString("teacher_name"));

                System.out.println("Thông tin lớp học đã được truy xuất từ DB:");
                System.out.println("- Tên lớp: " + rs.getString("class_name"));
                System.out.println("- Giáo viên: " + rs.getString("teacher_name"));
                System.out.println("- Khóa học: " + rs.getString("course_name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadAssignments() {
        String sql = "SELECT a.id, a.title, a.description, a.deadline, \n" +
                "               s.status \n" +
                "        FROM assignments a\n" +
                "        LEFT JOIN submissions s ON a.id = s.assignment_id AND s.student_id = ?\n" +
                "        WHERE a.class_id = ?\n" +
                "        ORDER BY a.deadline ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, classId);
            ResultSet rs = stmt.executeQuery();

            assignmentListVBox.getChildren().clear();
            int count = 0;

            while (rs.next()) {
                count++;
                System.out.println("Tìm thấy bài tập #" + count + ": " + rs.getString("title"));
                HBox assignmentItem = createAssignmentItem(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getTimestamp("deadline"),
                        rs.getString("status")
                );
                assignmentListVBox.getChildren().add(assignmentItem);
            }

            if (count == 0) {
                System.out.println("Không có bài tập nào được tìm thấy cho classId = " + classId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private HBox createAssignmentItem(int assignmentId, String title, String description, Timestamp deadline, String status) {
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label descLabel = new Label(description);
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(500);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String formattedDeadline = sdf.format(deadline);
        Label deadlineLabel = new Label("Deadline: " + formattedDeadline);
        deadlineLabel.setStyle("-fx-text-fill: #e84118;");

        Label statusLabel = new Label(status != null ? "Status: " + status : "Status: Not Submit");
        statusLabel.setStyle("-fx-text-fill: #0097e6; -fx-font-style: italic;");

        VBox infoBox = new VBox(titleLabel, descLabel, deadlineLabel, statusLabel);
        infoBox.setSpacing(5);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        Button submitBtn = new Button("Submit");
        submitBtn.setStyle("-fx-background-color: #00a8ff; -fx-text-fill: white; -fx-padding: 8 16; -fx-background-radius: 6;");
        submitBtn.setOnAction(e -> handleSubmitAssignment(assignmentId));

        HBox itemBox = new HBox(infoBox, submitBtn);
        itemBox.setSpacing(20);
        itemBox.setPadding(new Insets(15));
        itemBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 3);");

        return itemBox;
    }

    private void handleSubmitAssignment(int assignmentId) {
        System.out.println("Mở form nộp bài cho assignmentId = " + assignmentId);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/center/academipro/view/student/assignment/submit-assignment.fxml"));
            Parent root = loader.load();

            // Truyền dữ liệu cho controller
            SubmitAssignmentController controller = loader.getController();
            controller.setAssignmentData(assignmentId, "Đang tải...", "Mô tả...", Timestamp.valueOf("2000-01-01 00:00:00"));

            // Lấy thông tin thật từ DB
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT * FROM assignments WHERE id = ?")) {
                stmt.setInt(1, assignmentId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    System.out.println("Đã truy xuất chi tiết bài tập từ DB:");
                    System.out.println("- Tiêu đề: " + rs.getString("title"));
                    controller.setAssignmentData(
                            assignmentId,
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getTimestamp("deadline")
                    );
                }
            }
            Stage stage = new Stage();
            stage.setTitle("Submit Assignment");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }


}
