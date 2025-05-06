package com.center.academipro.controller.student.classs;

import com.center.academipro.controller.student.EventDAO;
import com.center.academipro.models.Class;
import com.center.academipro.session.SessionCourse;
import com.center.academipro.session.SessionManager;
import com.center.academipro.utils.Alerts;
import com.center.academipro.utils.DBConnection;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class ListClassesController implements Initializable {

    @FXML
    private VBox classListVBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int courseId = SessionCourse.getCourseId(); // lấy từ Session
        List<Class> classes = EventDAO.getAllClassesByCourseId(courseId);
        renderClassCards(classes);

    }

    private int getStudentIdByUserId(int userId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT id FROM students WHERE user_id = ?")) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private boolean isEnrolled(int studentId, int courseId) {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE student_id = ? AND course_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, courseId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

//    private void renderClassCards(List<com.center.academipro.models.Class> classes) {
//        classListVBox.getChildren().clear();
//
//        for (com.center.academipro.models.Class c : classes) {
//            HBox card = new HBox(15);
//            card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 15;");
//            card.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.1)));
//            card.setAlignment(Pos.CENTER_LEFT);
//
//            VBox info = new VBox(5);
//            Label className = new Label("Class: " + c.getClassName());
//            className.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
//
//            Label teacherName = new Label("Teacher: " + c.getTeacherName());
//            teacherName.setStyle("-fx-text-fill: #666;");
//
//            Label courseName = new Label("Course: " + c.getCourseName());
//            courseName.setStyle("-fx-text-fill: #666;");
//
//            info.getChildren().addAll(className, teacherName, courseName);
//
//            Button joinBtn = new Button("Join Class");
//            joinBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 5;");
//            joinBtn.setOnAction(e -> {
//                int userId = SessionManager.getInstance().getUserId();
//                int studentId = getStudentIdByUserId(userId);
//
//                if (studentId == -1) {
//                    Alerts.alertError("Error", "Student ID not found.");
//                    return;
//                }
//
//                if (!isEnrolled(studentId, SessionCourse.getCourseId())) {
//                    Alerts.alertWarning("Error", "You must enroll in the course before joining a class.");
//                    return;
//                }
//
//                try (Connection conn = DBConnection.getConnection()) {
//
//                    // Kiểm tra đã tham gia lớp chưa
//                    String checkSql = "SELECT * FROM student_classes WHERE student_id = ? AND class_id = ?";
//                    PreparedStatement checkStmt = conn.prepareStatement(checkSql);
//                    checkStmt.setInt(1, studentId);
//                    checkStmt.setInt(2, c.getId()); // class id
//                    ResultSet rs = checkStmt.executeQuery();
//                    if (rs.next()) {
//                        Alerts.alertWarning("Already joined", "You have already joined this class.");
//                        return;
//                    }
//
//                    String sql = "INSERT INTO student_classes (student_id, class_id) VALUES (?, ?)";
//                    PreparedStatement stmt = conn.prepareStatement(sql);
//                    stmt.setInt(1, studentId);
//                    stmt.setInt(2, c.getId()); // Lấy classId từ object
//                    int result = stmt.executeUpdate();
//
//                    if (result > 0) {
//                        Alerts.alertSuccess("Success", "You have joined the class successfully!");
//                        joinBtn.setDisable(true); // Khóa nút để tránh join lại
//                    } else {
//                        Alerts.alertError("Failed", "Failed to join the class.");
//                    }
//                } catch (SQLException ex) {
//                    if (ex.getMessage().contains("Duplicate entry")) {
//                        Alerts.alertWarning("Already joined", "You have already joined this class.");
//                    } else {
//                        ex.printStackTrace();
//                        Alerts.alertError("Error", "An error occurred while joining the class.");
//                    }
//                }
//
//            });
//
//            HBox.setHgrow(info, Priority.ALWAYS);
//            HBox.setMargin(joinBtn, new Insets(0, 20, 0, 0));
//
//            card.getChildren().addAll(info, joinBtn);
//            classListVBox.getChildren().add(card);
//        }
//    }

    private void renderClassCards(List<com.center.academipro.models.Class> classes) {
        classListVBox.getChildren().clear();

        int userId = SessionManager.getInstance().getUserId();
        int studentId = getStudentIdByUserId(userId);
        int joinedClassId = -1;

        // Kiểm tra học sinh đã tham gia lớp nào trong khóa học chưa
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT class_id FROM student_classes sc " +
                             "JOIN classes c ON sc.class_id = c.id " +
                             "WHERE sc.student_id = ? AND c.course_id = ?"
             )) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, SessionCourse.getCourseId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                joinedClassId = rs.getInt("class_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (com.center.academipro.models.Class c : classes) {
            HBox card = new HBox(15);
            card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 15;");
            card.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.1)));
            card.setAlignment(Pos.CENTER_LEFT);

            VBox info = new VBox(5);
            Label className = new Label("Class: " + c.getClassName());
            className.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
            Label teacherName = new Label("Teacher: " + c.getTeacherName());
            teacherName.setStyle("-fx-text-fill: #666;");
            Label courseName = new Label("Course: " + c.getCourseName());
            courseName.setStyle("-fx-text-fill: #666;");
            info.getChildren().addAll(className, teacherName, courseName);

            Button joinBtn = new Button("Join Class");
            joinBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 5;");

            // Nếu đã tham gia lớp khác, disable các nút khác
            if (joinedClassId != -1 && joinedClassId != c.getId()) {
                joinBtn.setDisable(true);
            }

            // Nếu đã tham gia lớp này, disable luôn nút này
            if (joinedClassId == c.getId()) {
                joinBtn.setText("Already Joined");
                joinBtn.setDisable(true);
            }

            joinBtn.setOnAction(e -> {
                if (!isEnrolled(studentId, SessionCourse.getCourseId())) {
                    Alerts.alertWarning("Error", "You must enroll in the course before joining a class.");
                    return;
                }

                try (Connection conn = DBConnection.getConnection()) {
                    String sql = "INSERT INTO student_classes (student_id, class_id) VALUES (?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, studentId);
                    stmt.setInt(2, c.getId());
                    int result = stmt.executeUpdate();

                    if (result > 0) {
                        Alerts.alertSuccess("Success", "You have joined the class successfully!");
                        renderClassCards(classes); // Refresh lại UI để disable các nút khác
                    } else {
                        Alerts.alertError("Failed", "Failed to join the class.");
                    }
                } catch (SQLException ex) {
                    if (ex.getMessage().contains("Duplicate entry")) {
                        Alerts.alertWarning("Already joined", "You have already joined this class.");
                    } else {
                        ex.printStackTrace();
                        Alerts.alertError("Error", "An error occurred while joining the class.");
                    }
                }
            });

            HBox.setHgrow(info, Priority.ALWAYS);
            HBox.setMargin(joinBtn, new Insets(0, 20, 0, 0));

            card.getChildren().addAll(info, joinBtn);
            classListVBox.getChildren().add(card);
        }
    }

}
