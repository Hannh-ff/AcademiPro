package com.center.academipro.controller;

import com.center.academipro.utils.SceneSwitch;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuBarController {
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private Button btnDashboard, btnTeacher, btnCourse, btnPayment, btnStudent, btnClass;

    @FXML
    public void changePasswordScene(ActionEvent event) {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneSwitch.switchTo(currentStage, "view/change-password-view.fxml");
    }

    public void dashboardScene(ActionEvent actionEvent) {
        FXMLLoader loader = SceneSwitch.loadView("view/admin/adminMainForm.fxml"); // Thêm đường dẫn đến file FXML của dashboard
        assert loader != null;
        Parent newView = loader.getRoot();
        mainBorderPane.setCenter(newView);
    }

    public void teacherScene(ActionEvent actionEvent) {
        FXMLLoader loader = SceneSwitch.loadView("view/admin/teacherManagement/teacher-view.fxml");
        assert loader != null;
        Parent newView = loader.getRoot();
        mainBorderPane.setCenter(newView);
    }

    public void courseScene(ActionEvent actionEvent) {
        FXMLLoader loader = SceneSwitch.loadView("view/admin/courseManagement/course-management.fxml");
        assert loader != null;
        Parent newView = loader.getRoot();
        mainBorderPane.setCenter(newView);
    }

    public void classScene(ActionEvent actionEvent) {
        FXMLLoader loader = SceneSwitch.loadView("view/admin/classManagement/class-management.fxml");
        assert loader != null;
        Parent newView = loader.getRoot();
        mainBorderPane.setCenter(newView);
    }

    public void paymentScene(ActionEvent actionEvent) {
        FXMLLoader loader = SceneSwitch.loadView("view/admin/paymentManagement/payment-view.fxml");
        assert loader != null;
        Parent newView = loader.getRoot();
        mainBorderPane.setCenter(newView);
    }

    public void studentScene(ActionEvent actionEvent) {
        FXMLLoader loader = SceneSwitch.loadView("view/admin/studentManagement/student-view.fxml");
        assert loader != null;
        Parent newView = loader.getRoot();
        mainBorderPane.setCenter(newView);
    }


    public void listCoursesScene(ActionEvent actionEvent) {
        FXMLLoader loader = SceneSwitch.loadView("view/student/list-courses.fxml");
        assert loader != null;
        Parent newView = loader.getRoot();
        mainBorderPane.setCenter(newView);
    }

    public void historyScene(ActionEvent actionEvent) {
        FXMLLoader loader = SceneSwitch.loadView("view/student/purchased-courses.fxml");
        assert loader != null;
        Parent newView = loader.getRoot();
        mainBorderPane.setCenter(newView);
    }
}
