package com.center.academipro.controller.student;

import com.center.academipro.models.Course;
import com.center.academipro.models.Payment;
import com.center.academipro.models.Student;
import com.center.academipro.session.SessionCourse;
import com.center.academipro.session.SessionManager;
import com.center.academipro.utils.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert.AlertType;

import java.sql.*;
import java.time.LocalDateTime;

public class PaymentController {

    @FXML
    public TableColumn<Payment, String> studenNameCol;
    @FXML
    public TableColumn<Payment, String> courseNameCol;
    @FXML
    public TableColumn<Payment, LocalDateTime> dateCol;
    @FXML
    private TableView<Payment> paymentTable;
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
    ObservableList<Payment> info = FXCollections.observableArrayList();

    public void initialize() {
        paymentMethodGroup = new ToggleGroup();
        cashPayment.setToggleGroup(paymentMethodGroup);
        onlinePayment.setToggleGroup(paymentMethodGroup);

        studenNameCol.setCellValueFactory(cell -> cell.getValue().studentNameProperty());
        courseNameCol.setCellValueFactory(cell -> cell.getValue().courseNameProperty());
        dateCol.setCellValueFactory(cell -> cell.getValue().paymentDateProperty());


    }

    public void setStudentAndCourse(int studentId, int courseId) {
        this.studentId = studentId;
        this.courseId = courseId;

        getCourseInfo();
        getStudentInfo();
    }

    public void getCourseInfo() {
        String sql = "SELECT course_name, price, description, image FROM courses WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                courseName.setText(rs.getString("course_name"));
                double price = rs.getDouble("price");
                priceCourse.setText(String.valueOf(price));
                total.setText(String.valueOf(price)); // hiển thị tổng luôn ở đây
                descripCousre.setText(rs.getString("description"));
                String imagePath = rs.getString("image");
                if (imagePath != null && !imagePath.isEmpty()) {
                    imageCourse.setImage(new Image(imagePath));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getStudentInfo() {
        info.clear();
        String sql = "SELECT u.fullname AS student_name, c.course_name, p.payment_date " +
                "FROM payments p " +
                "JOIN students s ON p.student_id = s.id " +
                "JOIN users u ON s.user_id = u.id " +
                "JOIN courses c ON p.course_id = c.id " +
                "WHERE s.id = ? AND c.id = ?";

        try (Connection conn = DBConnection.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, courseId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String studentName = rs.getString("student_name");
                String courseName = rs.getString("course_name");
                LocalDateTime date = rs.getTimestamp("payment_date").toLocalDateTime();

                info.add(new Payment(studentName, courseName, date));

            }
            paymentTable.setItems(info);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

