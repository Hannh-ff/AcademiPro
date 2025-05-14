package com.center.academipro.controller.teacher;

import com.center.academipro.models.Class;
import com.center.academipro.models.Timetable;
import com.center.academipro.session.SessionManager;
import com.center.academipro.utils.DBConnection;
import com.center.academipro.utils.SceneSwitch;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class TimetableController {

    @FXML private TableView<Timetable> timetableTable;
    @FXML private TableColumn<Timetable, Integer> idColumn;
    @FXML private TableColumn<Timetable, String> classColumn;
    @FXML private TableColumn<Timetable, LocalDate> dateColumn;
    @FXML private TableColumn<Timetable, LocalTime> startTimeCol;
    @FXML private TableColumn<Timetable, LocalTime> endTimeCol;
    @FXML private TableColumn<Timetable, Void> actionColumn;
    @FXML private TextField searchField;

    private ObservableList<Timetable> timetableList = FXCollections.observableArrayList();
    private FilteredList<Timetable> filteredData;

    public void initialize() {

        setupTableColumns();
        loadTimetables();
        setUpActionColumn();
        setupSearchFilter();
        timetableTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        classColumn.setCellValueFactory(new PropertyValueFactory<>("className"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        startTimeCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endTimeCol.setCellValueFactory(new PropertyValueFactory<>("endTime"));
    }

    private void loadTimetables() {
        timetableList.clear();

        String sql = "SELECT t.id, c.class_name, t.date, t.start_time, t.end_time " +
                "FROM timetable t " +
                "JOIN classes c ON t.class_id = c.id " +
                "ORDER BY t.date, t.start_time";

        try (Connection conn = DBConnection.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Timetable t = new Timetable(
                        rs.getInt("id"),
                        rs.getString("class_name"),
                        rs.getDate("date").toLocalDate(),
                        rs.getTime("start_time").toLocalTime(),
                        rs.getTime("end_time").toLocalTime()
                );
                timetableList.add(t);
            }

            timetableTable.setItems(timetableList);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database error: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void setupSearchFilter() {
        filteredData = new FilteredList<>(timetableList, p -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(timetable -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (timetable.getClassName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (timetable.getDate().toString().contains(lowerCaseFilter)) {
                    return true;
                } else if (timetable.getStartTime().toString().contains(lowerCaseFilter)) {
                    return true;
                } else if (timetable.getEndTime().toString().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });

        SortedList<Timetable> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(timetableTable.comparatorProperty());
        timetableTable.setItems(sortedData);
    }

    private void setUpActionColumn() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button updateBtn = new Button("Update");
            private final Button deleteBtn = new Button("Delete");
            private final HBox btnBox = new HBox(10, updateBtn, deleteBtn);

            {
                updateBtn.setOnAction(event -> {
                    Timetable timetable = getTableView().getItems().get(getIndex());
                    handleUpdate(timetable);
                });

                deleteBtn.setOnAction(event -> {
                    Timetable timetable = getTableView().getItems().get(getIndex());
                    handleDelete(timetable);
                });

                updateBtn.setStyle("-fx-background-color: linear-gradient(to bottom right, #a18cd1, #fbc2eb); -fx-text-fill: white;");
                deleteBtn.setStyle("-fx-background-color: linear-gradient(to bottom right, #ff1e56, #ff4b2b); -fx-text-fill: white;");
                btnBox.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnBox);
                }
            }
        });
    }

    @FXML
    private void handleUpdate(Timetable timetable) {
        try {
            // Load the edit-timetable view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/center/academipro/view/teacher/timetableManagement/edit-timetable-view.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the timetable data
            EditTimetableController controller = loader.getController();
            controller.setTimetable(timetable);

            // Get reference to main BorderPane
            BorderPane mainBorderPane = (BorderPane) timetableTable.getScene().lookup("#mainBorderPane");
            if (mainBorderPane != null) {
                mainBorderPane.setCenter(root);
            } else {
                // Alternative approach if BorderPane isn't found
                Scene currentScene = timetableTable.getScene();
                currentScene.setRoot(root);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error loading edit form: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    private void handleDelete(Timetable timetable) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Confirmation");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete this timetable entry?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String sql = "DELETE FROM timetable WHERE id = ?";

                try (Connection conn = DBConnection.getConn();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {

                    stmt.setInt(1, timetable.getId());
                    int affectedRows = stmt.executeUpdate();

                    if (affectedRows > 0) {
                        showAlert("Timetable deleted successfully", Alert.AlertType.INFORMATION);
                        loadTimetables(); // Refresh the table
                    } else {
                        showAlert("Failed to delete timetable", Alert.AlertType.ERROR);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Database error: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }

    @FXML
    private void handleReload() {
        loadTimetables();
        searchField.clear();
    }
    @FXML
    private void changeSceneAdd(ActionEvent event) {
        try {
            // Load the add-timetable view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/center/academipro/view/teacher/timetableManagement/add-timetable.fxml"));
            Parent root = loader.load();

            // Get reference to main BorderPane (assuming your main layout uses BorderPane)
            BorderPane mainBorderPane = (BorderPane) ((Node) event.getSource()).getScene().lookup("#mainBorderPane");

            if (mainBorderPane != null) {
                mainBorderPane.setCenter(root);
            } else {
                // Alternative approach if BorderPane isn't found
                Scene currentScene = ((Node) event.getSource()).getScene();
                currentScene.setRoot(root);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error loading add timetable form: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(type.name());
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}