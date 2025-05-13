package com.center.academipro.controller.admin.classManagement;

import com.center.academipro.controller.admin.courseManagement.EditCourseController;
import com.center.academipro.models.Class;
import com.center.academipro.utils.SceneSwitch;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.Optional;

public class ClassController {

    @FXML
    private TableView<Class> tableClass;
    @FXML
    private TableColumn<Class, Integer> colClassId;
    @FXML
    private TableColumn<Class, String> colClassName;
    @FXML
    private TableColumn<Class, String> colCourseName;
    @FXML
    private TableColumn<Class, String> colTeacherName;
    @FXML
    private TableColumn<Class, Integer> colStudentCount;
    @FXML
    private TableColumn<Class, Void> colAction;
    @FXML
    private TextField searchField;
    @FXML
    private Label pageIndicator;


    private final ObservableList<Class> classList = FXCollections.observableArrayList();
    private FilteredList<Class> filteredClass;
    private static final int ITEMS_PER_PAGE = 4;
    private int currentPage = 0;

    @FXML
    public void initialize() {
        colClassId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        colClassName.setCellValueFactory(cellData -> cellData.getValue().classNameProperty());
        colCourseName.setCellValueFactory(cellData -> cellData.getValue().courseNameProperty());
        colTeacherName.setCellValueFactory(cellData -> cellData.getValue().teacherNameProperty());
        colStudentCount.setCellValueFactory(cellData -> cellData.getValue().studentCountProperty().asObject());

        addActionColumn();
        loadClassesFromDatabase();
        setupSearchFilter();
        tableClass.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    }

    public void reloadClassTable() {
        loadClassesFromDatabase();
    }

    private void loadClassesFromDatabase() {
        classList.clear();

        String query = """
                SELECT 
                    c.id AS class_id, 
                    c.class_name, 
                    co.course_name, 
                    u.fullname AS teacher_name,
                    (SELECT COUNT(*) FROM student_classes sc WHERE sc.class_id = c.id) AS student_count
                FROM classes c
                JOIN courses co ON c.course_id = co.id
                LEFT JOIN users u ON c.teacher_id = u.id
                """;

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/academipro", "root", "");
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Class classItem = new Class(
                        rs.getInt("class_id"),
                        rs.getString("class_name"),
                        rs.getString("teacher_name"),
                        rs.getString("course_name"),
                        rs.getInt("student_count")
                );
                classList.add(classItem);
            }

            if (filteredClass != null) {
                // Nếu filteredClass đã khởi tạo rồi thì chỉ cần render lại
                currentPage = 0;
                renderPage(currentPage);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot load classes from database.");
        }
    }

    @FXML
    private void addClass(ActionEvent event) {
        FXMLLoader loader = SceneSwitch.loadView("view/admin/classManagement/add-new-class.fxml");
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
                    Class classes = getTableView().getItems().get(getIndex());
                    updateClass(classes);
                });

                deleteBtn.setOnAction(event -> {
                    Class classes = getTableView().getItems().get(getIndex());
                    deleteClass(classes);
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
    private void updateClass(Class classes) {
        if (classes == null) {
            System.out.println("Class is null. Cannot open update class.");
            return;
        }

        try {
            FXMLLoader loader = SceneSwitch.loadView("view/admin/classManagement/edit-class.fxml");
            if (loader != null) {
                EditClassController controller = loader.getController();
                controller.setClass(classes);// Truyền dữ liệu ghế cần chỉnh sửa

                Parent newView = loader.getRoot();
                StackPane pane = (StackPane) tableClass.getScene().getRoot();
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
    private void deleteClass(Class classes) {
        if (classes == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure want to delete this class?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Delete Confirmation");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/academipro", "root", "");
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM classes WHERE id = ?")) {

                stmt.setInt(1, classes.getId());
                int rowsAffected = stmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Class deleted successfully.");
                    loadClassesFromDatabase();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete class.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete class.");
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

    private void setupSearchFilter(){

        filteredClass = new FilteredList<>(classList, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Search: " + newValue);

            filteredClass.setPredicate(classes ->{
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                boolean match = classes.getClassName().toLowerCase().contains(lowerCaseFilter);

                if (match) {
                    System.out.println("Matched: " + classes.getClassName());
                }
                return match;
            });

            System.out.println("Filtered : " + filteredClass.size());
            currentPage = 0;
            renderPage(currentPage);
        });

        renderPage(currentPage);
    }

    private void renderPage(int page) {
        int start = page * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, filteredClass.size());

        // Lấy danh sách con (subList) cho trang hiện tại
        List<Class> pageClasses = filteredClass.subList(start, end);
        // Cập nhật dữ liệu vào TableView
        tableClass.setItems(FXCollections.observableArrayList(pageClasses));
        addActionColumn();

        // Hiển thị log để kiểm tra dữ liệu hiển thị (có thể xóa sau khi hoàn tất)
        System.out.println("========== Page " + (page + 1) + " ==========");
        for (Class classes : pageClasses) {
            System.out.println("Rendering class: " + classes.getClassName());
        }

        int totalPages = (int) Math.ceil((double) filteredClass.size() / ITEMS_PER_PAGE);
        if (totalPages == 0) {
            totalPages = 1; // Đảm bảo luôn hiển thị ít nhất "Page 1 / 1"
        }
        pageIndicator.setText("Page " + (currentPage + 1) + " / " + totalPages);
    }

    @FXML
    private void handleNextPage() {
        if ((currentPage + 1) * ITEMS_PER_PAGE < filteredClass.size()) {
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
