package com.center.academipro.controller.teacher;

import com.center.academipro.models.Attendance;
import com.center.academipro.session.SessionManager;
import com.center.academipro.utils.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;

public class AttendanceHistoryController {
    @FXML private ComboBox<String> classCombox;
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;
    @FXML private ComboBox<String> statusFilterComboBox;
    @FXML private TableView<Attendance> attendanceTableView;
    @FXML private TableColumn<Attendance, String> dateColumn;
    @FXML private TableColumn<Attendance, String> studentNameColumn;
    @FXML private TableColumn<Attendance, String> statusColumn;
    @FXML private TableColumn<Attendance, String> notesColumn;
    @FXML private Button viewButton;
    @FXML private Button exportButton;
    @FXML private Label statusLabel;

    private ObservableList<Attendance> originalData = FXCollections.observableArrayList();
    private FilteredList<Attendance> filteredData;
    private int currentTeacherId;

    @FXML
    public void initialize() {
        currentTeacherId = SessionManager.getInstance().getUserId();
        setupTableColumns();
        loadClasses();
        setupStatusFilter();

        viewButton.setOnAction(e -> loadAttendanceHistory());
        statusFilterComboBox.setOnAction(e -> filterByStatus());

        filteredData = new FilteredList<>(originalData, p -> true);
        attendanceTableView.setItems(originalData);
    }

    private void setupTableColumns() {
        // Sửa cách bind dữ liệu không dùng PropertyValueFactory
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        studentNameColumn.setCellValueFactory(cellData -> cellData.getValue().studentNameProperty());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        notesColumn.setCellValueFactory(cellData -> cellData.getValue().notesProperty());


        // Định dạng ngày tháng
        dateColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String date, boolean empty) {
                super.updateItem(date, empty);
                setText(empty || date == null ? null : LocalDate.parse(date).toString());
            }
        });
    }
    private void loadClasses() {
        try (Connection conn = DBConnection.getConn();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT class_name FROM classes WHERE teacher_id = ? ORDER BY class_name")) {

            stmt.setInt(1, currentTeacherId);
            ResultSet rs = stmt.executeQuery();

            classCombox.getItems().clear();
            while (rs.next()) {
                classCombox.getItems().add(rs.getString("class_name"));
            }

            if (classCombox.getItems().isEmpty()) {
                statusLabel.setText("You don't have any assigned classes");
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load classes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupStatusFilter() {
        statusFilterComboBox.setItems(FXCollections.observableArrayList(
                "All", "Present", "Absent", "Late", "Excused"
        ));
        statusFilterComboBox.getSelectionModel().selectFirst();
    }

    private void loadAttendanceHistory() {
        String selectedClass = classCombox.getValue();
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();

        if (selectedClass == null || fromDate == null || toDate == null) {
            showAlert("Validation Error", "Please select class and date range");
            return;
        }

        if (toDate.isBefore(fromDate)) {
            showAlert("Validation Error", "End date must be after start date");
            return;
        }

        try (Connection conn = DBConnection.getConn()) {
            String query = """
                SELECT 
                    t.date,
                    u.fullname AS student_name,
                    a.status,
                    a.notes
                FROM attendance a
                JOIN timetable t ON a.timetable_id = t.id
                JOIN classes c ON t.class_id = c.id
                JOIN students s ON a.student_id = s.id
                JOIN users u ON s.user_id = u.id
                WHERE c.class_name = ?
                AND t.date BETWEEN ? AND ?
                ORDER BY t.date, u.fullname""";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, selectedClass);
                stmt.setDate(2, Date.valueOf(fromDate));
                stmt.setDate(3, Date.valueOf(toDate));

                ResultSet rs = stmt.executeQuery();
                originalData.clear();

                while (rs.next()) {
                    Attendance att = new Attendance(
                            0, // id không sử dụng
                            rs.getString("student_name"),
                            0, // timetableId không sử dụng
                            rs.getDate("date").toString()
                    );
                    att.setStatus(rs.getString("status"));
                    att.setNotes(rs.getString("notes"));

                    originalData.add(att);
                }

                filterByStatus();
                statusLabel.setText("Found " + originalData.size() + " records");
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load attendance history: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void filterByStatus() {
        String selectedStatus = statusFilterComboBox.getValue();

        if (selectedStatus == null || "All".equals(selectedStatus)) {
            filteredData.setPredicate(att -> true);
        } else {
            filteredData.setPredicate(att ->
                    selectedStatus.equalsIgnoreCase(att.getStatus())
            );
        }

        statusLabel.setText("Showing " + filteredData.size() + " filtered records");
    }

    private void exportToExcel() {
        if (filteredData.isEmpty()) {
            showAlert("Export Error", "No data to export");
            return;
        }

        // TODO: Implement export logic
        showAlert("Info", "Export feature will be implemented here");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}