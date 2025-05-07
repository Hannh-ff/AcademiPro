package com.center.academipro.controller.teacher;

import com.center.academipro.controller.admin.studentManagement.EditStudentController;
import com.center.academipro.models.Student;
import com.center.academipro.models.Timetable;
import com.center.academipro.utils.DBConnection;
import com.center.academipro.utils.SceneSwitch;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class TimetableController {

    @FXML
    private TableView<Timetable> timetableTable;
    @FXML
    private TableColumn<Timetable, Integer> idColumn;
    @FXML
    private TableColumn<Timetable, String> classColumn;
    @FXML
    private TableColumn<Timetable, LocalDate> dateColumn;
    @FXML
    private TableColumn<Timetable, LocalTime> startTimeCol;
    @FXML
    private TableColumn<Timetable, LocalTime> endTimeCol;
    @FXML
    private TableColumn<Timetable, Void> actionColumn;
    private ObservableList<Timetable> timetableList = FXCollections.observableArrayList();

    public void initialize() {
        setupTableColumns();
        loadTimetables();
        setUpActionColumn();
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
                "JOIN classes c ON t.class_id = c.id";


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

        } catch (SQLException e) {
            e.printStackTrace();
        }

        timetableTable.setItems(timetableList);
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
                    Timetable timetable= getTableView().getItems().get(getIndex());
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
    private void handleUpdate(Timetable timetable) {
        if (timetable == null) {
            System.out.println("Student is null. Cannot open update view.");
            return;
        }

        try {
            FXMLLoader loader = SceneSwitch.loadView("view/admin/studentManagement/edit-student-view.fxml");
            if (loader != null) {
//                EditStudentController controller = loader.getController();
//                controller.setStudent(student); // Truyền dữ liệu ghế cần chỉnh sửa
//
//                Parent newView = loader.getRoot();
//                StackPane pane = (StackPane) tableView_Student.getScene().getRoot();
//                BorderPane mainPane = (BorderPane) pane.lookup("#mainBorderPane");
//
//                if (mainPane != null) {
//                    mainPane.setCenter(newView);
//                } else {
//                    System.err.println("BorderPane with ID 'mainBorderPane' not found");
//                }
            } else {
                System.err.println("Could not load edit-seat.fxml");
            }

            // Sau khi đóng form update, reload lại danh sách
            loadTimetables();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void handleDelete(Timetable timetable) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Confirmation");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete this student?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {

            }
        });
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(type.name());
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public void changeSceneAdd(ActionEvent event) {
        FXMLLoader loader = SceneSwitch.loadView("view/teacher/timetableManagement/add-timetable.fxml");
        if (loader != null) {
            Parent newView = loader.getRoot(); // Lấy Root từ FXMLLoader
            StackPane pane = (StackPane) ((Node) event.getSource()).getScene().getRoot();
            BorderPane mainPane = (BorderPane) pane.lookup("#mainBorderPane");
            mainPane.setCenter(newView); // Thay đổi nội dung của center
        } else {
            System.err.println("Failed to load addnew-user.fxml");
        }

    }


}
