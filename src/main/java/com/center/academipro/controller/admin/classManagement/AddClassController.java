package com.center.academipro.controller.admin.classManagement;

import com.center.academipro.models.Class;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddClassController {

    @FXML private TextField txtClassName;
    @FXML private TextArea txtDescription;
    @FXML private TextField txtImage;
    @FXML private TextField txtPrice;
    @FXML private ImageView imagePreview;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/academipro";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private ClassController parentController;

    public void setParentController(ClassController controller) {
        this.parentController = controller;
    }

    public void addClass(ActionEvent actionEvent) {
        String name = txtClassName.getText().trim();
        String description = txtDescription.getText().trim();
        String image = txtImage.getText().trim();
        String priceText = txtPrice.getText().trim();

        if (name.isEmpty() || priceText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Thiếu thông tin", "Vui lòng nhập đầy đủ tên lớp và giá.");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Giá không hợp lệ", "Vui lòng nhập một giá hợp lệ.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO classes (class_name, description, image, price) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.setString(3, image);
            stmt.setDouble(4, price);
            stmt.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã thêm lớp học.");
            parentController.reloadClassTable();

            Stage stage = (Stage) txtClassName.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi cơ sở dữ liệu", "Không thể thêm lớp.");
        }
    }

    public void clearForm(ActionEvent actionEvent) {
        txtClassName.clear();
        txtDescription.clear();
        txtImage.clear();
        txtPrice.clear();
        imagePreview.setImage(null);
    }

    public void importImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            txtImage.setText(file.getAbsolutePath());
            loadImage(file.getAbsolutePath());
        }
    }

    private void loadImage(String imagePath) {
        try (FileInputStream input = new FileInputStream(imagePath)) {
            imagePreview.setImage(new Image(input));
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi hình ảnh", "Không thể tải hình ảnh.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
