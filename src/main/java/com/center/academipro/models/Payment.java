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

    public Payment() {
    }

    public Payment(int id, int userId, int courseId, double amount, String paymentMethod,
                   String status, String transactionId, LocalDateTime paymentDate) {
        this.id.set(id);
        this.userId.set(userId);
        this.courseId.set(courseId);
        this.amount.set(amount);
        this.paymentMethod.set(paymentMethod);
        this.status.set(status);
        this.transactionId.set(transactionId);
        this.paymentDate.set(paymentDate);
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
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod.set(paymentMethod); }

    public String getStatus() { return status.get(); }
    public void setStatus(String status) { this.status.set(status); }

    public String getTransactionId() { return transactionId.get(); }
    public void setTransactionId(String transactionId) { this.transactionId.set(transactionId); }

    public LocalDateTime getPaymentDate() { return paymentDate.get(); }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate.set(paymentDate); }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id.get() +
                ", userId=" + userId.get() +
                ", courseId=" + courseId.get() +
                ", amount=" + amount.get() +
                ", paymentMethod='" + paymentMethod.get() + '\'' +
                ", status='" + status.get() + '\'' +
                ", transactionId='" + transactionId.get() + '\'' +
                ", paymentDate=" + paymentDate.get() +
                '}';
    }
}