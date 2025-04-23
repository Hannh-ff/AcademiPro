package com.center.academipro.controller.teacher;

import com.center.academipro.models.Attendance;
import com.center.academipro.utils.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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

    @FXML private Button prevPageButton;
    @FXML private Label pageLabel;
    @FXML private Button nextPageButton;

    private FilteredList<Attendance> filteredData;
    private ObservableList<Attendance> originalData = FXCollections.observableArrayList();

    public void initialize() {
        loadClasses();

        // Thiết lập combobox lọc trạng thái
        statusFilterComboBox.setItems(FXCollections.observableArrayList(
                "Tất cả", "Present", "Absent"
        ));
        statusFilterComboBox.getSelectionModel().selectFirst();

        // Thiết lập binding dữ liệu
        studentNameColumn.setCellValueFactory(cellData -> cellData.getValue().studentNameProperty());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        // Khởi tạo filteredData
        filteredData = new FilteredList<>(originalData, p -> true);
        studentTableView.setItems(filteredData);

        viewButton.setOnAction(e -> loadAttendanceHistory());
        statusFilterComboBox.setOnAction(e -> filterByStatus());
    }

    private void filterByStatus() {
        String selectedStatus = statusFilterComboBox.getValue();

        if (selectedStatus == null || "Tất cả".equals(selectedStatus)) {
            filteredData.setPredicate(attendance -> true);
        } else {
            filteredData.setPredicate(attendance ->
                    selectedStatus.equalsIgnoreCase(attendance.getStatus())
            );
        }
    }

    private void loadClasses() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT class_name FROM classes")) {

            ResultSet rs = stmt.executeQuery();
            ObservableList<String> classes = FXCollections.observableArrayList();
            while (rs.next()) {
                classes.add(rs.getString("class_name"));
            }
            classCombox.setItems(classes);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể tải danh sách lớp: " + e.getMessage());
        }
    }

    private void loadAttendanceHistory() {
        String selectedClass = classCombox.getValue();
        LocalDate selectedDate = datePicker.getValue();

        if (selectedClass == null || selectedDate == null) {
            showAlert("Cảnh báo", "Vui lòng chọn cả lớp và ngày");
            return;
        }

        originalData.clear();

        try (Connection conn = DBConnection.getConnection()) {
            int classId = getClassId(conn, selectedClass);
            if (classId == -1) return;

            String query = "SELECT s.fullname AS student_name, a.status AS attendance_status " +
                    "FROM attendance a " +
                    "JOIN students st ON a.student_id = st.id " +
                    "JOIN users s ON st.user_id = s.id " +
                    "WHERE a.attendance_date = ? AND a.class_id = ?";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setDate(1, Date.valueOf(selectedDate));
                stmt.setInt(2, classId);

                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    String studentName = rs.getString("student_name");
                    String status = rs.getString("attendance_status");
                    originalData.add(new Attendance(0, classId, 0, studentName, selectedDate.toString(), status));
                }
            }

            filterByStatus();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể tải lịch sử điểm danh: " + e.getMessage());
        }
    }

    private int getClassId(Connection conn, String className) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT id FROM classes WHERE class_name = ?")) {
            stmt.setString(1, className);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt("id") : -1;
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}