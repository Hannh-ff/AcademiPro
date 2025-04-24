package com.center.academipro.controller.admin.teacherManagement;

import com.center.academipro.models.Teacher;
import com.center.academipro.utils.DBConnection;
import com.center.academipro.utils.SceneSwitch;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;

public class TeacherViewController {
    @FXML
    private TableView<Teacher> tableView_Teacher;
    @FXML
    private TableColumn<Teacher, Integer> teacherId;
    @FXML
    private TableColumn<Teacher, String> teacherName;
    @FXML
    private TableColumn<Teacher, String> teacherUser;
    @FXML
    private TableColumn<Teacher, String> teacherEmail;
    @FXML
    private TableColumn<Teacher, LocalDate> teacherBirth;
    @FXML
    private TableColumn<Teacher, String> teacherPhone;
    @FXML
    private TableColumn<Teacher, String> teacherCourse;
    @FXML
    private TableColumn<Teacher, Void> teacherAction;

//    private Connection connect;
//    private PreparedStatement prepare;
//    private ResultSet result;

    private final ObservableList<Teacher> teachersList = FXCollections.observableArrayList();


    @FXML
    public void initialize() {
        teacherId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        teacherName.setCellValueFactory(cellData -> cellData.getValue().fullnameProperty());
        teacherUser.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        teacherEmail.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        teacherBirth.setCellValueFactory(cellData -> cellData.getValue().birthdayProperty());
        teacherPhone.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        teacherCourse.setCellValueFactory(cellData -> cellData.getValue().coursesProperty());

        setUpActionColumn();
        loadTeachers();
    }

    private void loadTeachers() {
        ObservableList<Teacher> teacherList = FXCollections.observableArrayList();

        String query = """
                    SELECT t.id, t.user_id, u.fullname, u.username, u.email, t.birthday, t.phone,
                                        GROUP_CONCAT(c.course_name SEPARATOR ', ') AS courses
                                 FROM teachers t
                                 JOIN users u ON t.user_id = u.id
                                 LEFT JOIN teacher_courses tc ON t.id = tc.teacher_id
                                 LEFT JOIN courses c ON tc.course_id = c.id
                                 WHERE u.role = 'Teacher'
                                 GROUP BY t.id
                """;

        try (Connection conn = DBConnection.getConn();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Teacher teacher = new Teacher(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("fullname"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getDate("birthday") != null ? rs.getDate("birthday").toLocalDate() : null,
                        rs.getString("phone"),
                        rs.getString("courses") != null ? rs.getString("courses") : ""
                );
                teacherList.add(teacher);
            }

            tableView_Teacher.setItems(teacherList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void setUpActionColumn() {
        teacherAction.setCellFactory(param -> new TableCell<>() {
            private final Button updateBtn = new Button("Update");
            private final Button deleteBtn = new Button("Delete");
            private final HBox btnBox = new HBox(10, updateBtn, deleteBtn);

            {
                updateBtn.setOnAction(event -> {
                    Teacher teacher = getTableView().getItems().get(getIndex());
                    handleUpdate(teacher);
                });

                deleteBtn.setOnAction(event -> {
                    Teacher teacher = getTableView().getItems().get(getIndex());
                    handleDelete(teacher);
                });

                updateBtn.setStyle("-fx-background-color: linear-gradient(to bottom right, #a18cd1, #fbc2eb); -fx-text-fill: white;");
                deleteBtn.setStyle("-fx-background-color: linear-gradient(to bottom right, #ff1e56, #ff4b2b); -fx-text-fill: white;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnBox);
                }
            }
        });
    }

    private void handleUpdate(Teacher teacher) {
        if (teacher == null) {
            System.out.println("Teacher is null. Cannot open update view.");
            return;
        }

        try {
            FXMLLoader loader = SceneSwitch.loadView("view/admin/teacherManagement/update-teacher-view.fxml");
            if (loader != null) {
                EditTeacherController controller = loader.getController();
                controller.setTeacher(teacher); // Truyền dữ liệu ghế cần chỉnh sửa

                Parent newView = loader.getRoot();
                StackPane pane = (StackPane) tableView_Teacher.getScene().getRoot();
                BorderPane mainPane = (BorderPane) pane.lookup("#mainBorderPane");

                if (mainPane != null) {
                    mainPane.setCenter(newView);
                } else {
                    System.err.println("BorderPane with ID 'mainBorderPane' not found");
                }
            } else {
                System.err.println("Could not load edit-seat.fxml");
            }

            // Sau khi đóng form update, reload lại danh sách
            loadTeachers();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleDelete(Teacher teacher)  {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Confirmation");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete this teacher?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String getUserIdSql = "SELECT user_id FROM teachers WHERE id = ?";
                String deleteTeacherCoursesSql = "DELETE FROM teacher_courses WHERE teacher_id = ?";
                String deleteTeacherSql = "DELETE FROM teachers WHERE id = ?";
                String deleteUserSql = "DELETE FROM users WHERE id = ?";

                try (Connection conn = DBConnection.getConn()) {
                    conn.setAutoCommit(false);
                    int userId = -1;

                    try (PreparedStatement getUserIdStmt = conn.prepareStatement(getUserIdSql)) {
                        getUserIdStmt.setInt(1, teacher.getId());
                        ResultSet rs = getUserIdStmt.executeQuery();
                        if (rs.next()) {
                            userId = rs.getInt("user_id");
                        } else {
                            throw new SQLException("User ID not found for teacher.");
                        }
                    }

                    try (PreparedStatement ps1 = conn.prepareStatement(deleteTeacherCoursesSql)) {
                        ps1.setInt(1, teacher.getId());
                        ps1.executeUpdate();
                    }

                    try (PreparedStatement ps2 = conn.prepareStatement(deleteTeacherSql)) {
                        ps2.setInt(1, teacher.getId());
                        ps2.executeUpdate();
                    }

                    try (PreparedStatement ps3 = conn.prepareStatement(deleteUserSql)) {
                        ps3.setInt(1, userId);
                        ps3.executeUpdate();
                    }

                    conn.commit();
                    showAlert("Teacher and associated user deleted successfully.", Alert.AlertType.INFORMATION);
                    loadTeachers(); // reload sau khi xóa

                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Error while deleting: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }


    public void changeSceneAdd(ActionEvent event) {
        FXMLLoader loader = SceneSwitch.loadView("view/admin/teacherManagement/add-new-teacher.fxml");
        if (loader != null) {
            Parent newView = loader.getRoot(); // Lấy Root từ FXMLLoader
            StackPane pane = (StackPane) ((Node) event.getSource()).getScene().getRoot();
            BorderPane mainPane = (BorderPane) pane.lookup("#mainBorderPane");
            mainPane.setCenter(newView); // Thay đổi nội dung của center
        } else {
            System.err.println("Failed to load addnew-user.fxml");
        }

    }

    ;

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(type.name());
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public void handleReload(ActionEvent actionEvent) {
        loadTeachers();
    }
}
