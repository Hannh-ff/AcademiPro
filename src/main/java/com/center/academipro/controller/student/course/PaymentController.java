package com.center.academipro.controller.student.course;

import com.center.academipro.models.Payment;
import com.center.academipro.utils.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert.AlertType;

import java.sql.*;
import java.time.LocalDateTime;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class PaymentController {
    // Constants for VNPay configuration
    private static final String VNP_VERSION = "2.1.0";
    private static final String VNP_COMMAND = "pay";
    private static final String VNP_TMN_CODE = "LKR7HBNP";
    private static final String VNP_HASH_SECRET = "UIHVD0YFF3V9FXRNIS6FXJ62VH7UPVD6";
    private static final String VNP_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    private static final String VNP_RETURN_URL = "http://academipro.test/vnpay-callback.php?vnp_ResponseCode=00&vnp_TxnRef=TEST123&custom_student_id=1&custom_course_id=1";
    private static final String VNP_LOCALE = "vn";
    private static final String VNP_CURR_CODE = "VND";


    // FXML components
    @FXML private RadioButton cashPayment, onlinePayment;
    @FXML private Label courseName, priceCourse, amountPayment, total;
    @FXML private ImageView imageCourse;
    @FXML private Text descripCousre;

    private ToggleGroup paymentMethodGroup;
    private int studentId;
    private int courseId;

    // Initialize payment method toggle group
    @FXML
    public void initialize() {
        paymentMethodGroup = new ToggleGroup();
        cashPayment.setToggleGroup(paymentMethodGroup);
        onlinePayment.setToggleGroup(paymentMethodGroup);

        userIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        courseIdCol.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        methodCol.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Định dạng cột amount hiển thị tiền tệ
        amountCol.setCellFactory(column -> new TableCell<Payment, Double>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(String.format("%,.0f VND", amount));
                }
            }
        });
        loadSamplePayments();

    }
    private void loadSamplePayments() {
        ObservableList<Payment> payments = FXCollections.observableArrayList(
                new Payment(1, 1, 1, 2500000, "VNPay", "Completed", "VNPAY123456789", LocalDateTime.now()),
                new Payment(2, 2, 2, 3000000, "Cash", "Completed", "", LocalDateTime.now().minusDays(1)),
                new Payment(3, 1, 3, 3500000, "VNPay", "Pending", "VNPAY987654321", null)
        );
        paymentTable.setItems(payments);
    }

    // Set student and course info
    public void setStudentAndCourse(int studentId, int courseId) {
        this.studentId = studentId;
        this.courseId = courseId;
    }

    // Set course data for display
    public void setCourseData(String name, String description, String imageUrl, double price) {
        courseName.setText(name);
        descripCousre.setText(description);
        priceCourse.setText(String.format("%,.0f VND", price));
        amountPayment.setText("1");
        total.setText(String.format("%,.0f VND", price));
        imageCourse.setImage(new Image(imageUrl));
    }

    // Handle payment button click
    @FXML
    private void handlePayment(ActionEvent event) {
        if (cashPayment.isSelected()) {
            handleCashPayment();
        } else if (onlinePayment.isSelected()) {
            handleOnlinePayment();
        } else {
            showAlert(AlertType.WARNING, "Cảnh báo", "Vui lòng chọn phương thức thanh toán!");
        }
    }

    // Process cash payment
    private void handleCashPayment() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO payments (user_id, course_id, amount, payment_method, status, payment_date) VALUES (?, ?, ?, ?, ?, ?)")) {

            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            pstmt.setDouble(3, parsePrice(total.getText()));
            pstmt.setString(4, "Cash");
            pstmt.setString(5, "Completed");
            pstmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));

            if (pstmt.executeUpdate() > 0) {
                showAlert(AlertType.INFORMATION, "Thành công", "Thanh toán tiền mặt thành công!");
            }
        } catch (Exception e) {
            handleError("Lỗi thanh toán tiền mặt", e);
        }
    }

    // Process online payment
    private void handleOnlinePayment() {
        try {
            double amount = parsePrice(total.getText());
            if (amount < 10000) {
                showAlert(AlertType.ERROR, "Lỗi", "Số tiền tối thiểu là 10,000 VND");
                return;
            }

            String orderId = "ACADEMI" + System.currentTimeMillis();
            savePaymentToDatabase(orderId, amount, "VNPay", "Pending");
            String paymentUrl = generateVNPayUrl(amount, orderId);
            openBrowser(paymentUrl);
        } catch (Exception e) {
            handleError("Lỗi thanh toán VNPay", e);
        }
    }

    // Generate VNPay payment URL
    private String generateVNPayUrl(double amount, String orderId) throws UnsupportedEncodingException {
        Map<String, String> vnpParams = new LinkedHashMap<>();
        vnpParams.put("vnp_Version", VNP_VERSION);
        vnpParams.put("vnp_Command", VNP_COMMAND);
        vnpParams.put("vnp_TmnCode", VNP_TMN_CODE);
        vnpParams.put("vnp_Amount", String.valueOf((int)(amount * 100)));
        vnpParams.put("vnp_CurrCode", VNP_CURR_CODE);
        vnpParams.put("vnp_TxnRef", orderId);
        vnpParams.put("vnp_OrderInfo", "Thanh toán khóa học: " + courseName.getText());
        vnpParams.put("vnp_ReturnUrl", VNP_RETURN_URL);
        vnpParams.put("vnp_IpAddr", getClientIp());
        vnpParams.put("vnp_CreateDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        vnpParams.put("vnp_Locale", VNP_LOCALE);
        vnpParams.put("custom_student_id", String.valueOf(studentId));
        vnpParams.put("custom_course_id", String.valueOf(courseId));

        return buildPaymentUrl(vnpParams);
    }

    // Build complete payment URL with signature
    private String buildPaymentUrl(Map<String, String> params) throws UnsupportedEncodingException {
        // 1. Sắp xếp tham số
        Map<String, String> sortedParams = new TreeMap<>(params);

        // 2. Xây dựng chuỗi hashData (QUAN TRỌNG: KHÔNG encode)
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                // 2.1. Chuỗi để hash (không encode)
                if (hashData.length() > 0) hashData.append('&');
                hashData.append(entry.getKey()).append('=').append(entry.getValue());

                // 2.2. Chuỗi query (có encode)
                if (query.length() > 0) query.append('&');
                query.append(URLEncoder.encode(entry.getKey(), "UTF-8"))
                        .append('=')
                        .append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
        }

        // 3. Tạo chữ ký
        String secureHash = hmacSHA512(VNP_HASH_SECRET, hashData.toString());

        // 4. Thêm chữ ký vào URL
        return VNP_URL + "?" + query.toString() + "&vnp_SecureHash=" + secureHash;
    }    // Save payment to database
    private void savePaymentToDatabase(String orderId, double amount, String method, String status) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO payments (user_id, course_id, amount, payment_method, status, transaction_id, payment_date) VALUES (?, ?, ?, ?, ?, ?, ?)")) {

            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            pstmt.setDouble(3, amount);
            pstmt.setString(4, method);
            pstmt.setString(5, status);
            pstmt.setString(6, orderId);
            pstmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            handleError("Lỗi lưu thanh toán", e);
        }
    }

    // Helper methods
    private double parsePrice(String priceText) {
        return Double.parseDouble(priceText.replaceAll("[^\\d.]", ""));
    }

    private String getClientIp() {
        try {
            return java.net.InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "127.0.0.1";
        }
    }

    private void openBrowser(String url) {
        try {
            java.awt.Desktop.getDesktop().browse(URI.create(url));
        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Lỗi", "Không thể mở trình duyệt: " + e.getMessage());
        }
    }

    private String hmacSHA512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA256");
            hmac512.init(new SecretKeySpec(key.getBytes(), "HmacSHA512"));
            byte[] result = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : result) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Lỗi tạo chữ ký bảo mật", e);
        }
    }

    private void showAlert(AlertType type, String title, String message) {
        new Alert(type, message, ButtonType.OK).showAndWait();
    }

    private void handleError(String title, Exception e) {
        e.printStackTrace();
        showAlert(AlertType.ERROR, title, e.getMessage());
    }
}