package com.center.academipro.controller.teacher;

import com.center.academipro.models.Attendance;
import com.center.academipro.utils.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;

import java.sql.*;
import java.time.LocalDate;

public class AttendanceController {

    @FXML private ComboBox<String> classCombox;
    @FXML private DatePicker datePicker;
    @FXML private TableView<Attendance> studentTableView;
    @FXML private TableColumn<Attendance, String> studentNameColumn;
    @FXML private TableColumn<Attendance, Boolean> presentColumn;
    @FXML private TableColumn<Attendance, Boolean> absentColumn;
    @FXML private Button saveButton;

    private ObservableList<Attendance> attendanceList = FXCollections.observableArrayList();

    public void initialize() {
        loadClasses();
        loadAllStudents();
        // Cột họ tên
        studentNameColumn.setCellValueFactory(data -> data.getValue().studentNameProperty());
        studentTableView.setEditable(true);
        presentColumn.setEditable(true);
        absentColumn.setEditable(true);


        // Cột checkbox "Có mặt"
        presentColumn.setCellValueFactory(data -> data.getValue().presentProperty());
        presentColumn.setCellFactory(col -> {
            CheckBoxTableCell<Attendance, Boolean> cell = new CheckBoxTableCell<>(index -> {
                Attendance att = studentTableView.getItems().get(index);
                return att.presentProperty();
            });
            return cell;
        });

        // Cột checkbox "Vắng mặt"
        absentColumn.setCellValueFactory(data -> data.getValue().absentProperty());
        absentColumn.setCellFactory(col -> {
            CheckBoxTableCell<Attendance, Boolean> cell = new CheckBoxTableCell<>(index -> {
                Attendance att = studentTableView.getItems().get(index);
                return att.absentProperty();
            });
            return cell;
        });

        // Khi chọn lớp thì load danh sách sinh viên
        classCombox.setOnAction(e -> loadStudentsForSelectedClass());


        // Lưu điểm danh khi nhấn nút
        saveButton.setOnAction(e -> saveAttendance());
    }
    private void loadAllStudents() {
        attendanceList.clear();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT s.id, u.fullname " +
                             "FROM students s " +
                             "JOIN users u ON s.user_id = u.id")) {

            ResultSet rs = stmt.executeQuery();
            boolean hasData = false;

            while (rs.next()) {
                hasData = true;
                int studentId = rs.getInt("id");
                String fullName = rs.getString("fullname");

                System.out.println("Student ID: " + studentId + ", Full Name: " + fullName);

                attendanceList.add(new Attendance(studentId, fullName));
            }

            if (!hasData) {
                System.out.println("⚠ Không có sinh viên nào trong hệ thống.");
            }

            studentTableView.setItems(attendanceList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadClasses() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT class_name FROM classes")) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                classCombox.getItems().add(rs.getString("class_name"));
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
                     "SELECT s.id, u.fullname " +
                             "FROM students s " +
                             "JOIN users u ON s.user_id = u.id " +
                             "JOIN classes c ON s.class_id = c.id " +
                             "WHERE c.class_name LIKE ?")) {

            stmt.setString(1, "%" + selectedClass + "%");
            ResultSet rs = stmt.executeQuery();

            boolean hasData = false;

            while (rs.next()) {
                hasData = true;
                int studentId = rs.getInt("id");
                String fullName = rs.getString("fullname");

                System.out.println("Student ID: " + studentId + ", Full Name: " + fullName);

                attendanceList.add(new Attendance(studentId, fullName));
            }

            if (!hasData) {
                System.out.println("⚠ Không tìm thấy học sinh nào cho lớp: " + selectedClass);
            }

            studentTableView.setItems(attendanceList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveAttendance() {
        LocalDate date = datePicker.getValue();
        String className = classCombox.getValue();

        if (date == null || className == null) {
            new Alert(Alert.AlertType.WARNING, "Vui lòng chọn ngày và lớp học!").show();
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            int classId = getClassIdByName(conn, className);
            if (classId == -1) {
                new Alert(Alert.AlertType.ERROR, "Không tìm thấy lớp học.").show();
                return;
            }

            for (Attendance att : attendanceList) {
                String status = att.getStatus(); // "present", "absent", hoặc "unknown"

                if (status.equals("unknown")) {
                    System.out.println("⚠ Sinh viên chưa điểm danh: " + att.getStudentName());
                    continue;
                }

                try (PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO attendance (class_id, student_id, attendance_date, status) " +
                                "VALUES (?, ?, ?, ?)")) {

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
            new Alert(Alert.AlertType.ERROR, "Lỗi khi lưu điểm danh!").show();
        }
    }

    private int getClassIdByName(Connection conn, String className) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT id FROM classes WHERE class_name = ?")) {

            stmt.setString(1, className);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return -1;
    }
}
