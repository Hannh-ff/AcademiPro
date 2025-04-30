package com.center.academipro.controller;

import com.center.academipro.utils.Alerts;
import com.center.academipro.utils.SceneSwitch;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

public class LogoutController {

    @FXML
    private void handleLogout(ActionEvent event) {
        if (Alerts.alertConfirm("Logout Confirmation", "Are you sure you want to log out?")) {
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneSwitch.switchTo(currentStage, "/com/center/academipro/view/login-view.fxml");
        }
    }
}
