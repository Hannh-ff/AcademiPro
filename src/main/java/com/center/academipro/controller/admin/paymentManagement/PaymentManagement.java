package com.center.academipro.controller.admin.paymentManagement;

import com.center.academipro.models.Payment;
import com.center.academipro.utils.DBConnection;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;

public class PaymentManagement {

    @FXML
    private TableView<Payment> tableViewPayment;

    @FXML
    private TableColumn<Payment, Integer> IdCol;
    @FXML
    private TableColumn<Payment, String> studentNameCol;
    @FXML
    private TableColumn<Payment, String> CourseCol;
    @FXML
    private TableColumn<Payment, Double> priceCol;
    @FXML
    private TableColumn<Payment, String> paymethodCol;
    @FXML
    private TableColumn<Payment, String> paystatusCol;
    @FXML
    private TableColumn<Payment, String> payDateCol;

    private ObservableList<Payment> paymentList = FXCollections.observableArrayList();

    public void initialize() {
        IdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        studentNameCol.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        CourseCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        paymethodCol.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        paystatusCol.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));
        payDateCol.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));

        loadPaymentData();
    }

    private void loadPaymentData() {
        String query = "SELECT p.id, u.fullname, c.course_name, c.price, p.payment_method, p.payment_status, p.payment_date " +
                "FROM payments p " +
                "JOIN users u ON p.student_id = u.id " +
                "JOIN courses c ON p.course_id = c.id";

        try (Connection conn = DBConnection.getConn();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            paymentList.clear();
            while (rs.next()) {
                Payment payment = new Payment(
                        new SimpleIntegerProperty(rs.getInt("id")),
                        new SimpleStringProperty(rs.getString("fullname")),
                        new SimpleStringProperty(rs.getString("course_name")),
                        new SimpleStringProperty(rs.getString("payment_method")),
                        new SimpleStringProperty(rs.getString("payment_status")),
                        new SimpleDoubleProperty(rs.getDouble("price")),
                        new SimpleObjectProperty<>(rs.getTimestamp("payment_date").toLocalDateTime())
                );

                paymentList.add(payment);
            }

            tableViewPayment.setItems(paymentList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
