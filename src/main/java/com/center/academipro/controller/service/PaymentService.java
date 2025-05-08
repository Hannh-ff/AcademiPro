package com.center.academipro.controller.service;

import com.center.academipro.models.Payment;
import com.center.academipro.utils.DBConnection;
import com.center.academipro.utils.VnpayUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class PaymentService {

    public static String createVnpayPayment(double amount, String orderInfo, String ipAddress) {
        try {
            return VnpayUtil.createPaymentUrl(amount, orderInfo, ipAddress);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

//    public static boolean savePayment(int studentId, int courseId, double amount,
//                                      String paymentMethod, String paymentStatus,
//                                      String transactionId) {
//        String sql = "INSERT INTO payments (user_id, course_id, amount, payment_method, payment_status, payment_date) " +
//                "VALUES (?, ?, ?, ?, ?, ?)";
//
//        try (Connection conn = DBConnection.getConn();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//
//            stmt.setInt(1, studentId);
//            stmt.setInt(2, courseId);
//            stmt.setDouble(3, amount);
//            stmt.setString(4, paymentMethod);
//            stmt.setString(5, paymentStatus);
//            stmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
//
//            return stmt.executeUpdate() > 0;
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }

    public static boolean updateVnpayPayment(int paymentId, Map<String, String> vnpParams) {
        String sql = "UPDATE payments SET " +
                "transaction_id = ?, " +
                "payment_status = ? " +
                "WHERE id = ?";

        try (Connection conn = DBConnection.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, vnpParams.get("vnp_TransactionNo"));
            stmt.setString(2, "00".equals(vnpParams.get("vnp_ResponseCode")) ? "Completed" : "Failed");
            stmt.setInt(3, paymentId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ObservableList<Payment> getPaymentsByStudent(int studentId) {
        ObservableList<Payment> payments = FXCollections.observableArrayList();
        String sql = "SELECT p.*, u.fullname AS student_name, c.course_name " +
                "FROM payments p " +
                "JOIN students s ON p.student_id = s.id " +
                "JOIN users u ON s.user_id = u.id " +
                "JOIN courses c ON p.course_id = c.id " +
                "WHERE p.student_id = ? " +
                "ORDER BY p.payment_date DESC";

        try (Connection conn = DBConnection.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Payment payment = new Payment(
                        rs.getString("student_name"),
                        rs.getString("course_name"),
                        rs.getTimestamp("payment_date").toLocalDateTime()
                );
                payment.setId(rs.getInt("id"));
                payment.setAmount(rs.getDouble("amount"));
                payment.setPaymentMethod(rs.getString("payment_method"));
                payment.setPaymentStatus(rs.getString("payment_status"));
                payment.setTransactionId(rs.getString("transaction_id"));

                payments.add(payment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return payments;
    }

    public static int getLastInsertedPaymentId() {
        String sql = "SELECT LAST_INSERT_ID() as last_id";

        try (Connection conn = DBConnection.getConn();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("last_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }
}