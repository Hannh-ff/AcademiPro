package com.center.academipro.controller.course;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import com.center.academipro.models.Course;
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

    private final ObservableList<Course> courseList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colCourseId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        colCourseName.setCellValueFactory(cellData -> cellData.getValue().courseNameProperty());
        colDescription.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        colImage.setCellValueFactory(cellData -> cellData.getValue().imageProperty());
        colPrice.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());
        tableCourse.setItems(courseList);
        loadCoursesFromDatabase();

    }

    private void loadCoursesFromDatabase() {
        String url = "jdbc:mysql://localhost:3306/academipro";
        String user = "root";
        String password = "";

        String query = "SELECT * FROM courses";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("course_name");
                String description = resultSet.getString("description");
                String image = resultSet.getString("image");
                double price = resultSet.getDouble("price");

                Course course = new Course(id, name, description, image, price);
                courseList.add(course);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addCourse() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/center/academipro/view/course/add-new-course.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) tableCourse.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void updateCourse() {
        Course selectedCourse = tableCourse.getSelectionModel().getSelectedItem();

        if (selectedCourse == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Course Selected");
            alert.setContentText("Please select a course to update.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/center/academipro/view/course/edit-course.fxml"));
            Parent root = loader.load();

            EditCourseController controller = loader.getController();
            controller.setCourse(selectedCourse);

            Scene scene = new Scene(root);
            Stage stage = (Stage) tableCourse.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void deleteCourse() {
        Course selectedCourse = tableCourse.getSelectionModel().getSelectedItem();

        if (selectedCourse == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Course Selected");
            alert.setContentText("Please select a course to delete.");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Are you sure you want to delete this course?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String url = "jdbc:mysql://localhost:3306/academipro";
            String user = "root";
            String password = "";
            String deleteQuery = "DELETE FROM courses WHERE id = ?";

            try (Connection conn = DriverManager.getConnection(url, user, password);
                 PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {

                stmt.setInt(1, selectedCourse.getId());
                stmt.executeUpdate();
                courseList.remove(selectedCourse);

            } catch (SQLException e) {
                e.printStackTrace();
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Database Error");
                errorAlert.setHeaderText("Failed to delete course.");
                errorAlert.setContentText(e.getMessage());
                errorAlert.showAndWait();
            }
        }
    }
}
