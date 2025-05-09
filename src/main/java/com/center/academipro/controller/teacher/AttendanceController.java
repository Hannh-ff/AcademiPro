package com.center.academipro.controller.teacher;

import com.center.academipro.models.Attendance;
import com.center.academipro.session.SessionManager;
import com.center.academipro.utils.DBConnection;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;

public class AttendanceController {
    @FXML private ComboBox<String> classCombox;
    @FXML private DatePicker datePicker;
    @FXML private TableView<Attendance> studentTableView;
    @FXML private TableColumn<Attendance, String> studentCodeColumn;
    @FXML private TableColumn<Attendance, String> studentNameColumn;
    @FXML private TableColumn<Attendance, Boolean> presentColumn;
    @FXML private TableColumn<Attendance, Boolean> absentColumn;
    @FXML private TableColumn<Attendance, Boolean> lateColumn;
    @FXML private TableColumn<Attendance, Boolean> excusedColumn;
    @FXML private TableColumn<Attendance, String> notesColumn;
    @FXML private Button saveButton;
    @FXML private Button refreshButton;
    @FXML private Label statusLabel;

    private ObservableList<Attendance> attendanceList = FXCollections.observableArrayList();
    private int currentTeacherId;

    @FXML
    public void initialize() {
        currentTeacherId = SessionManager.getInstance().getUserId();

        setupTableColumns();
        loadClasses();
        setupDatePicker();
        setupEventHandlers();
        studentTableView.setEditable(true);

        saveButton.setOnAction(e -> saveAttendance());
        studentTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
    private void setupTableColumns() {
        studentNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStudentName())
        );

        // Các cột checkbox giữ nguyên
        presentColumn.setCellValueFactory(data -> data.getValue().presentProperty());
        presentColumn.setCellFactory(CheckBoxTableCell.forTableColumn(presentColumn));

        absentColumn.setCellValueFactory(data -> data.getValue().absentProperty());
        absentColumn.setCellFactory(CheckBoxTableCell.forTableColumn(absentColumn));

        lateColumn.setCellValueFactory(data -> data.getValue().lateProperty());
        lateColumn.setCellFactory(CheckBoxTableCell.forTableColumn(lateColumn));

        excusedColumn.setCellValueFactory(data -> data.getValue().excusedProperty());
        excusedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(excusedColumn));

        // Sửa lại phần notesColumn
        notesColumn.setCellValueFactory(cellData -> cellData.getValue().notesProperty());
        notesColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        notesColumn.setOnEditCommit(event -> {
            System.out.println("Note changed from '" + event.getOldValue()
                    + "' to '" + event.getNewValue() + "'");
            event.getRowValue().setNotes(event.getNewValue());
        });
    }
    private void loadClasses() {
        try (Connection conn = DBConnection.getConn();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT class_name FROM classes " +
                             "WHERE teacher_id = ? " +
                             "ORDER BY class_name")) {

            stmt.setInt(1, currentTeacherId);
            ResultSet rs = stmt.executeQuery();

            classCombox.getItems().clear();
            while (rs.next()) {
                classCombox.getItems().add(rs.getString("class_name"));
            }

            classCombox.getSelectionModel().selectedItemProperty().addListener(
                    (obs, oldVal, newVal) -> loadStudentsForSelectedClass());

        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load classes: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void setupDatePicker() {
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                try {
                    boolean isValid = isValidDateForSelectedClass(date);
                    setDisable(!isValid);
                } catch (SQLException e) {
                    setDisable(true);
                }
            }
        });

        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && classCombox.getValue() != null) {
                loadStudentsForSelectedClass();
            }
        });
    }
    private void setupEventHandlers() {
        classCombox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && datePicker.getValue() != null) {
                loadStudentsForSelectedClass();
            }
        });

        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && classCombox.getValue() != null) {
                loadStudentsForSelectedClass();
            }
        });
    }

    private boolean isValidDateForSelectedClass(LocalDate date) throws SQLException {
        String selectedClass = classCombox.getValue();
        if (selectedClass == null) return false;

        try (Connection conn = DBConnection.getConn();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT 1 FROM timetable t " +
                             "JOIN classes c ON t.class_id = c.id " +
                             "WHERE c.class_name = ? AND t.date = ?")) {

            stmt.setString(1, selectedClass);
            stmt.setDate(2, Date.valueOf(date));
            return stmt.executeQuery().next();
        }
    }

    private void loadStudentsForSelectedClass() {
        String selectedClass = classCombox.getValue();
        LocalDate selectedDate = datePicker.getValue();

        if (selectedClass == null || selectedDate == null) {
            statusLabel.setText("Please select both class and date");
            return;
        }

        try (Connection conn = DBConnection.getConn()) {
            int timetableId = getTimetableId(conn, selectedClass, selectedDate);
            if (timetableId == -1) {
                statusLabel.setText("No schedule for " + selectedDate.toString());
                attendanceList.clear();
                return;
            }

            // Load danh sách sinh viên
            String query = """
                SELECT s.id, u.fullname 
                FROM students s
                JOIN users u ON s.user_id = u.id
                JOIN student_classes sc ON s.id = sc.student_id
                JOIN classes c ON sc.class_id = c.id
                WHERE c.class_name = ?
                ORDER BY u.fullname""";

            attendanceList.clear();

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, selectedClass);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    Attendance att = new Attendance(
                            rs.getInt("id"),
                            rs.getString("fullname"),
                            timetableId,
                            selectedDate.toString()
                    );
                    attendanceList.add(att);
                }
            }

            // Load trạng thái điểm danh nếu có
            loadExistingAttendance(conn, timetableId);

            studentTableView.setItems(attendanceList);
            statusLabel.setText("Loaded " + attendanceList.size() + " students");

        } catch (SQLException e) {
            statusLabel.setText("Error loading students");
            e.printStackTrace();
        }
    }

    private void loadExistingAttendance(Connection conn, int timetableId) throws SQLException {
        String query = """
            SELECT student_id, status, notes 
            FROM attendance 
            WHERE timetable_id = ?""";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, timetableId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int studentId = rs.getInt("student_id");
                String status = rs.getString("status");
                String notes = rs.getString("notes");

                attendanceList.stream()
                        .filter(att -> att.getStudentId() == studentId)
                        .findFirst()
                        .ifPresent(att -> {
                            att.setStatus(status);
                            att.setNotes(notes);

                            // Cập nhật các checkbox
                            switch (status) {
                                case "Present" -> att.presentProperty().set(true);
                                case "Absent" -> att.absentProperty().set(true);
                                case "Late" -> att.lateProperty().set(true);
                                case "Excused" -> att.excusedProperty().set(true);

                            }
                            System.out.println("Loading notes for student " + studentId + ": " + notes);
                            att.setNotes(notes);
                        });

            }

        }
    }
    private int getTimetableId(Connection conn, String className, LocalDate date) throws SQLException {
        String sql = """
            SELECT t.id 
            FROM timetable t
            JOIN classes c ON t.class_id = c.id
            WHERE c.class_name = ? AND t.date = ?
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, className);
            stmt.setDate(2, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt("id") : -1;
        }
    }

    private void saveAttendance() {
        String selectedClass = classCombox.getValue();
        LocalDate selectedDate = datePicker.getValue();

        if (selectedClass == null || selectedDate == null) {
            showAlert("Validation Error", "Please select both class and date");
            return;
        }

        if (attendanceList.isEmpty()) {
            showAlert("Validation Error", "No students to save");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Save");
        confirmation.setHeaderText("Save Attendance Records");
        confirmation.setContentText("Are you sure you want to save attendance for " +
                attendanceList.size() + " students?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        try (Connection conn = DBConnection.getConn()) {
            conn.setAutoCommit(false);

            int timetableId = getTimetableId(conn, selectedClass, selectedDate);
            if (timetableId == -1) {
                showAlert("Error", "Invalid timetable selected");
                return;
            }

            String sql = """
                INSERT INTO attendance 
                (timetable_id, student_id, status, notes, recorded_by)
                VALUES (?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE 
                status = VALUES(status),
                notes = VALUES(notes),
                recorded_by = VALUES(recorded_by),
                recorded_at = CURRENT_TIMESTAMP
                """;

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                int currentUserId = getCurrentUserId();

                for (Attendance att : attendanceList) {
                    if (att.getStatus() == null || att.getStatus().isEmpty()) {
                        continue;
                    }

                    stmt.setInt(1, timetableId);
                    stmt.setInt(2, att.getStudentId());
                    stmt.setString(3, att.getStatus());
                    stmt.setString(4, att.getNotes());
                    stmt.setInt(5, currentUserId);
                    stmt.addBatch();
                }

                int[] results = stmt.executeBatch();
                conn.commit();

                int savedCount = 0;
                for (int r : results) {
                    if (r >= 0 || r == Statement.SUCCESS_NO_INFO) {
                        savedCount++;
                    }
                }

                statusLabel.setText("Successfully saved " + savedCount + " records");
                showAlert("Success", "Attendance records saved successfully");
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to save attendance: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void refreshData() {
        loadClasses();
        if (classCombox.getValue() != null && datePicker.getValue() != null) {
            loadStudentsForSelectedClass();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private int getCurrentUserId() {
        // Implement your authentication logic here
        return 1; // Temporary - replace with actual user ID from session
    }
}