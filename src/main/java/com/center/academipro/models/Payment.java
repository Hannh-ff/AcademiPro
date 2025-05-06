package com.center.academipro.models;

import java.time.LocalDateTime;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;


public class Payment {
    private final SimpleIntegerProperty id = new SimpleIntegerProperty();
    private final SimpleIntegerProperty userId = new SimpleIntegerProperty();
    private final SimpleIntegerProperty courseId = new SimpleIntegerProperty();
    private final SimpleDoubleProperty amount = new SimpleDoubleProperty();
    private final SimpleStringProperty paymentMethod = new SimpleStringProperty();
    private final SimpleStringProperty status = new SimpleStringProperty();
    private final SimpleStringProperty transactionId = new SimpleStringProperty();
    private final SimpleObjectProperty<LocalDateTime> paymentDate = new SimpleObjectProperty<>();


    // Constructors
    public Payment() {
        // Default constructor
    }

    // Constructor for creating new payments (without ID)
    public Payment(int userId, int courseId, double amount, String paymentMethod,
                   String status, String transactionId, LocalDateTime paymentDate) {
        this.userId.set(userId);
        this.courseId.set(courseId);
        this.amount.set(amount);
        this.paymentMethod.set(paymentMethod);
        this.status.set(status);
        this.transactionId.set(transactionId);
        this.paymentDate.set(paymentDate);
    }

    // Constructor for existing payments (with ID)
    // Constructor cho thông tin thanh toán cơ bản (không có ID, transactionId và paymentDate)
    public Payment(int userId, int courseId, double amount, String paymentMethod, String status) {
        this.userId.set(userId);
        this.courseId.set(courseId);
        this.amount.set(amount);
        this.paymentMethod.set(paymentMethod);
        this.status.set(status);
        this.transactionId.set(""); // Mã giao dịch mặc định là rỗng
        this.paymentDate.set(LocalDateTime.now()); // Mặc định là thời gian hiện tại
    }

    // Property getters
    public SimpleIntegerProperty idProperty() { return id; }
    public SimpleIntegerProperty userIdProperty() { return userId; }
    public SimpleIntegerProperty courseIdProperty() { return courseId; }
    public SimpleDoubleProperty amountProperty() { return amount; }
    public SimpleStringProperty paymentMethodProperty() { return paymentMethod; }
    public SimpleStringProperty statusProperty() { return status; }
    public SimpleStringProperty transactionIdProperty() { return transactionId; }
    public SimpleObjectProperty<LocalDateTime> paymentDateProperty() { return paymentDate; }

    // Regular getters and setters
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }

    public int getUserId() { return userId.get(); }
    public void setUserId(int userId) { this.userId.set(userId); }

    public int getCourseId() { return courseId.get(); }
    public void setCourseId(int courseId) { this.courseId.set(courseId); }

    public double getAmount() { return amount.get(); }
    public void setAmount(double amount) { this.amount.set(amount); }

    public String getPaymentMethod() { return paymentMethod.get(); }
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod.set(paymentMethod != null ? paymentMethod : "");
    }

    public String getStatus() { return status.get(); }
    public void setStatus(String status) {
        this.status.set(status != null ? status : "Pending");
    }

    public String getTransactionId() { return transactionId.get(); }
    public void setTransactionId(String transactionId) {
        this.transactionId.set(transactionId != null ? transactionId : "");
    }

    public LocalDateTime getPaymentDate() { return paymentDate.get(); }
    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate.set(paymentDate != null ? paymentDate : LocalDateTime.now());
    }

    // Utility methods
    public boolean isCompleted() {
        return "Completed".equalsIgnoreCase(getStatus());
    }

    public boolean isPending() {
        return "Pending".equalsIgnoreCase(getStatus());
    }

    public boolean isFailed() {
        return "Failed".equalsIgnoreCase(getStatus());
    }

    @Override
    public String toString() {
        return String.format(
                "Payment{id=%d, userId=%d, courseId=%d, amount=%.2f, method='%s', status='%s', transactionId='%s', date=%s}",
                getId(), getUserId(), getCourseId(), getAmount(),
                getPaymentMethod(), getStatus(), getTransactionId(), getPaymentDate()
        );
    }
}