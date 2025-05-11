package com.center.academipro.controller.student;

import com.center.academipro.models.StudentTimetable;
import com.center.academipro.session.SessionManager;
import com.center.academipro.utils.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class StudentTimetableController implements Initializable {

    @FXML private TableView<StudentTimetable> timetableTable;
    @FXML private TableColumn<StudentTimetable, Integer> idColumn;
    @FXML private TableColumn<StudentTimetable, String> classColumn;
    @FXML private TableColumn<StudentTimetable, LocalDate> dateColumn;
    @FXML private TableColumn<StudentTimetable, LocalTime> startTimeCol;
    @FXML private TableColumn<StudentTimetable, LocalTime> endTimeCol;
    @FXML private TableColumn<StudentTimetable, String> statusColumn;

    private ObservableList<StudentTimetable> timetableList = FXCollections.observableArrayList();
    private int currentStudentId;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentStudentId = SessionManager.getInstance().getUserId();

        setupTableColumns();
        setupStatusColumnStyle();
        loadTimetable();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        classColumn.setCellValueFactory(new PropertyValueFactory<>("className"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        startTimeCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endTimeCol.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void setupStatusColumnStyle() {
        statusColumn.setCellFactory(column -> new TableCell<StudentTimetable, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);

                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);

                    switch (status) {
                        case "Present":
                            setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                            break;
                        case "Absent":
                            setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                            break;
                        case "Late":
                            setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                            break;
                        default:
                            setStyle("-fx-text-fill: gray;");
                    }
                }
            }
        });
    }

    private void loadTimetable() {
        timetableList.clear();

        String sql = "SELECT t.id, c.class_name, t.date, t.start_time, t.end_time, " +
                "COALESCE(a.status, 'Chưa điểm danh') AS status " +
                "FROM timetable t " +
                "JOIN classes c ON t.class_id = c.id " +
                "LEFT JOIN attendance a ON t.id = a.timetable_id AND a.student_id = ? " +
                "ORDER BY t.date, t.start_time";

        try (Connection conn = DBConnection.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, currentStudentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                StudentTimetable t = new StudentTimetable(
                        rs.getInt("id"),
                        rs.getString("class_name"),
                        rs.getDate("date").toLocalDate(),
                        rs.getTime("start_time").toLocalTime(),
                        rs.getTime("end_time").toLocalTime(),
                        rs.getString("status")
                );
                timetableList.add(t);
            }

            timetableTable.setItems(timetableList);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi database: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String message, Alert.AlertType type) {

    }
}