package com.center.academipro.controller.student;

import com.center.academipro.utils.DBConnection;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert.AlertType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class PaymentController {

    @FXML
    private RadioButton cashPayment;

    @FXML
    private RadioButton onlinePayment;

    @FXML
    private Label courseName;

    @FXML
    private Label priceCourse;

    @FXML
    private Label amountPayment;

    @FXML
    private Label total;

    @FXML
    private ImageView imageCourse;

    @FXML
    private Text descripCousre;

    private ToggleGroup paymentMethodGroup;

    // ID của học sinh và khóa học - bạn phải truyền giá trị này vào trước khi thanh toán(hoàng nhớ truyen vào)
    private int studentId;
    private int courseId;

    public void initialize() {
        paymentMethodGroup = new ToggleGroup();
        cashPayment.setToggleGroup(paymentMethodGroup);
        onlinePayment.setToggleGroup(paymentMethodGroup);
    }

    public void setStudentAndCourse(int studentId, int courseId) {
        this.studentId = studentId;
        this.courseId = courseId;
    }

    @FXML
    private void handlePayment(ActionEvent event) {
        if (cashPayment.isSelected()) {
            payByCash();
        } else if (onlinePayment.isSelected()) {
            payOnline(); // Nếu sau này bạn cần thanh toán online
        } else {
            showAlert(AlertType.WARNING, "Payment Method", "Please select a payment method!");
        }
    }

    private void payByCash() {
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "INSERT INTO payments (student_id, course_id, amount, payment_method, payment_status, payment_date) VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            pstmt.setBigDecimal(3, new java.math.BigDecimal(total.getText()));
            pstmt.setString(4, "Cash");
            pstmt.setString(5, "Completed");
            pstmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                showAlert(AlertType.INFORMATION, "Payment Successful", "Cash payment completed successfully!");
            } else {
                showAlert(AlertType.ERROR, "Payment Failed", "Payment failed. Please try again.");
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "An error occurred during payment: " + e.getMessage());
        }
    }

    private void payOnline() {
        showAlert(AlertType.INFORMATION, "Coming Soon", "Online payment not implemented yet.");
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

