package com.center.academipro.controller;

import com.center.academipro.models.Payment;
import com.center.academipro.models.Enrollment;
import com.center.academipro.session.SessionManager;
import com.center.academipro.utils.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;
import java.time.LocalDate;

public class PaymentController {

    @FXML
    private TableView<Enrollment> enrollmentTable;
    @FXML
    private TableColumn<Enrollment, String> courseNameColumn;
    @FXML
    private TableColumn<Enrollment, String> statusColumn;
    @FXML
    private Button payButton;

    private ObservableList<Enrollment> enrollments = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        courseNameColumn.setCellValueFactory(cellData -> cellData.getValue().courseNameProperty());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        loadEnrollments();
    }

    private void loadEnrollments() {
        enrollments.clear();
        String username = SessionManager.getInstance().getUsername();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT e.id, c.name, e.status FROM enrollments e JOIN classes cl ON e.class_id = cl.id " +
                             "JOIN courses c ON cl.course_id = c.id WHERE e.student_id = ?")) {

            stmt.setInt(1, Integer.parseInt(username));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int enrollmentId = rs.getInt("id");
                String courseName = rs.getString("name");
                String status = rs.getString("status");
                enrollments.add(new Enrollment(enrollmentId, courseName, status));
            }
            enrollmentTable.setItems(enrollments);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handlePayment() {
        Enrollment selected = enrollmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Vui lòng chọn một khóa học để thanh toán!");
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO payments (enrollment_id, amount, payment_date, method) VALUES (?, ?, ?, ?)")) {
            stmt.setInt(1, selected.getId());
            stmt.setDouble(2, 1000000); // giá giả định
            stmt.setDate(3, Date.valueOf(LocalDate.now()));
            stmt.setString(4, "Tiền mặt");

            stmt.executeUpdate();

            updateEnrollmentStatus(selected.getId());
            showAlert("Thanh toán thành công!");
            loadEnrollments();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateEnrollmentStatus(int enrollmentId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE enrollments SET status = 'paid' WHERE id = ?")) {
            stmt.setInt(1, enrollmentId);
            stmt.executeUpdate();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
