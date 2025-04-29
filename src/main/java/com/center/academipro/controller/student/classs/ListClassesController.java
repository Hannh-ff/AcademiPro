package com.center.academipro.controller.student.classs;

import com.center.academipro.controller.student.EventDAO;
import com.center.academipro.models.Class;
import com.center.academipro.session.SessionCourse;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ListClassesController implements Initializable {

    @FXML
    private VBox classListVBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int courseId = SessionCourse.getCourseId(); // lấy từ Session
        List<Class> classes = EventDAO.getAllClassesByCourseId(courseId);
        renderClassCards(classes);

    }

    private void renderClassCards(List<com.center.academipro.models.Class> classes) {
        classListVBox.getChildren().clear();

        for (com.center.academipro.models.Class c : classes) {
            HBox card = new HBox(15);
            card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 15;");
            card.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.1)));
            card.setAlignment(Pos.CENTER_LEFT);

            VBox info = new VBox(5);
            Label className = new Label("Class: " + c.getClassName());
            className.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

            Label teacherName = new Label("Teacher: " + c.getTeacherName());
            teacherName.setStyle("-fx-text-fill: #666;");

            Label courseName = new Label("Course: " + c.getCourseName());
            courseName.setStyle("-fx-text-fill: #666;");

            info.getChildren().addAll(className, teacherName, courseName);

            Button joinBtn = new Button("Join Class");
            joinBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 5;");
//            joinBtn.setOnAction(e -> {
//
//            });

            HBox.setHgrow(info, Priority.ALWAYS);
            HBox.setMargin(joinBtn, new Insets(0, 20, 0, 0));

            card.getChildren().addAll(info, joinBtn);
            classListVBox.getChildren().add(card);
        }
    }
}
