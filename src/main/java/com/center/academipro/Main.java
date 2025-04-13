package com.center.academipro;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/center/academipro/view/course/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        //css
//        scene.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());
//        scene.getStylesheets().add(getClass().getResource("/css/admin.css").toExternalForm());
//        scene.getStylesheets().add(getClass().getResource("/css/staff-detail.css").toExternalForm());

        //font
//        Font font = Font.loadFont(getClass().getResourceAsStream("/font/Montserrat-ExtraLight.ttf"), 16);

        stage.setTitle("Academi Pro");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {

        launch(args);
    }
}