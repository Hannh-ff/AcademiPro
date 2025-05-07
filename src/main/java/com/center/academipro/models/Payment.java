package com.center.academipro.models;


import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Payment {
    private SimpleIntegerProperty id;
    private SimpleStringProperty studentName;
    private SimpleStringProperty courseName;
    private SimpleStringProperty paymentMethod;
    private SimpleStringProperty paymentStatus;
    private SimpleDoubleProperty price;
    private SimpleObjectProperty<LocalDateTime> paymentDate;

    public Payment() {
        this.id = new SimpleIntegerProperty();
        this.studentName = new SimpleStringProperty();
        this.courseName = new SimpleStringProperty();
        this.paymentMethod = new SimpleStringProperty();
        this.paymentStatus = new SimpleStringProperty();
        this.price = new SimpleDoubleProperty();
        this.paymentDate = new SimpleObjectProperty<>();
    }

    public Payment(SimpleIntegerProperty id, SimpleStringProperty studentName, SimpleStringProperty courseName, SimpleStringProperty paymentMethod, SimpleStringProperty paymentStatus, SimpleDoubleProperty price, SimpleObjectProperty<LocalDateTime> paymentDate) {
        this.id = id;
        this.studentName = studentName;
        this.courseName = courseName;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.price = price;
        this.paymentDate = paymentDate;
    }

    public Payment(String studentName, String courseName, LocalDateTime date) {
        this.id = new SimpleIntegerProperty();
        this.studentName = new SimpleStringProperty(studentName);
        this.courseName = new SimpleStringProperty(courseName);
        this.paymentMethod = new SimpleStringProperty();
        this.paymentStatus = new SimpleStringProperty();
        this.price = new SimpleDoubleProperty();
        this.paymentDate = new SimpleObjectProperty<>(date);
    }

    public int getId() {
        return id.get();
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getStudentName() {
        return studentName.get();
    }

    public SimpleStringProperty studentNameProperty() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName.set(studentName);
    }

    public String getCourseName() {
        return courseName.get();
    }

    public SimpleStringProperty courseNameProperty() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName.set(courseName);
    }

    public String getPaymentMethod() {
        return paymentMethod.get();
    }

    public SimpleStringProperty paymentMethodProperty() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod.set(paymentMethod);
    }

    public String getPaymentStatus() {
        return paymentStatus.get();
    }

    public SimpleStringProperty paymentStatusProperty() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus.set(paymentStatus);
    }

    public double getPrice() {
        return price.get();
    }

    public SimpleDoubleProperty priceProperty() {
        return price;
    }

    public void setPrice(double price) {
        this.price.set(price);
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate.get();
    }

    public SimpleObjectProperty<LocalDateTime> paymentDateProperty() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate.set(paymentDate);
    }
}
