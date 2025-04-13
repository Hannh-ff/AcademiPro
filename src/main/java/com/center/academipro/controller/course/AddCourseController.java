package com.center.academipro.controller.course;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import com.center.academipro.models.Course;

public class AddCourseController {

    @FXML private TextField txtCourseName;
    @FXML private TextArea txtDescription;
    @FXML private TextField txtImage;
    @FXML private TextField txtPrice;
    @FXML private ImageView imagePreview;

    private Course course;
    private List<Course> courseList;

    public void setCourse(Course course, List<Course> courseList) {
        this.course = course;
        this.courseList = courseList;
        if (course != null) {
            txtCourseName.setText(course.getCourseName());
            txtDescription.setText(course.getDescription());
            txtImage.setText(course.getImage());
            txtPrice.setText(String.valueOf(course.getPrice()));
            loadImage(course.getImage());
        }
    }

    public void addCourse(ActionEvent actionEvent) {
        if (course == null) {
            course = new Course();
        }

        boolean success = fillCourseFromForm(course);
        if (!success) return;

        if (!courseList.contains(course)) {
            courseList.add(course);
        }
    }

    private boolean fillCourseFromForm(Course targetCourse) {
        String name = txtCourseName.getText().trim();
        String description = txtDescription.getText().trim();
        String image = txtImage.getText().trim();
        String priceText = txtPrice.getText().trim();

        if (name.isEmpty() || priceText.isEmpty()) {
            showErrorMessage("Thiếu thông tin", "Vui lòng nhập đầy đủ tên khóa học và giá.");
            return false;
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            showErrorMessage("Giá không hợp lệ", "Vui lòng nhập một giá hợp lệ.");
            return false;
        }

        targetCourse.setCourseName(name);
        targetCourse.setDescription(description);
        targetCourse.setImage(image);
        targetCourse.setPrice(price);
        return true;
    }

    public void importImage(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
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

    private void showErrorMessage(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void clearForm(ActionEvent actionEvent) {
        txtCourseName.clear();
        txtDescription.clear();
        txtImage.clear();
        txtPrice.clear();
        imagePreview.setImage(null);
        course = null;
    }
}
