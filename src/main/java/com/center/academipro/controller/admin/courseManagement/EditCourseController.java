package com.center.academipro.controller.admin.courseManagement;

import com.center.academipro.models.Course;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class EditCourseController {

    @FXML private TextField txtCourseName;
    @FXML private TextArea txtDescription;
    @FXML private TextField txtImage;
    @FXML private TextField txtPrice;
    @FXML private ImageView imagePreview;

    private Course course;

    public void setCourse(Course course) {
        this.course = course;
        if (course != null) {
            txtCourseName.setText(course.getCourseName());
            txtDescription.setText(course.getDescription());
            txtImage.setText(course.getImage());
            txtPrice.setText(String.valueOf(course.getPrice()));
            loadImage(course.getImage());
        }
    }

    public void updateCourse() {
        if (course != null) {
            course.setCourseName(txtCourseName.getText());
            course.setDescription(txtDescription.getText());
            course.setImage(txtImage.getText());

            try {
                String priceText = txtPrice.getText().trim();
                if (!priceText.isEmpty()) {
                    double price = Double.parseDouble(priceText);
                    course.setPrice(price);
                } else {
                    showErrorMessage("Giá không hợp lệ", "Vui lòng nhập giá khóa học.");
                }
            } catch (NumberFormatException e) {
                showErrorMessage("Giá không hợp lệ", "Vui lòng nhập một giá hợp lệ.");
            }
        }
    }

    private void showErrorMessage(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void importImage(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            txtImage.setText(file.getAbsolutePath());
            loadImage(file.getAbsolutePath());
        }
    }

    private void loadImage(String imagePath) {
        try (FileInputStream inputStream = new FileInputStream(imagePath)) {
            Image image = new Image(inputStream);
            imagePreview.setImage(image);
        } catch (IOException e) {
            showErrorMessage("Lỗi hình ảnh", "Không thể tải hình ảnh.");
        }
    }

    public void addCourse(ActionEvent actionEvent) {
        updateCourse();
        System.out.println("Khóa học đã được thêm: " + course);
    }

    public void clearForm(ActionEvent actionEvent) {
        txtCourseName.clear();
        txtDescription.clear();
        txtImage.clear();
        txtPrice.clear();
        imagePreview.setImage(null);
    }
}
