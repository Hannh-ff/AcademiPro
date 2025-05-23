package com.center.academipro.controller.admin.courseManagement;

import com.center.academipro.models.Course;
import com.center.academipro.utils.SceneSwitch;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class EditCourseController {

    @FXML private TextField txtCourseName;
    @FXML private TextField txtPrice;
    @FXML private TextArea txtDescription;
    @FXML private TextField txtImage;
    @FXML private ImageView imagePreview;

    private CourseController parentController;
    private Course selectedCourse;

    public void setCourse(Course course) {
        this.selectedCourse = course;

        txtCourseName.setText(course.getCourseName());
        txtPrice.setText(String.valueOf(course.getPrice()));
        txtDescription.setText(course.getDescription());
        txtImage.setText(course.getImage());

        // Hiển thị ảnh nếu có
        try {
            Image image = new Image(new File(course.getImage()).toURI().toString());
            imagePreview.setImage(image);
        } catch (Exception e) {
            System.out.println("Cannot load image: " + course.getImage());
        }
    }

    @FXML
    private void updateCourse(ActionEvent actionEvent) {
        if (selectedCourse == null) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không có khóa học nào được chọn.");
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/academipro", "root", "");
             PreparedStatement stmt = conn.prepareStatement("UPDATE courses SET course_name=?, description=?, image=?, price=? WHERE id=?")) {

            stmt.setString(1, txtCourseName.getText());
            stmt.setString(2, txtDescription.getText());
            stmt.setString(3, txtImage.getText());
            stmt.setDouble(4, Double.parseDouble(txtPrice.getText()));
            stmt.setInt(5, selectedCourse.getId());

            stmt.executeUpdate();
            showAlert(Alert.AlertType.INFORMATION, "Successfully!", "Updated course successfully!");
            SceneSwitch.returnToView(actionEvent, "view/admin/courseManagement/course-management.fxml");

            if (parentController != null) {
                parentController.reloadCourseTable();
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot update course: " + e.getMessage());
        }
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
                showAlert(Alert.AlertType.ERROR, "Error", "Cannot import image: " + e.getMessage());
            }
        }
    }

    private void loadImage(String imagePath) {
        try {
            Image image = new Image(new File(imagePath).toURI().toString());
            imagePreview.setImage(image);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot load image: " + imagePath);
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

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public void handleCancel(ActionEvent actionEvent) {
        SceneSwitch.returnToView(actionEvent, "view/admin/courseManagement/course-management.fxml");
    }
}
