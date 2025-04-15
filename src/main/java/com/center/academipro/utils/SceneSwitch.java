package com.center.academipro.utils;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class SceneSwitch {

    /**
     * Switches the current scene of the given stage to the specified FXML file.
     *
     * @param stage    The stage to switch the scene for.
     * @param fxmlFile The name of the FXML file to load.
     */
    public static void switchTo(Stage stage, String fxmlFile) {
        if (stage == null) {
            throw new IllegalArgumentException("Stage cannot be null.");
        }

        try {
            URL fxmlURL = SceneSwitch.class.getResource("/com/center/academipro/" + fxmlFile);
            if (fxmlURL == null) {
                throw new IOException("FXML file not found: " + fxmlFile);
            }

            FXMLLoader fxmlLoader = new FXMLLoader(fxmlURL);
            Scene scene = new Scene(fxmlLoader.load());
//            scene.getStylesheets().add(SceneSwitch.class.getResource("/css/main.css").toExternalForm());
//            scene.getStylesheets().add(SceneSwitch.class.getResource("/css/admin.css").toExternalForm());
//            scene.getStylesheets().add(SceneSwitch.class.getResource("/css/staff-detail.css").toExternalForm());
            stage.setScene(scene);
            stage.setFullScreenExitHint("");

            if (fxmlFile.equals("login-view.fxml")) {
                stage.setMaximized(false);
                stage.setFullScreen(false);
            } else {
                stage.setMaximized(true);
            }
            stage.show();
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error switching scene: " + e.getMessage());
        }
    }

    /**
     * Loads the specified FXML file and returns the FXMLLoader.
     *
     * @param fxmlFile The name of the FXML file to load.
     * @return The FXMLLoader for the loaded FXML file.
     */
    public static FXMLLoader loadView(String fxmlFile) {
        FXMLLoader loader = new FXMLLoader(SceneSwitch.class.getResource("/com/center/academipro/" + fxmlFile));
        try {
            loader.load(); // Load FXML trước
            return loader; // Trả về loader để lấy Controller
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void backToView(ActionEvent actionEvent, String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneSwitch.class.getResource("/com/center/academipro/" + fxmlFile));
            Parent seatView = loader.load();

            AnchorPane root = (AnchorPane) ((Button) actionEvent.getSource()).getScene().getRoot();
            BorderPane mainPane = (BorderPane) root.lookup("#mainBorderPane");

            if (mainPane != null) {
                mainPane.setCenter(seatView);
            } else {
                System.err.println("BorderPane with ID 'mainBorderPane' not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
