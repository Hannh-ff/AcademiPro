package com.center.academipro.controller.teacher;


import com.center.academipro.models.Class;
import com.center.academipro.models.Timetable;
import com.center.academipro.utils.DBConnection;
import com.center.academipro.utils.SceneSwitch;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.event.ActionEvent;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.io.IOException;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

public class AddTimetableController implements Initializable {

    @FXML private ComboBox<Class> classComboBox;
    @FXML private DatePicker datePicker;
    @FXML private TextField startTimeField;
    @FXML private TextField endTimeField;
    @FXML private Button addButton;
    @FXML private Button clearButton;


    private ObservableList<Class> classList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadClasses();
        setupTimeFields();
    }

    private void loadClasses() {
        classList.clear();

        String sql = "SELECT id, class_name FROM classes";

        try (Connection conn = DBConnection.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Class c = new Class(rs.getInt("id"), rs.getString("class_name"));
                classList.add(c);
            }

            classComboBox.setItems(classList);
            classComboBox.setCellFactory(param -> new ListCell<Class>() {
                @Override
                protected void updateItem(Class item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getClassName());
                    }
                }
            });

            classComboBox.setButtonCell(new ListCell<Class>() {
                @Override
                protected void updateItem(Class item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getClassName());
                    }
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupTimeFields() {
        // Kiểm tra null trước khi sử dụng
        if (startTimeField == null || endTimeField == null) {
            System.err.println("Lỗi: Các trường thời gian chưa được khởi tạo!");
            return;
        }

        // Thiết lập prompt text
        startTimeField.setPromptText("HH:mm");
        endTimeField.setPromptText("HH:mm");

        // Thêm listener để validate định dạng thời gian
        startTimeField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$") && !newVal.isEmpty()) {
                startTimeField.setStyle("-fx-border-color: red;");
            } else {
                startTimeField.setStyle("");
            }
        });

        endTimeField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$") && !newVal.isEmpty()) {
                endTimeField.setStyle("-fx-border-color: red;");
            } else {
                endTimeField.setStyle("");
            }
        });
    }
    @FXML
    private void handleAdd() {
        if (validateInput()) {
            try {
                Class selectedClass = classComboBox.getValue();
                LocalDate date = datePicker.getValue();
                LocalTime startTime = LocalTime.parse(startTimeField.getText());
                LocalTime endTime = LocalTime.parse(endTimeField.getText());

                if (endTime.isBefore(startTime) || endTime.equals(startTime)) {
                    showAlert("End time must be after start time", Alert.AlertType.ERROR);
                    return;
                }

                String sql = "INSERT INTO timetable (class_id, date, start_time, end_time) VALUES (?, ?, ?, ?)";

                try (Connection conn = DBConnection.getConn();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {

                    stmt.setInt(1, selectedClass.getId());
                    stmt.setDate(2, Date.valueOf(date));
                    stmt.setTime(3, Time.valueOf(startTime));
                    stmt.setTime(4, Time.valueOf(endTime));

                    int affectedRows = stmt.executeUpdate();

                    if (affectedRows > 0) {
                        showAlert("Timetable added successfully!", Alert.AlertType.INFORMATION);
                        clearFields();
                    } else {
                        showAlert("Failed to add timetable", Alert.AlertType.ERROR);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Database error: " + e.getMessage(), Alert.AlertType.ERROR);
            } catch (DateTimeParseException e) {
                showAlert("Invalid time format. Please use HH:mm format", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleClear() {
        clearFields();
    }

    private boolean validateInput() {
        if (classComboBox.getValue() == null) {
            showAlert("Please select a class", Alert.AlertType.ERROR);
            return false;
        }

        if (datePicker.getValue() == null) {
            showAlert("Please select a date", Alert.AlertType.ERROR);
            return false;
        }

        if (startTimeField.getText().isEmpty() || !startTimeField.getText().matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            showAlert("Please enter a valid start time (HH:mm)", Alert.AlertType.ERROR);
            return false;
        }

        if (endTimeField.getText().isEmpty() || !endTimeField.getText().matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            showAlert("Please enter a valid end time (HH:mm)", Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    public void clearFields() {
        classComboBox.getSelectionModel().clearSelection();
        datePicker.setValue(null);
        startTimeField.clear();
        endTimeField.clear();
    }
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            // Load the timetable view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/center/academipro/view/teacher/timetableManagement/timetable-view.fxml"));
            Parent root = loader.load();

            // Get reference to main BorderPane
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
            showAlert("Error loading timetable view: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(type.name());
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}