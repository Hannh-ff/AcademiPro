package com.center.academipro.controller.admin.courseManagement;

import com.center.academipro.models.Course;
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
import java.sql.*;
import java.util.Optional;

public class CourseController {

    @FXML private TableView<Course> tableCourse;
    @FXML private TableColumn<Course, Integer> colCourseId;
    @FXML private TableColumn<Course, String> colCourseName;
    @FXML private TableColumn<Course, String> colDescription;
    @FXML private TableColumn<Course, String> colImage;
    @FXML private TableColumn<Course, Double> colPrice;
    @FXML private TableColumn<Course, Void> colAction;

    private final ObservableList<Course> courseList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colCourseId.setCellValueFactory(cell -> cell.getValue().idProperty().asObject());
        colCourseName.setCellValueFactory(cell -> cell.getValue().courseNameProperty());
        colDescription.setCellValueFactory(cell -> cell.getValue().descriptionProperty());
        colImage.setCellValueFactory(cell -> cell.getValue().imageProperty());
        colPrice.setCellValueFactory(cell -> cell.getValue().priceProperty().asObject());
        tableCourse.setItems(courseList);
        loadCoursesFromDatabase();
        addActionColumn();
    }

    public void reloadCourseTable() {
        courseList.clear();
        loadCoursesFromDatabase();
        tableCourse.refresh();
    }

    private void loadCoursesFromDatabase() {
        String query = "SELECT * FROM courses";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/academipro", "root", "");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Course course = new Course(
                        rs.getInt("id"),
                        rs.getString("course_name"),
                        rs.getString("description"),
                        rs.getString("image"),
                        rs.getDouble("price")
                );
                courseList.add(course);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải danh sách khóa học.");
        }
    }

    @FXML
    private void addCourse() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/center/academipro/view/admin/courseManagement/add-new-course.fxml"));
            Parent root = loader.load();

            AddCourseController controller = loader.getController();
            controller.setParentController(this);

            Stage stage = new Stage();
            stage.setTitle("Thêm khóa học");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể mở form thêm khóa học.");
        }
    }

    @FXML
    private void updateCourse() {
        Course selectedCourse = tableCourse.getSelectionModel().getSelectedItem();

        if (selectedCourse == null) {
            showAlert(Alert.AlertType.WARNING, "Chưa chọn khóa học", "Vui lòng chọn một khóa học để cập nhật.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/center/academipro/view/admin/courseManagement/edit-course.fxml"));
            Parent root = loader.load();

            EditCourseController controller = loader.getController();
            controller.setCourse(selectedCourse);
            controller.setParentController(this);

            Stage stage = new Stage();
            stage.setTitle("Cập nhật khóa học");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể mở form cập nhật khóa học.");
        }
    }

    @FXML
    private void deleteCourse() {
        Course selectedCourse = tableCourse.getSelectionModel().getSelectedItem();
        if (selectedCourse == null) {
            showAlert(Alert.AlertType.WARNING, "Chưa chọn khóa học", "Vui lòng chọn một khóa học để xóa.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc muốn xóa khóa học này?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Xác nhận xóa");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/academipro", "root", "");
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM courses WHERE id = ?")) {

                stmt.setInt(1, selectedCourse.getId());
                stmt.executeUpdate();
                courseList.remove(selectedCourse);

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa khóa học.");
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void addActionColumn() {
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("✏️");
            private final Button deleteBtn = new Button("🗑️");
            private final HBox actionBox = new HBox(10, editBtn, deleteBtn);

            {
                editBtn.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white;");
                deleteBtn.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white;");

                editBtn.setOnAction(event -> {
                    tableCourse.getSelectionModel().select(getIndex());
                    updateCourse();
                });

                deleteBtn.setOnAction(event -> {
                    tableCourse.getSelectionModel().select(getIndex());
                    deleteCourse();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(actionBox);
                }
            }
        });
    }
}
