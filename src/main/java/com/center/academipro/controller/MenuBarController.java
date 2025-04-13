package com.center.academipro.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class MenuBarController {
  @FXML
  private BorderPane mainBorderPane;
  @FXML
  private Button btnDashboard, btnTeacher, btnCourse, btnPayment, btnStudent,btnClass;
    @FXML
    public void initialize() {
        btnDashboard.setOnAction(e -> loadPage("dashboard.fxml"));
        btnTeacher.setOnAction(e -> loadPage("teacher.fxml"));
        btnCourse.setOnAction(e -> loadPage("course.fxml"));
        btnPayment.setOnAction(e -> loadPage("payment.fxml"));
        btnStudent.setOnAction(e -> loadPage("student.fxml"));
        btnClass.setOnAction(e -> loadPage("student.fxml"));

        loadPage("dashboard.fxml");
    }
    private void loadPage(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/your/path/" + fxmlFile));
            AnchorPane pane = loader.load();
            mainBorderPane.setCenter(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
