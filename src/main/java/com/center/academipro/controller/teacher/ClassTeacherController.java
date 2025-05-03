package com.center.academipro.controller.teacher;

import com.center.academipro.models.Class;
import com.center.academipro.models.Submission;
import com.center.academipro.session.SessionManager;
import com.center.academipro.utils.DBConnection;
import com.center.academipro.utils.SceneSwitch;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ClassTeacherController {
    @FXML
    private TableColumn<Submission, String> CommentCol;
    @FXML
    private TableColumn<Submission, Integer> idCol;
    @FXML
    private TableColumn<Submission, String> assignmentCol;

    @FXML
    private ComboBox<Class> className;

    @FXML
    private TableColumn<Submission, String> fileCol;

    @FXML
    private TableColumn<Submission, String> statusCol;

    @FXML
    private TableColumn<Submission, String> studentCol;

    @FXML
    private TableColumn<Submission, String> submitDateCol;

    @FXML
    private TableView<Submission> tableViewSubmit;

    private  int teacherId = SessionManager.getInstance().getUserId();
    @FXML
    public void initialize() {
        loadClassList();
        setupTableColumns();
        loadSubmissionData();
//        thêm listener  cho className để lắng nghe khi người dùng chọn ớp học
        className.setOnAction(event -> {
            Class selectedClass = className.getValue();
            if (selectedClass != null) {
                loadSubmissionDataByClassId(selectedClass.getId());
            }
        });
    }
    private void loadClassList() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT id, class_name FROM classes WHERE teacher_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, teacherId);
            var rs = stmt.executeQuery();
            while (rs.next()) {
                int classId = rs.getInt("id");
                String classNameValue = rs.getString("class_name");
                Class c = new Class(classId, classNameValue);
                className.getItems().add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupTableColumns() {
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        studentCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStudentName()));
        assignmentCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getAssignmentTitle()));
        submitDateCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getSubmittedAt()));
        fileCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFileLink()));
        statusCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus()));
        CommentCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getComment()));
    }

    private void loadSubmissionData() {
        ObservableList<Submission> submissions = FXCollections.observableArrayList();

        String sql = """
                SELECT s.id, u.fullname, a.title AS assignment_title, 
                       s.submitted_at, s.file_link, s.status, s.comment
                FROM submissions s
                JOIN students st ON s.student_id = st.id
                JOIN users u ON st.user_id = u.id
                JOIN assignments a ON s.assignment_id = a.id
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                submissions.add(new Submission(
                        rs.getInt("id"),
                        rs.getString("fullname"),
                        rs.getString("assignment_title"),
                        rs.getString("submitted_at"),
                        rs.getString("file_link"),
                        rs.getString("status"),
                        rs.getString("comment")
                ));
            }

            tableViewSubmit.setItems(submissions);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//    lọc bài học đã nộp dựa trên lớp dược chọn
    private void loadSubmissionDataByClassId(int classId) {
        ObservableList<Submission> submissions = FXCollections.observableArrayList();

        String sql = """
            SELECT s.id, u.fullname, a.title AS assignment_title, 
                   s.submitted_at, s.file_link, s.status, s.comment
            FROM submissions s
            LEFT JOIN students st ON s.student_id = st.id
            LEFT JOIN users u ON st.user_id = u.id
            LEFT JOIN assignments a ON s.assignment_id = a.id
            WHERE a.class_id = ?
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, classId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                submissions.add(new Submission(
                        rs.getInt("id"),
                        rs.getString("fullname"),
                        rs.getString("assignment_title"),
                        rs.getString("submitted_at"),
                        rs.getString("file_link"),
                        rs.getString("status"),
                        rs.getString("comment")
                ));
            }

            tableViewSubmit.setItems(submissions);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeSceneAssignment(ActionEvent event) {
        FXMLLoader loader = SceneSwitch.loadView("view/teacher/assignment-view.fxml");
        if (loader != null) {
            Parent newView = loader.getRoot(); // Lấy Root từ FXMLLoader
            StackPane pane = (StackPane) ((Node) event.getSource()).getScene().getRoot();
            BorderPane mainPane = (BorderPane) pane.lookup("#mainBorderPane");
            mainPane.setCenter(newView); // Thay đổi nội dung của center
        } else {
            System.err.println("Failed to load addnew-user.fxml");
        }
    }

}

