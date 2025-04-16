package com.center.academipro.controller;

import com.center.academipro.utils.SceneSwitch;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class MenuBarController {
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private Button btnDashboard, btnTeacher, btnCourse, btnPayment, btnStudent, btnClass;
//    @FXML
//    public void initialize() {
////        btnDashboard.setOnAction(e -> loadPage("dashboard.fxml"));
//        btnTeacher.setOnAction(e -> loadPage("view/admin/teacher-view.fxml"));
////        btnCourse.setOnAction(e -> loadPage("course.fxml"));
////        btnPayment.setOnAction(e -> loadPage("payment.fxml"));
////        btnStudent.setOnAction(e -> loadPage("student.fxml"));
////        btnClass.setOnAction(e -> loadPage("student.fxml"));
//
//        loadPage("dashboard.fxml");
//    }
//    private void loadPage(String fxmlFile) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/center/academipro/" + fxmlFile));
//            AnchorPane pane = loader.load();
//            mainBorderPane.setCenter(pane);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public void dashboardScene(ActionEvent actionEvent) {
        FXMLLoader loader = SceneSwitch.loadView(""); // Thêm đường dẫn đến file FXML của dashboard
        assert loader != null;
        Parent newView = loader.getRoot();
        mainBorderPane.setCenter(newView);
    }

    public void teacherScene(ActionEvent actionEvent) {
        FXMLLoader loader = SceneSwitch.loadView("view/admin/teacher-view.fxml");
        assert loader != null;
        Parent newView = loader.getRoot();
        mainBorderPane.setCenter(newView);
    }

    public void courseScene(ActionEvent actionEvent) {
        FXMLLoader loader = SceneSwitch.loadView("view/course/course-management.fxml");
        assert loader != null;
        Parent newView = loader.getRoot();
        mainBorderPane.setCenter(newView);
    }

    public void classScene(ActionEvent actionEvent) {
        FXMLLoader loader = SceneSwitch.loadView(""); // Thêm đường dẫn đến file FXML của class
        assert loader != null;
        Parent newView = loader.getRoot();
        mainBorderPane.setCenter(newView);
    }

    public void paymentScene(ActionEvent actionEvent) {
        FXMLLoader loader = SceneSwitch.loadView(""); // Thêm đường dẫn đến file FXML của payment
        assert loader != null;
        Parent newView = loader.getRoot();
        mainBorderPane.setCenter(newView);
    }

    public void studentScene(ActionEvent actionEvent) {
        FXMLLoader loader = SceneSwitch.loadView(""); // Thêm đường dẫn đến file FXML của student
        assert loader != null;
        Parent newView = loader.getRoot();
        mainBorderPane.setCenter(newView);
    }

}
