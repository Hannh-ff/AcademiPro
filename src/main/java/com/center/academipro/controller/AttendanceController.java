package com.center.academipro.controller;


import com.center.academipro.models.Attendance;
import com.center.academipro.utils.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;
import java.time.LocalDate;

public class AttendanceController {

    @FXML private ComboBox<String> classCombox;
    @FXML private DatePicker datePicker;
    @FXML private TableView<Attendance> studentTableView;
    @FXML private TableColumn<Attendance, String> studentNameColumn;
    @FXML private TableColumn<Attendance, String> presentColumn;
    @FXML private TableColumn<Attendance, String> absentColumn;
    @FXML private Button saveButton;

    private ObservableList<Attendance> attendanceList = FXCollections.observableArrayList();

    public void initialize() {
        loadClasses();
        studentNameColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));

        presentColumn.setCellFactory(tc -> new CheckBoxTableCell<>());
        absentColumn.setCellFactory(tc -> new CheckBoxTableCell<>());

        classCombox.setOnAction(e -> loadStudentsForSelectedClass());
        saveButton.setOnAction(e -> saveAttendance());
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

    private void loadStudentsForSelectedClass() {
        attendanceList.clear();
        String selectedClass = classCombox.getValue();

        if (selectedClass == null) return;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT s.id, s.name FROM students s JOIN classes c ON s.class_id = c.id WHERE c.name = ?")) {
            stmt.setString(1, selectedClass);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                attendanceList.add(new Attendance(rs.getInt("id"), rs.getString("name")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        studentTableView.setItems(attendanceList);
    }

    private void saveAttendance() {
        LocalDate date = datePicker.getValue();
        String className = classCombox.getValue();

        if (date == null || className == null) return;

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            int classId = getClassIdByName(conn, className);

            for (Attendance att : attendanceList) {
                String status = att.getStatus(); // You can link this with CheckBoxes logic
                try (PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO attendance (class_id, student_id, attendance_date, status) VALUES (?, ?, ?, ?)")) {
                    stmt.setInt(1, classId);
                    stmt.setInt(2, att.getStudentId());
                    stmt.setDate(3, Date.valueOf(date));
                    stmt.setString(4, status);
                    stmt.executeUpdate();
                }
            }

            conn.commit();
            new Alert(Alert.AlertType.INFORMATION, "Lưu điểm danh thành công!").show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getClassIdByName(Connection conn, String className) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT id FROM classes WHERE name = ?")) {
            stmt.setString(1, className);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("id");
        }
        return -1;
    }
}
