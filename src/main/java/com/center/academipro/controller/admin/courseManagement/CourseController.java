package com.center.academipro.controller.admin.courseManagement;

import com.center.academipro.controller.admin.teacherManagement.EditTeacherController;
import com.center.academipro.models.Course;
import com.center.academipro.utils.SceneSwitch;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.sql.*;
import java.util.List;
import java.util.Optional;

public class CourseController {

    @FXML private TableView<Course> tableCourse;
    @FXML private TableColumn<Course, Integer> colCourseId;
    @FXML private TableColumn<Course, String> colCourseName;
    @FXML private TableColumn<Course, String> colDescription;
    @FXML private TableColumn<Course, ImageView> colImage;
    @FXML private TableColumn<Course, Double> colPrice;
    @FXML private TableColumn<Course, Void> colAction;
    @FXML private TextField searchField;
    @FXML private Label pageIndicator;

    private final ObservableList<Course> courseList = FXCollections.observableArrayList();
    private FilteredList<Course> filteredCourse;
    private static final int ITEMS_PER_PAGE = 4;
    private int currentPage = 0;

    @FXML
    public void initialize() {
        colCourseId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        colCourseName.setCellValueFactory(cellData -> cellData.getValue().courseNameProperty());
        colDescription.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        colPrice.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());
        colImage.setCellValueFactory(cellData -> {
            ImageView imageView = new ImageView();
            String imagePath = cellData.getValue().getImage();

            if (imagePath != null && !imagePath.isEmpty()) {
                try {
                    Image image;
                    if (imagePath.startsWith("http") || imagePath.startsWith("https")) {
                        image = new Image(imagePath);
                    } else {
                        File file = new File(imagePath);
                        if (file.exists()) {
                            image = new Image(file.toURI().toString()); // Sửa ở đây
                        } else {
                            System.err.println("File not found: " + imagePath);
                            image = new Image(getClass().getResource("/images/default.jpg").toExternalForm());
                        }
                    }
                    imageView.setImage(image);
                } catch (Exception e) {
                    System.err.println("Lỗi khi load ảnh: " + e.getMessage());
                }
            }
            imageView.setFitWidth(100);
            imageView.setFitHeight(100);
            return new SimpleObjectProperty<>(imageView);
        });

        addActionColumn();
        loadCoursesFromDatabase();
        setupSearchFilter();
        tableCourse.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    }

    public void reloadCourseTable() {
        loadCoursesFromDatabase();
    }

    private void loadCoursesFromDatabase() {
        courseList.clear();
        String query = "SELECT * FROM courses";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/academipro", "root", "");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Course course = new Course(
                        rs.getInt("id"),
                        rs.getString("course_name"),
                        rs.getString("description"),
                        rs.getString("image"),
                        rs.getDouble("price")
                );
                courseList.add(course);
            }

            if (filteredCourse != null) {
                // Nếu filteredCourse đã khởi tạo rồi thì chỉ cần render lại
                currentPage = 0;
                renderPage(currentPage);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot load courses from database.");
        }
    }

    @FXML
    private void addCourse(ActionEvent event) {
        FXMLLoader loader = SceneSwitch.loadView("view/admin/courseManagement/add-new-course.fxml");
        if (loader != null) {
            Parent newView = loader.getRoot(); // Lấy Root từ FXMLLoader
            StackPane pane = (StackPane) ((Node) event.getSource()).getScene().getRoot();
            BorderPane mainPane = (BorderPane) pane.lookup("#mainBorderPane");
            mainPane.setCenter(newView); // Thay đổi nội dung của center
        } else {
            System.err.println("Failed to load addnew-user.fxml");
        }
    }

    private void addActionColumn() {
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Update");
            private final Button deleteBtn = new Button("Delete");
            private final HBox actionBox = new HBox(10, editBtn, deleteBtn);

            {
                editBtn.setOnAction(event -> {
                    Course course = getTableView().getItems().get(getIndex());
                    updateCourse(course);
                });

                deleteBtn.setOnAction(event -> {
                    Course course = getTableView().getItems().get(getIndex());
                    deleteCourse(course);
                });

                editBtn.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white;");
                deleteBtn.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white;");
                actionBox.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(actionBox);
                }
            }
        });
    }

    @FXML
    private void updateCourse(Course course) {
        if (course == null) {
            System.out.println("Course is null. Cannot open update view.");
            return;
        }

        try {
            FXMLLoader loader = SceneSwitch.loadView("view/admin/courseManagement/edit-course.fxml");
            if (loader != null) {
                EditCourseController controller = loader.getController();
                controller.setCourse(course);// Truyền dữ liệu ghế cần chỉnh sửa

                Parent newView = loader.getRoot();
                StackPane pane = (StackPane) tableCourse.getScene().getRoot();
                BorderPane mainPane = (BorderPane) pane.lookup("#mainBorderPane");

                if (mainPane != null) {
                    mainPane.setCenter(newView);
                } else {
                    System.err.println("BorderPane with ID 'mainBorderPane' not found");
                }
            } else {
                System.err.println("Could not load edit-seat.fxml");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteCourse(Course course) {
        if (course == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Course is null. Cannot delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this course?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Delete Confirmation");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            Integer id = course.getId();

            String deleteClassesSql = "DELETE FROM classes WHERE course_id = ?";
            String deleteTeacherCoursesSql = "DELETE FROM teacher_courses WHERE course_id = ?";
            String checkPurchaseSql = "SELECT COUNT(*) FROM purchase_history WHERE course_id = ?";
            String deletePurchaseSql = "DELETE FROM purchase_history WHERE course_id = ?";
            String deleteCourseSql = "DELETE FROM courses WHERE id = ?";

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/academipro", "root", "")) {
                conn.setAutoCommit(false);

                boolean hasPurchase = false;

                try (PreparedStatement checkStmt = conn.prepareStatement(checkPurchaseSql)) {
                    checkStmt.setInt(1, id);
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) {
                            hasPurchase = true;
                        }
                    }
                }

                if (hasPurchase) {
                    Alert purchaseConfirm = new Alert(Alert.AlertType.CONFIRMATION, "This course has purchases. Do you still want to delete it?", ButtonType.YES, ButtonType.NO);
                    purchaseConfirm.setTitle("Course Has Purchases");
                    Optional<ButtonType> purchaseResult = purchaseConfirm.showAndWait();

                    if (!(purchaseResult.isPresent() && purchaseResult.get() == ButtonType.YES)) {
                        conn.rollback();
                        return;
                    }

                    try (PreparedStatement deletePurchaseStmt = conn.prepareStatement(deletePurchaseSql)) {
                        deletePurchaseStmt.setInt(1, id);
                        deletePurchaseStmt.executeUpdate();
                    }
                }

                try (PreparedStatement stmt1 = conn.prepareStatement(deleteTeacherCoursesSql)) {
                    stmt1.setInt(1, id);
                    stmt1.executeUpdate();
                }

                try (PreparedStatement deleteClassesStmt = conn.prepareStatement(deleteClassesSql)) {
                    deleteClassesStmt.setInt(1, id);
                    deleteClassesStmt.executeUpdate();
                }

                try (PreparedStatement deleteCourseStmt = conn.prepareStatement(deleteCourseSql)) {
                    deleteCourseStmt.setInt(1, id);
                    int rowsAffected = deleteCourseStmt.executeUpdate();

                    if (rowsAffected > 0) {
                        conn.commit();
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Course deleted successfully.");
                        loadCoursesFromDatabase();
                    } else {
                        conn.rollback();
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete course.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Cannot delete course from database.");
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public void handleReload(ActionEvent actionEvent) {
        reloadCourseTable();
    }
    
    private void setupSearchFilter(){

        filteredCourse = new FilteredList<>(courseList, p -> true);
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


            System.out.println("Filtered : " + filteredCourse.size());
            currentPage = 0;
            renderPage(currentPage);
        });

        renderPage(currentPage);
    }

    private void renderPage(int page) {
        int start = page * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, filteredCourse.size());

        // Lấy danh sách con (subList) cho trang hiện tại
        List<Course> pageCourses = filteredCourse.subList(start, end);
        // Cập nhật dữ liệu vào TableView
        tableCourse.setItems(FXCollections.observableArrayList(pageCourses));
        addActionColumn();

        // Hiển thị log để kiểm tra dữ liệu hiển thị (có thể xóa sau khi hoàn tất)
        System.out.println("========== Page " + (page + 1) + " ==========");
        for (Course course : pageCourses) {
            System.out.println("Rendering course: " + course.getCourseName());
        }

        int totalPages = (int) Math.ceil((double) filteredCourse.size() / ITEMS_PER_PAGE);
        if (totalPages == 0) {
            totalPages = 1; // Đảm bảo luôn hiển thị ít nhất "Page 1 / 1"
        }
        pageIndicator.setText("Page " + (currentPage + 1) + " / " + totalPages);
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
