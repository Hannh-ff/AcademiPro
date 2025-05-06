package com.center.academipro.controller.admin.teacherManagement;

import com.center.academipro.models.Course;
import com.center.academipro.models.Teacher;
import com.center.academipro.utils.DBConnection;
import com.center.academipro.utils.SceneSwitch;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
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
import java.util.List;
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
    @FXML private TextField searchField;
    @FXML private Label pageIndicator;
    
    private final ObservableList<Teacher> teacherList = FXCollections.observableArrayList();
    private FilteredList<Teacher> filteredTeacher;
    private static final int ITEMS_PER_PAGE = 4;
    private int currentPage = 0;


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
        setupSearchFilter();
        tableView_Teacher.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadTeachers() {
        teacherList.clear();

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

            if (filteredTeacher != null) {
                // Nếu filteredTeacher đã khởi tạo rồi thì chỉ cần render lại
                currentPage = 0;
                renderPage(currentPage);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error loading teachers: " + e.getMessage(), Alert.AlertType.ERROR);
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
                btnBox.setAlignment(Pos.CENTER);
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

    private void handleDelete(Teacher teacher) {
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

    private void setupSearchFilter(){

        filteredTeacher = new FilteredList<>(teacherList, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Search: " + newValue);

            filteredTeacher.setPredicate(teacher ->{
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                boolean match = teacher.getUsername().toLowerCase().contains(lowerCaseFilter);

                if (match) {
                    System.out.println("Matched: " + teacher.getUsername());
                }
                return match;
            });


            System.out.println("Filtered : " + filteredTeacher.size());
            currentPage = 0;
            renderPage(currentPage);
        });

        renderPage(currentPage);
    }

    private void renderPage(int page) {
        int start = page * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, filteredTeacher.size());

        // Lấy danh sách con (subList) cho trang hiện tại
        List<Teacher> pageTeachers = filteredTeacher.subList(start, end);
        // Cập nhật dữ liệu vào TableView
        tableView_Teacher.setItems(FXCollections.observableArrayList(pageTeachers));
        setUpActionColumn();

        // Hiển thị log để kiểm tra dữ liệu hiển thị (có thể xóa sau khi hoàn tất)
        System.out.println("========== Page " + (page + 1) + " ==========");
        for (Teacher teacher : pageTeachers) {
            System.out.println("Rendering course: " + teacher.getUsername());
        }

        int totalPages = (int) Math.ceil((double) filteredTeacher.size() / ITEMS_PER_PAGE);
        if (totalPages == 0) {
            totalPages = 1; // Đảm bảo luôn hiển thị ít nhất "Page 1 / 1"
        }
        pageIndicator.setText("Page " + (currentPage + 1) + " / " + totalPages);
    }

    @FXML
    private void handleNextPage() {
        if ((currentPage + 1) * ITEMS_PER_PAGE < filteredTeacher.size()) {
            currentPage++;
            renderPage(currentPage);
        }
    }

    @FXML
    private void handlePrevPage() {
        if (currentPage > 0) {
            currentPage--;
            renderPage(currentPage);
        }
    }
}
