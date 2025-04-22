package com.center.academipro.controller.admin.classManagement;

import com.center.academipro.models.Class;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EditClassController {

    private ClassController parentController;  // Biến lưu controller cha
    private Class classModel;  // Biến lưu thông tin lớp học cần chỉnh sửa

    @FXML private TextField classNameField;
    @FXML private TextArea descriptionField;
    @FXML private TextField imageField;
    @FXML private TextField priceField;

    public void setParentController(ClassController parentController) {
        this.parentController = parentController;
    }

    public void setClassModel(Class classModel) {
        this.classModel = classModel;
        // Gán giá trị từ classModel vào các trường trên giao diện
        classNameField.setText(classModel.getClassName());
        descriptionField.setText(classModel.getDescription());
        imageField.setText(classModel.getImage());
        priceField.setText(String.valueOf(classModel.getPrice()));
    }

    @FXML
    private void saveClass() {
        String className = classNameField.getText();
        String description = descriptionField.getText();
        String image = imageField.getText();
        double price = Double.parseDouble(priceField.getText());
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/academipro", "root", "");
             PreparedStatement stmt = conn.prepareStatement("UPDATE classes SET class_name = ?, description = ?, image = ?, price = ? WHERE id = ?")) {

            stmt.setString(1, className);
            stmt.setString(2, description);
            stmt.setString(3, image);
            stmt.setDouble(4, price);
            stmt.setInt(5, classModel.getId());
            stmt.executeUpdate();

            parentController.reloadClassTable();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể cập nhật lớp học.");
        }
    }

    @FXML
    private void cancel() {
        Stage stage = (Stage) classNameField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
