package com.center.academipro.controller.admin.courseManagement;

import com.center.academipro.models.Course;
import com.center.academipro.utils.SceneSwitch;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
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

            showAlert(Alert.AlertType.INFORMATION, "Add course successfully");
            clearForm();

            if (parentController != null) parentController.reloadCourseTable();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Cannot add course: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validateInput() {
        if (txtCourseName.getText().trim().isEmpty() || txtPrice.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Please fill in all fields.");
            return false;
        }

        try {
            Double.parseDouble(txtPrice.getText().trim());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Price must be a number.");
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
            try {
                File imagesDir = new File("images"); // thư mục ngoài src
                if (!imagesDir.exists()) {
                    imagesDir.mkdirs();
                }

                String uniqueFileName = System.currentTimeMillis() + "_" + file.getName();
                File destFile = new File(imagesDir, uniqueFileName);

                // copy ảnh vào thư mục images
                try (InputStream in = new FileInputStream(file);
                     OutputStream out = new FileOutputStream(destFile)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0) {
                        out.write(buffer, 0, length);
                    }
                }

                txtImage.setText("images/" + uniqueFileName); // lưu đường dẫn tương đối
                loadImage(destFile.getPath());

            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Không thể lưu ảnh: " + e.getMessage());
            }
        }
    }


    private void loadImage(String path) {
        try (FileInputStream input = new FileInputStream(path)) {
            imagePreview.setImage(new Image(input));
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Cannot load image ");
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

    public void handleCancel(ActionEvent actionEvent) {
        SceneSwitch.returnToView(actionEvent, "view/admin/courseManagement/course-management.fxml");
    }
}
