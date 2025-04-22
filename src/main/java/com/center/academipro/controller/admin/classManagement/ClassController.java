package com.center.academipro.controller.admin.classManagement;

import com.center.academipro.models.Class;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class ClassController {

    @FXML private TableView<Class> tableClass;
    @FXML private TableColumn<Class, Integer> colClassId;
    @FXML private TableColumn<Class, String> colClassName;
    @FXML private TableColumn<Class, String> colDescription;
    @FXML private TableColumn<Class, String> colImage;
    @FXML private TableColumn<Class, Double> colPrice;
    @FXML private TableColumn<Class, Void> colAction;

    private final ObservableList<Class> classList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colClassId.setCellValueFactory(cell -> cell.getValue().idProperty().asObject());
        colClassName.setCellValueFactory(cell -> cell.getValue().classNameProperty());
        colDescription.setCellValueFactory(cell -> cell.getValue().descriptionProperty());
        colImage.setCellValueFactory(cell -> cell.getValue().imageProperty());
        colPrice.setCellValueFactory(cell -> cell.getValue().priceProperty().asObject());
        tableClass.setItems(classList);
        loadClassesFromDatabase();
        addActionColumn();
    }

    public void reloadClassTable() {
        classList.clear();
        loadClassesFromDatabase();
        tableClass.refresh();
    }

    private void loadClassesFromDatabase() {
        String query = "SELECT * FROM classes";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/academipro", "root", "");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Class classItem = new Class(
                        rs.getInt("id"),
                        rs.getString("class_name"),
                        rs.getString("description"),
                        rs.getString("image"),
                        rs.getDouble("price")
                );
                classList.add(classItem);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải danh sách lớp học.");
        }
    }

    @FXML
    private void addClass() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/center/academipro/view/admin/classManagement/add-new-class.fxml"));
            Parent root = loader.load();

            AddClassController controller = loader.getController();
            controller.setParentController(this);

            Stage stage = new Stage();
            stage.setTitle("Thêm lớp học");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể mở form thêm lớp học.");
        }
    }

    @FXML
    private void updateClass() {
        Class selectedClass = tableClass.getSelectionModel().getSelectedItem();

        if (selectedClass == null) {
            showAlert(Alert.AlertType.WARNING, "Chưa chọn lớp", "Vui lòng chọn một lớp để cập nhật.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/center/academipro/view/admin/classManagement/edit-class.fxml"));
            Parent root = loader.load();

            EditClassController controller = loader.getController();
            controller.setClassModel(selectedClass);
            controller.setParentController(this);

            Stage stage = new Stage();
            stage.setTitle("Cập nhật lớp học");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể mở form cập nhật lớp học.");
        }
    }

    @FXML
    private void deleteClass() {
        Class selectedClass = tableClass.getSelectionModel().getSelectedItem();
        if (selectedClass == null) {
            showAlert(Alert.AlertType.WARNING, "Chưa chọn lớp", "Vui lòng chọn một lớp để xóa.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc muốn xóa lớp học này?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Xác nhận xóa");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/academipro", "root", "");
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM classes WHERE id = ?")) {

                stmt.setInt(1, selectedClass.getId());
                stmt.executeUpdate();
                classList.remove(selectedClass);

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa lớp học.");
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

    private void addActionColumn() {
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("✏️");
            private final Button deleteBtn = new Button("🗑️");
            private final HBox actionBox = new HBox(10, editBtn, deleteBtn);

            {
                editBtn.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white;");
                deleteBtn.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white;");

                editBtn.setOnAction(event -> {
                    tableClass.getSelectionModel().select(getIndex());
                    updateClass();
                });

                deleteBtn.setOnAction(event -> {
                    tableClass.getSelectionModel().select(getIndex());
                    deleteClass();
                });
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
}
