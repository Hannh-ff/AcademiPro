package com.center.academipro.controller;

import com.center.academipro.session.SessionManager;
import com.center.academipro.utils.Alerts;
import com.center.academipro.utils.DBConnection;
import com.center.academipro.utils.SceneSwitch;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
        import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ChangePasswordController {
    @FXML private StackPane rootPane;
    @FXML private PasswordField oldPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;

    @FXML private CheckBox showPasswordCheckbox1;
    @FXML private CheckBox showPasswordCheckbox2;
    @FXML private CheckBox showPasswordCheckbox3;

    @FXML
    private TextField oldPasswordVisible = new TextField();
    @FXML
    private TextField newPasswordVisible = new TextField();
    @FXML
    private TextField confirmPasswordVisible = new TextField();

    private final Connection conn = new DBConnection().getConn();



    @FXML
    public void handleChangePassword(ActionEvent actionEvent) {
        String oldPass = showPasswordCheckbox1.isSelected() ? oldPasswordVisible.getText() : oldPasswordField.getText();
        String newPass = showPasswordCheckbox2.isSelected() ? newPasswordVisible.getText() : newPasswordField.getText();
        String confirmPass = showPasswordCheckbox3.isSelected() ? confirmPasswordVisible.getText() : confirmPasswordField.getText();

        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            Alerts.alertError("Error", "Please fill in all fields.");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            Alerts.alertError("Error", "New password and confirm password do not match.");
            return;
        }

        int userId = SessionManager.getInstance().getUserId();

        try (Connection conn = DBConnection.getConnection()) {
            // Lấy mật khẩu hiện tại từ DB
            String query = "SELECT password FROM users WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String currentPassword = rs.getString("password");

                // So sánh mật khẩu cũ (có thể hash nếu cần)
                if (!currentPassword.equals(hashPassword(oldPass))) {
                    Alerts.alertError("Error", "Old password is incorrect.");
                    return;
                }

                // Cập nhật mật khẩu mới
                String updateQuery = "UPDATE users SET password = ? WHERE id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setString(1, hashPassword(newPass)); // có thể dùng hash nếu cần
                updateStmt.setInt(2, userId);

                int rowsAffected = updateStmt.executeUpdate();
                if (rowsAffected > 0) {
                    Alerts.alertInfo("Success", "Password updated successfully.");
                    clearFields();
                    // Chuyển về trang đăng nhập
                    Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                    SceneSwitch.switchTo(stage, "view/login-view.fxml");
                } else {
                    Alerts.alertError("Error", "Failed to update password.");
                }
            } else {
                Alerts.alertError("Error", "User not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Alerts.alertError("Error", "Database error: " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private void clearFields() {
        oldPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
        oldPasswordVisible.clear();
        newPasswordVisible.clear();
        confirmPasswordVisible.clear();
    }

    @FXML
    public void toggleOldPasswordVisibility() {
        toggleField(showPasswordCheckbox1, oldPasswordField, oldPasswordVisible);
    }

    @FXML
    public void toggleNewPasswordVisibility() {
        toggleField(showPasswordCheckbox2, newPasswordField, newPasswordVisible);
    }

    @FXML
    public void toggleConfirmPasswordVisibility() {
        toggleField(showPasswordCheckbox3, confirmPasswordField, confirmPasswordVisible);
    }

    private void toggleField(CheckBox checkBox, PasswordField passwordField, TextField visibleField) {
        if (checkBox.isSelected()) {
            visibleField.setText(passwordField.getText());
            visibleField.setVisible(true);
            visibleField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
        } else {
            passwordField.setText(visibleField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            visibleField.setVisible(false);
            visibleField.setManaged(false);
        }
    }

    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashed = md.digest(password.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hashed) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
