package com.center.academipro.controller.admin.studentManagement;

import com.center.academipro.controller.admin.teacherManagement.EditTeacherController;
import com.center.academipro.models.Student;
import com.center.academipro.models.Teacher;
import com.center.academipro.utils.DBConnection;
import com.center.academipro.utils.SceneSwitch;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class StudentViewController {
    @FXML private TableView<Student> tableView_Student;
    @FXML private TableColumn<Student, Integer> studentId;
    @FXML private TableColumn<Student, String> studentName;
    @FXML private TableColumn<Student, String> studentUser;
    @FXML private TableColumn<Student, String> studentEmail;
    @FXML private TableColumn<Student, LocalDate> studentBirth;
    @FXML private TableColumn<Student, String> studentPhone;
    @FXML private TableColumn<Student, String> studentCourse;
    @FXML private TableColumn<Student, String> studentClass;
    @FXML private TableColumn<Student, Void> studentAction;
    @FXML private TextField searchField;
    @FXML private Label pageIndicator;

    private final ObservableList<Student> studentList = FXCollections.observableArrayList();
    private FilteredList<Student> filteredStudent;
    private static final int ITEMS_PER_PAGE = 4;
    private int currentPage = 0;

    @FXML
    public void initialize() {
        studentId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        studentName.setCellValueFactory(cellData -> cellData.getValue().fullNameProperty());
        studentUser.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        studentEmail.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        studentBirth.setCellValueFactory(cellData -> cellData.getValue().birthdayProperty());
        studentPhone.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        studentCourse.setCellValueFactory(cellData -> cellData.getValue().courseProperty());
        studentClass.setCellValueFactory(cellData-> cellData.getValue().classNameProperty());

        setUpActionColumn();
        loadStudent();
        setupSearchFilter();
    }

    private void loadStudent() {
        studentList.clear();
        
        String sql = """
            SELECT s.id, s.user_id, u.fullname, u.username, u.email, s.birthday, s.phone, c.course_name, cl.class_name
            FROM students s
            JOIN users u ON s.user_id = u.id
            JOIN student_classes sc ON sc.student_id = s.id
            JOIN classes cl ON sc.class_id = cl.id
            JOIN courses c ON cl.course_id = c.id
        """;

        try (Connection conn = DBConnection.getConn();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Student student = new Student(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("fullname"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getDate("birthday") != null ? rs.getDate("birthday").toLocalDate() : null,
                        rs.getString("phone"),
                        rs.getString("course_name"),
                        rs.getString("class_name")
                );
                studentList.add(student);
            }

            if (filteredStudent != null) {
                // Nếu filteredStudent đã khởi tạo rồi thì chỉ cần render lại
                currentPage = 0;
                renderPage(currentPage);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error loading students: " + e.getMessage(), Alert.AlertType.ERROR);
        }
        
    }

    private void setUpActionColumn() {
        studentAction.setCellFactory(param -> new TableCell<>() {
            private final Button updateBtn = new Button("Update");
            private final Button deleteBtn = new Button("Delete");
            private final HBox btnBox = new HBox(10, updateBtn, deleteBtn);

            {
                updateBtn.setOnAction(event -> {
                    Student student = getTableView().getItems().get(getIndex());
                    handleUpdate(student);
                });

                deleteBtn.setOnAction(event -> {
                    Student student = getTableView().getItems().get(getIndex());
                    handleDelete(student);
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

    private void handleUpdate(Student student) {
        if (student == null) {
            System.out.println("Student is null. Cannot open update view.");
            return;
        }

        try {
            FXMLLoader loader = SceneSwitch.loadView("view/admin/studentManagement/edit-student-view.fxml");
            if (loader != null) {
                EditStudentController controller = loader.getController();
                controller.setStudent(student); // Truyền dữ liệu ghế cần chỉnh sửa

                Parent newView = loader.getRoot();
                StackPane pane = (StackPane) tableView_Student.getScene().getRoot();
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
            loadStudent();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleDelete(Student student) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Confirmation");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete this student?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String getUserIdSql = "SELECT user_id FROM students WHERE id = ?";
                String deleteStudentClassesSql = "DELETE FROM student_classes WHERE student_id = ?";
                String deleteStudentSql = "DELETE FROM students WHERE id = ?";
                String deleteUserSql = "DELETE FROM users WHERE id = ?";

                try (Connection conn = DBConnection.getConn()) {
                    conn.setAutoCommit(false);
                    int userId = -1;

                    try (PreparedStatement getUserIdStmt = conn.prepareStatement(getUserIdSql)) {
                        getUserIdStmt.setInt(1, student.getId());
                        ResultSet rs = getUserIdStmt.executeQuery();
                        if (rs.next()) {
                            userId = rs.getInt("user_id");
                        } else {
                            throw new SQLException("User ID not found for student.");
                        }
                    }

                    try (PreparedStatement ps1 = conn.prepareStatement(deleteStudentClassesSql)) {
                        ps1.setInt(1, student.getId());
                        ps1.executeUpdate();
                    }

                    try (PreparedStatement ps2 = conn.prepareStatement(deleteStudentSql)) {
                        ps2.setInt(1, student.getId());
                        ps2.executeUpdate();
                    }

                    try (PreparedStatement ps3 = conn.prepareStatement(deleteUserSql)) {
                        ps3.setInt(1, userId);
                        ps3.executeUpdate();
                    }

                    conn.commit();
                    showAlert("Student and associated user deleted successfully.", Alert.AlertType.INFORMATION);
                    loadStudent(); // reload danh sách sau khi xoá

                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Error while deleting: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(type.name());
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void changeSceneAdd(ActionEvent event) {
        FXMLLoader loader = SceneSwitch.loadView("view/admin/studentManagement/add-new-student.fxml");
        if (loader != null) {
            Parent newView = loader.getRoot(); // Lấy Root từ FXMLLoader
            StackPane pane = (StackPane) ((Node) event.getSource()).getScene().getRoot();
            BorderPane mainPane = (BorderPane) pane.lookup("#mainBorderPane");
            mainPane.setCenter(newView); // Thay đổi nội dung của center
        } else {
            System.err.println("Failed to load addnew-user.fxml");
        }

    }

    public void handleReload(ActionEvent actionEvent) {
        loadStudent();
    }

    private void setupSearchFilter(){

        filteredStudent = new FilteredList<>(studentList, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Search: " + newValue);

            filteredStudent.setPredicate(student ->{
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                boolean match = student.getUsername().toLowerCase().contains(lowerCaseFilter);

                if (match) {
                    System.out.println("Matched: " + student.getUsername());
                }
                return match;
            });


            System.out.println("Filtered : " + filteredStudent.size());
            currentPage = 0;
            renderPage(currentPage);
        });

        renderPage(currentPage);
    }

    private void renderPage(int page) {
        int start = page * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, filteredStudent.size());

        // Lấy danh sách con (subList) cho trang hiện tại
        List<Student> pageStudents = filteredStudent.subList(start, end);
        // Cập nhật dữ liệu vào TableView
        tableView_Student.setItems(FXCollections.observableArrayList(pageStudents));
        setUpActionColumn();

        // Hiển thị log để kiểm tra dữ liệu hiển thị (có thể xóa sau khi hoàn tất)
        System.out.println("========== Page " + (page + 1) + " ==========");
        for (Student student : pageStudents) {
            System.out.println("Rendering course: " + student.getUsername());
        }

        int totalPages = (int) Math.ceil((double) filteredStudent.size() / ITEMS_PER_PAGE);
        if (totalPages == 0) {
            totalPages = 1; // Đảm bảo luôn hiển thị ít nhất "Page 1 / 1"
        }
        pageIndicator.setText("Page " + (currentPage + 1) + " / " + totalPages);
    }

    @FXML
    private void handleNextPage() {
        if ((currentPage + 1) * ITEMS_PER_PAGE < filteredStudent.size()) {
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
