package com.center.academipro.controller.student;

import com.center.academipro.models.Course;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import javafx.scene.image.ImageView;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PurchasedCoursesController implements Initializable {

    @FXML
    private VBox purchasedCoursesVBox;
    @FXML
    private TextField searchField;
    @FXML
    private Label pageIndicator;

    private final ObservableList<Course> allPurchasedCourses = FXCollections.observableArrayList();
    private FilteredList<Course> filteredPurchasedCourse;
    private static final int ITEMS_PER_PAGE = 4;
    private int currentPage = 0;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<Course> purchasedCourses = EventCourses.getPurchasedCoursesByUserId();
        allPurchasedCourses.setAll(purchasedCourses);
        setupSearchFilter();
    }

    private HBox createPurchasedCourseCard(Course course) {
        HBox card = new HBox(15);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 10;");
        card.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.1)));

        ImageView imageView = new ImageView();
        InputStream imgStream = getClass().getResourceAsStream("/com/center/academipro/images/" + course.getImage());
        if (imgStream == null) {
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
        Label price = new Label("$" + course.getPrice());
        price.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");

        info.getChildren().addAll(title, desc, price);
        HBox.setHgrow(info, Priority.ALWAYS);

        Button cancelBtn = new Button("Cancel ➜");
        cancelBtn.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white; -fx-background-radius: 5;");
        cancelBtn.setOnAction(e -> {
            EventCourses.cancelCourse(course.getId());
            allPurchasedCourses.setAll(EventCourses.getPurchasedCoursesByUserId());
            currentPage = 0;
            renderPage(currentPage);
        });

        VBox actionBox = new VBox(5, cancelBtn);
        actionBox.setAlignment(Pos.TOP_RIGHT);

        card.getChildren().addAll(imageView, info, actionBox);
        return card;
    }

    private void setupSearchFilter() {
        if (allPurchasedCourses == null) {
            System.out.println("Course list is NULL!");
            return;
        }

        System.out.println("Course List Size: " + allPurchasedCourses.size());
        for (Course c : allPurchasedCourses) {
            System.out.println("Course: " + c.getCourseName());
        }

        if (allPurchasedCourses.isEmpty()) {
            System.out.println("Course List is empty!");
            return;
        }

        filteredPurchasedCourse = new FilteredList<>(allPurchasedCourses, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Search: " + newValue);

            filteredPurchasedCourse.setPredicate(course ->{
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                boolean match = course.getCourseName().toLowerCase().contains(lowerCaseFilter);

                if (match) {
                    System.out.println("Matched: " + course.getCourseName());
                }
                return match;
            });


            System.out.println("Filtered Movies: " + filteredPurchasedCourse.size());
            currentPage = 0;
            renderPage(currentPage);
        });

        System.out.println("Search filter applied!");
        renderPage(currentPage);
    }

    private void renderPage(int page) {
        purchasedCoursesVBox.getChildren().clear();
        int start = page * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, filteredPurchasedCourse.size());

        for (int i = start; i < end; i++) {
            purchasedCoursesVBox.getChildren().add(createPurchasedCourseCard(filteredPurchasedCourse.get(i)));
        }

        int totalPages = (int) Math.ceil((double) filteredPurchasedCourse.size() / ITEMS_PER_PAGE);
        pageIndicator.setText("Page " + (currentPage + 1) + " / " + totalPages);
    }

    @FXML
    private void handleNextPage() {
        if ((currentPage + 1) * ITEMS_PER_PAGE < filteredPurchasedCourse.size()) {
            currentPage++;
            renderPage(currentPage);
        }
    }

    @FXML
    private void handlePrevPage() {
        if (currentPage > 0) {
            currentPage--;
            renderPage(currentPage);
        }
    }

    private void renderPurchasedCourses(List<Course> courses) {
        purchasedCoursesVBox.getChildren().clear();

        for (Course course : courses) {
            purchasedCoursesVBox.getChildren().add(createPurchasedCourseCard(course));
        }
    }
}
