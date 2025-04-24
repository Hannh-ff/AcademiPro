package com.center.academipro.controller.admin.studentManagement;

import com.center.academipro.models.Student;
import com.center.academipro.utils.DBConnection;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
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

    @FXML
    public void initialize() {
        studentId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()).asObject());
        studentName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFullName()));
        studentUser.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUsername()));
        studentEmail.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));
        studentBirth.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getBirthday()));
        studentPhone.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPhone()));
        studentCourse.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCourse()));
        studentClass.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getClassName()));

        setUpActionColumn();
        loadStudent();
    }

    private void loadStudent() {
        ObservableList<Student> studentList = FXCollections.observableArrayList();
        String sql = """
            SELECT s.id, u.fullname, u.username, u.email, s.birthday, s.phone, c.course_name, cl.class_name
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

        } catch (SQLException e) {
            e.printStackTrace();
        }

        tableView_Student.setItems(studentList);
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/center/academipro/view/admin/studentManagement/edit-student-view.fxml"));
            Parent root = loader.load();

            // Gửi dữ liệu teacher sang controller của update-teacher.fxml
            EditStudentController controller = loader.getController();
            controller.setStudent(student);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Update Teacher");
            stage.setScene(new Scene(root));
            stage.showAndWait();

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
    public void changeSceneAdd(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/center/academipro/view/admin/studentManagement/add-new-student.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) tableView_Student.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    };

}
