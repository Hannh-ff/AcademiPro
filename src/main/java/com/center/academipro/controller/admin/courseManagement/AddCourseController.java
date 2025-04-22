package com.center.academipro.controller.admin.courseManagement;

import com.center.academipro.models.Course;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddCourseController {

    @FXML private TextField txtCourseName, txtPrice, txtImage;
    @FXML private TextArea txtDescription;
    @FXML private ImageView imagePreview;

    private CourseController parentController;

    public void setParentController(CourseController parentController) {
        this.parentController = parentController;
    }

    @FXML
    private void addCourse() {
        if (!validateInput()) return;

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/academipro", "root", "");
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO courses (course_name, description, image, price) VALUES (?, ?, ?, ?)")) {

            stmt.setString(1, txtCourseName.getText().trim());
            stmt.setString(2, txtDescription.getText().trim());
            stmt.setString(3, txtImage.getText().trim());
            stmt.setDouble(4, Double.parseDouble(txtPrice.getText().trim()));

            stmt.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Khóa học đã được thêm!");

            if (parentController != null) parentController.reloadCourseTable();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Không thể thêm khóa học: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validateInput() {
        if (txtCourseName.getText().trim().isEmpty() || txtPrice.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Vui lòng nhập tên và giá.");
            return false;
        }

        try {
            Double.parseDouble(txtPrice.getText().trim());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Giá không hợp lệ.");
            return false;
        }

        return true;
    }

    @FXML
    private void importImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            txtImage.setText(file.getAbsolutePath());
            loadImage(file.getAbsolutePath());
        }
    }

    private void loadImage(String path) {
        try (FileInputStream input = new FileInputStream(path)) {
            imagePreview.setImage(new Image(input));
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Không thể tải hình ảnh.");
        }
    }

    @FXML
    private void clearForm() {
        txtCourseName.clear();
        txtDescription.clear();
        txtImage.clear();
        txtPrice.clear();
        imagePreview.setImage(null);
    }

    private void showAlert(Alert.AlertType type, String msg) {
        new Alert(type, msg, ButtonType.OK).showAndWait();
    }
}
