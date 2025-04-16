package com.center.academipro.controller.student;

import com.center.academipro.models.Course;
import com.center.academipro.utils.DBConnection;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import javafx.scene.image.ImageView;

import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ListCoursesController implements Initializable {

    @FXML
    private VBox courseListVBox;

    public List<Course> getAllCourses() {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM courses";

        try (Connection conn = DBConnection.getConn();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Course course = new Course();
                course.setId(rs.getInt("id"));
                course.setCourseName(rs.getString("course_name"));
                course.setDescription(rs.getString("description"));
                course.setImage(rs.getString("image")); // ảnh là tên file trong thư mục resources
                course.setPrice(rs.getDouble("price"));
                list.add(course);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private void renderCourses(List<Course> courses) {
        courseListVBox.getChildren().clear();

        for (Course course : courses) {
            HBox card = new HBox(15);
            card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 10;");
            card.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.1)));

            ImageView imageView = new ImageView();
            InputStream imgStream = getClass().getResourceAsStream("/com/center/academipro/images/" + course.getImage());
            if (imgStream == null) {
                System.out.println("Image not found " + course.getImage());
                imgStream = getClass().getResourceAsStream("/com/center/academipro/images/1.png");
            }
            if (imgStream != null) {
                Image image = new Image(imgStream);
                imageView.setImage(image);
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
            } else {
                System.out.println("Image stream is null for " + course.getImage());
            }


            VBox info = new VBox(5);
            Label title = new Label(course.getCourseName());
            title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            Label desc = new Label(course.getDescription());
            desc.setStyle("-fx-text-fill: gray;");
            Label enrolled = new Label("$" + course.getPrice());
            enrolled.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");

            info.getChildren().addAll(title, desc, enrolled);

            Button enrollBtn = new Button("Buy now ➜");
            enrollBtn.setStyle("-fx-background-color: #3a86ff; -fx-text-fill: white; -fx-background-radius: 5;");
            VBox actionBox = new VBox(5, enrollBtn);
            actionBox.setAlignment(Pos.TOP_RIGHT);
            HBox.setHgrow(info, Priority.ALWAYS);

            card.getChildren().addAll(imageView, info, actionBox);
            courseListVBox.getChildren().add(card);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<Course> courses = getAllCourses();
        renderCourses(courses);
    }
}
