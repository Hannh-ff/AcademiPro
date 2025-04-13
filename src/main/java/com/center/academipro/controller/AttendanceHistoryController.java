package com.center.academipro.controller;

import com.center.academipro.models.Attendance;
import com.center.academipro.utils.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;
import java.time.LocalDate;

public class AttendanceHistoryController {

    @FXML private ComboBox<String> classCombox;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> statusFilterComboBox;
    @FXML private TableView<Attendance> studentTableView;
    @FXML private TableColumn<Attendance, String> studentNameColumn;
    @FXML private TableColumn<Attendance, String> statusColumn;
    @FXML private Button viewButton;

    public void initialize() {
        loadClasses();
        statusFilterComboBox.setItems(FXCollections.observableArrayList("All", "Present", "Absent"));
        viewButton.setOnAction(e -> loadAttendanceHistory());

        studentNameColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadClasses() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT name FROM classes")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                classCombox.getItems().add(rs.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAttendanceHistory() {
        ObservableList<Attendance> historyList = FXCollections.observableArrayList();
        String selectedClass = classCombox.getValue();
        LocalDate selectedDate = datePicker.getValue();
        String statusFilter = statusFilterComboBox.getValue();

        if (selectedClass == null || selectedDate == null) return;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT s.name AS student_name, a.status " +
                             "FROM attendance a JOIN students s ON a.student_id = s.id " +
                             "JOIN classes c ON a.class_id = c.id " +
                             "WHERE c.name = ? AND a.attendance_date = ?")) {
            stmt.setString(1, selectedClass);
            stmt.setDate(2, Date.valueOf(selectedDate));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String status = rs.getString("status");
                if ("All".equals(statusFilter) || status.equalsIgnoreCase(statusFilter)) {
                    historyList.add(new Attendance(0, 0, 0, rs.getString("student_name"), null, status));
                }
            }

            studentTableView.setItems(historyList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
