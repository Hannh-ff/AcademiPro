package com.center.academipro.controller;

import com.center.academipro.models.Users;
import com.center.academipro.session.SessionManager;
import com.center.academipro.utils.Alerts;
import com.center.academipro.utils.DBConnection;
import com.center.academipro.utils.SceneSwitch;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML
    TextField usernameField;
    @FXML
    TextField passwordField;
    @FXML
    Button loginButton;

    public static Connection conn = new DBConnection().getConn();

    public Users login(String username, String password) throws SQLException {
        Users user = null;
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String dbPassword = rs.getString("password");
                    System.out.println("User found: true");
                    if (dbPassword.trim().equals(hashPassword(password).trim())) {
                        user = new Users();
                        user.setId(rs.getInt("id"));
                        user.setFullName(rs.getString("fullname"));
                        user.setUsername(rs.getString("username"));
                        user.setEmail(rs.getString("email"));
                        user.setPassword(dbPassword);
                        user.setRole(rs.getString("role"));
                        return user;
                    }
                    System.out.println("Input password (raw): " + password);
                    System.out.println("Input password (hashed): " + hashPassword(password));
                    System.out.println("DB password: " + dbPassword);
                }

                return null;
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void handleLogin(ActionEvent actionEvent) throws SQLException {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        Users user = login(username, password);
        if (user != null) {
            SessionManager.getInstance().setUser(user.getUsername(), user.getId());
//            SessionManager.getInstance().setRole(user.getRole());
            switch (user.getRole()) {
                case "Admin":
                    System.out.println("Login successful with role: Admin");
                    Stage currentStage1 = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                    SceneSwitch.switchTo(currentStage1, "view/admin/menu-bar-view.fxml");
                    break;
                case "Teacher":
                    System.out.println("Login successful with role: Teacher");
                    Stage currentStage2 = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                    SceneSwitch.switchTo(currentStage2, "view/teacher/menu-bar-view.fxml");
                    break;
                case "Student":

                    System.out.println("Login successful with role: Student");
                    Stage currentStage3 = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                    SceneSwitch.switchTo(currentStage3, "view/student/menu-bar-view.fxml");
                    break;
                default:
                    System.out.println("Role not recognized");
                    Alerts.alertError("Login Error", "Role not recognized");
                    break;
            }
        } else {

            System.out.println("Login failed: Invalid username or password");
            // Hiển thị thông báo lỗi cho người dùng
            Alerts.alertError("Login Error", "Invalid username or password");
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
