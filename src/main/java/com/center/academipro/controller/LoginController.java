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
                    if (dbPassword.equals(password)) {
                        user = new Users();
                        user.setId(rs.getInt("id"));
                        user.setFullName(rs.getString("fullname"));
                        user.setUsername(rs.getString("username"));
                        user.setEmail(rs.getString("email"));
                        user.setPassword(dbPassword);
                        user.setRole(rs.getString("role"));
                        return user;
                    }
                }
                // Nếu không tìm thấy người dùng hoặc mật khẩu không khớp
                return null;
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
                    // chuyển đến giao diện admin
                    System.out.println("Login successful with role: Admin");
                    Stage currentStage1 = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                    SceneSwitch.switchTo(currentStage1, "view/admin/menu-bar-view.fxml");
                    break;
                case "Teacher":
                    // chuyển đến giao diện giáo viên
                    System.out.println("Login successful with role: Teacher");
                    Stage currentStage2 = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                    SceneSwitch.switchTo(currentStage2, "");
                    break;
                case "Student":
                    // chuyển đến giao diện học viên
                    System.out.println("Login successful with role: Student");
                    Stage currentStage3 = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                    SceneSwitch.switchTo(currentStage3, "view/student/list-courses.fxml");
                    break;
                default:
                    System.out.println("Role not recognized");
                    // Hiển thị thông báo lỗi cho người dùng
                    Alerts.alertError("Login Error", "Role not recognized");
                    break;
            }
        } else {
            System.out.println("Login failed: Invalid username or password");
            // Hiển thị thông báo lỗi cho người dùng
            Alerts.alertError("Login Error", "Invalid username or password");
        }

    }
}
