package com.center.academipro.models;


import javafx.beans.property.*;

import java.time.LocalDateTime;

public class Payment {
    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty studentName = new SimpleStringProperty();
    private StringProperty courseName = new SimpleStringProperty();
    private StringProperty paymentMethod = new SimpleStringProperty();
    private StringProperty paymentStatus = new SimpleStringProperty();
    private final DoubleProperty amount = new SimpleDoubleProperty();
    private DoubleProperty price = new SimpleDoubleProperty();
    private ObjectProperty<LocalDateTime> paymentDate = new SimpleObjectProperty<>();
    private final StringProperty transactionId = new SimpleStringProperty();

    public Payment() {}
    public Payment(SimpleIntegerProperty id, SimpleStringProperty studentName, SimpleStringProperty courseName, SimpleStringProperty paymentMethod, SimpleStringProperty paymentStatus, SimpleDoubleProperty price, SimpleObjectProperty<LocalDateTime> paymentDate) {
        this.id = id;
        this.studentName = studentName;
        this.courseName = courseName;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.price = price;
        this.paymentDate = paymentDate;
    }


    public Payment(String studentName, String courseName, LocalDateTime paymentDate) {
        setStudentName(studentName);
        setCourseName(courseName);
        setPaymentDate(paymentDate);
    }

    // Getter và Setter cho id
    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }

    // Getter và Setter cho studentName
    public String getStudentName() { return studentName.get(); }
    public void setStudentName(String value) { studentName.set(value); }
    public StringProperty studentNameProperty() { return studentName; }

    // Getter và Setter cho courseName
    public String getCourseName() { return courseName.get(); }
    public void setCourseName(String value) { courseName.set(value); }
    public StringProperty courseNameProperty() { return courseName; }

    // Getter và Setter cho paymentMethod
    public String getPaymentMethod() { return paymentMethod.get(); }
    public void setPaymentMethod(String value) { paymentMethod.set(value); }
    public StringProperty paymentMethodProperty() { return paymentMethod; }

    // Getter và Setter cho paymentStatus
    public String getPaymentStatus() { return paymentStatus.get(); }
    public void setPaymentStatus(String value) { paymentStatus.set(value); }
    public StringProperty paymentStatusProperty() { return paymentStatus; }

    // Getter và Setter cho amount
    public double getAmount() { return amount.get(); }
    public void setAmount(double value) { amount.set(value); }
    public DoubleProperty amountProperty() { return amount; }

    // Getter và Setter cho paymentDate
    public LocalDateTime getPaymentDate() { return paymentDate.get(); }
    public void setPaymentDate(LocalDateTime value) { paymentDate.set(value); }
    public ObjectProperty<LocalDateTime> paymentDateProperty() { return paymentDate; }

    // Getter và Setter cho transactionId
    public String getTransactionId() { return transactionId.get(); }
    public void setTransactionId(String value) { transactionId.set(value); }
    public StringProperty transactionIdProperty() { return transactionId; }
}


