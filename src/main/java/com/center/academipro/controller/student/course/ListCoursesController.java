package com.center.academipro.controller.student.course;

import com.center.academipro.controller.student.EventDAO;
import com.center.academipro.controller.student.PaymentController;
import com.center.academipro.models.Course;
import com.center.academipro.session.SessionCourse;
import com.center.academipro.session.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ListCoursesController implements Initializable {

    @FXML
    private VBox courseListVBox;
    @FXML
    private TextField searchField;
    @FXML
    private Label pageIndicator;

    private final ObservableList<Course> allCourses  = FXCollections.observableArrayList();
    private FilteredList<Course> filteredCourse;
    private static final int ITEMS_PER_PAGE = 4;
    private int currentPage = 0;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<Course> courses = EventDAO.getAllCourses();
        allCourses.setAll(courses);
        setupSearchFilter();
    }

    private HBox createCourseCard(Course course) {
        HBox card = new HBox(15);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 10;");
        card.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.1)));

        ImageView imageView = new ImageView();

        File imgFile = new File(    course.getImage());
        System.out.println("Tìm ảnh tại: " + imgFile.getAbsolutePath());

        if (imgFile.exists()) {
            imageView.setImage(new Image(imgFile.toURI().toString()));
        } else {
            System.out.println("Không tìm thấy ảnh: " + imgFile.getPath());
            InputStream fallback = getClass().getResourceAsStream("/com/center/academipro/images/1.png");
            if (fallback != null) {
                imageView.setImage(new Image(fallback));
            }
        }

        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);



//        ImageView imageView = new ImageView();
//        InputStream imgStream = getClass().getResourceAsStream("/com/center/academipro/images/" + course.getImage());
//        if (imgStream == null) {
//            System.out.println("Image not found " + course.getImage());
//            imgStream = getClass().getResourceAsStream("/com/center/academipro/images/1.png");
//        }
//        if (imgStream != null) {
//            Image image = new Image(imgStream);
//            imageView.setImage(image);
//            imageView.setFitWidth(100);
//            imageView.setFitHeight(100);
//            imageView.setPreserveRatio(true);
//            imageView.setSmooth(true);
//        } else {
//            System.out.println("Image stream is null for " + course.getImage());
//        }

        VBox info = new VBox(5);
        Label title = new Label(course.getCourseName());
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label desc = new Label(course.getDescription());
        desc.setStyle("-fx-text-fill: gray;");
        Label price = new Label("$" + course.getPrice());
        price.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");

        info.getChildren().addAll(title, desc, price);

        Button enrollBtn = new Button("Buy now ➜");
        enrollBtn.setStyle("-fx-background-color: #3a86ff; -fx-text-fill: white; -fx-background-radius: 5;");
        VBox actionBox = new VBox(5, enrollBtn);
        actionBox.setAlignment(Pos.TOP_RIGHT);
        HBox.setHgrow(info, Priority.ALWAYS);

        enrollBtn.setOnAction(e -> {
            SessionCourse.setCourseId(course.getId()); // Lưu courseId vào session

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/center/academipro/view/student/payment-views.fxml"));
                Parent newView = loader.load(); // Load xong lấy root mới

                PaymentController controller = loader.getController();
                controller.setStudentAndCourse(SessionManager.getInstance().getUserId(), course.getId());

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

        card.getChildren().addAll(imageView, info, actionBox);
        return card;
    }

    private void renderCourses(List<Course> courses) {
        courseListVBox.getChildren().clear();
        for (Course course : courses) {
            courseListVBox.getChildren().add(createCourseCard(course));
        }
    }

    private void setupSearchFilter() {
        if (allCourses == null) {
            System.out.println("Course list is NULL!");
            return;
        }

        System.out.println("Course List Size: " + allCourses.size());
        for (Course c : allCourses) {
            System.out.println("Course: " + c.getCourseName());
        }

        if (allCourses.isEmpty()) {
            System.out.println("Course List is empty!");
            return;
        }

        filteredCourse = new FilteredList<>(allCourses, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Search: " + newValue);

            filteredCourse.setPredicate(course ->{
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
            currentPage = 0;
            renderPage(currentPage);
        });

        System.out.println("Search filter applied!");
        renderPage(currentPage);
    }

    private void renderPage(int page) {
        courseListVBox.getChildren().clear();
        int start = page * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, filteredCourse.size());

        for (int i = start; i < end; i++) {
            courseListVBox.getChildren().add(createCourseCard(filteredCourse.get(i)));
        }

        int totalPages = (int) Math.ceil((double) filteredCourse.size() / ITEMS_PER_PAGE);
        pageIndicator.setText("Page " + (currentPage + 1) + " / " + totalPages);
    }

    private List<Course> getCurrentPageItems() {
        int start = currentPage * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, filteredCourse.size());
        return filteredCourse.subList(start, end);
    }

    @FXML
    private void handleNextPage() {
        if ((currentPage + 1) * ITEMS_PER_PAGE < filteredCourse.size()) {
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

}
