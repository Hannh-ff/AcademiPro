package com.center.academipro.controller.student.classs;

import com.center.academipro.controller.student.EventDAO;
import com.center.academipro.models.Class;
import com.center.academipro.session.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyClassesController {
    @FXML
    private GridPane gridPane;

    private static final int COLUMN_COUNT = 4; // 4 cột
    int studentId = SessionManager.getInstance().getUserId();

    @FXML
    public void initialize() {
        List<Class> joinedClasses = EventDAO.getJoinedClassesByUserId(); // Lấy danh sách lớp

        int row = 0;
        int column = 0;

        for (Class joinedClass : joinedClasses) {
            VBox card = renderCard(joinedClass);

            gridPane.add(card, column, row);

            column++;
            if (column == COLUMN_COUNT) {
                column = 0;
                row++;
            }
        }
    }

    private VBox renderCard(Class joinedClass) {
        VBox card = new VBox(8);
        card.setPrefSize(220, 180);
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.TOP_LEFT);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #e0e0e0; -fx-border-radius: 12;");
        card.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.1)));

        Label classNameLabel = new Label(joinedClass.getClassName());
        classNameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        Label courseNameLabel = new Label("Course: " + joinedClass.getCourseName());
        courseNameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");

        Label teacherNameLabel = new Label("Teacher: " + joinedClass.getTeacherName());
        teacherNameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");

        Button openButton = new Button("Open");
        openButton.setStyle("-fx-background-color: #3a86ff; -fx-text-fill: white; -fx-background-radius: 8;");
        openButton.setMaxWidth(Double.MAX_VALUE);
        openButton.setOnAction(e -> {
            System.out.println("Mở lớp: " + joinedClass.getClassName());

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/center/academipro/view/student/class/my-class-details.fxml"));
                Parent newView = loader.load(); // Load xong lấy root mới

                MyClassDetailsController controller = loader.getController();
                controller.setClassAndStudentId(joinedClass.getId(), studentId); // Truyền classId và studentId cho controller

                StackPane pane = (StackPane) ((Node) e.getSource()).getScene().getRoot();
                BorderPane mainPane = (BorderPane) pane.lookup("#mainBorderPane");

                if (mainPane != null) {
                    mainPane.setCenter(newView); // Chỉ thay Center, không replace root
                } else {
                    System.err.println("Cannot find #mainBorderPane");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        card.getChildren().addAll(classNameLabel, courseNameLabel, teacherNameLabel, spacer, openButton);
        return card;
    }


}
