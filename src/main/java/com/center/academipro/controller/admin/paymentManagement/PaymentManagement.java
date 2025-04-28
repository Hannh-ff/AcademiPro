package com.center.academipro.controller.admin.paymentManagement;

import com.center.academipro.models.Payment;
import com.center.academipro.models.Student;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.util.Date;

public class PaymentManagement {
    @FXML
    private TableColumn<Payment, String> CourseCol;

    @FXML
    private TableColumn<Payment, Integer> IdCol;

    @FXML
    private TableColumn<Payment, Void> actionCol;

    @FXML
    private TableColumn<Payment, Date> payDateCol;

    @FXML
    private TableColumn<Payment, String> paymethodCol;

    @FXML
    private TableColumn<Payment, String> paystatusCol;

    @FXML
    private TableColumn<Payment, Double> priceCol;

    @FXML
    private TableColumn<Payment, String> studentNameCol;

    @FXML
    private TableView<Payment> tableViewPayment;
    @FXML
    public void initialize() {
        // Initialization logic here
    }
    private void loadPayment() {
        // Load payment data from the database and populate the table
    }

    private void setUpActionColumn() {
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button updateBtn = new Button("Update");
            private final Button deleteBtn = new Button("Delete");
            private final HBox btnBox = new HBox(10, updateBtn, deleteBtn);

//            {
//                updateBtn.setOnAction(event -> {
//                    Student student = getTableView().getItems().get(getIndex());
//                    handleUpdate();
//                });
//
//                deleteBtn.setOnAction(event -> {
//                    Student student = getTableView().getItems().get(getIndex());
//                    handleDelete(student);
//                });

//                updateBtn.setStyle("-fx-background-color: linear-gradient(to bottom right, #a18cd1, #fbc2eb); -fx-text-fill: white;");
//                deleteBtn.setStyle("-fx-background-color: linear-gradient(to bottom right, #ff1e56, #ff4b2b); -fx-text-fill: white;");
//            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnBox);
                }
            }
        });
    }
    private void handleUpdate(Payment payment) {
        // Handle update action
    }
    private void handleDelete(Payment payment) {
        // Handle delete action
    }
    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(type.name());
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



}
