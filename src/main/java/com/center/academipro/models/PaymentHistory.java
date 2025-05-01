package com.center.academipro.models;


import javafx.beans.property.SimpleStringProperty;

public class PaymentHistory {
    private final SimpleStringProperty studentName;
    private final SimpleStringProperty courseName;
    private final SimpleStringProperty date;
    private final SimpleStringProperty amount;

    public PaymentHistory(String studentName, String courseName, String date, String amount) {
        this.studentName = new SimpleStringProperty(studentName);
        this.courseName = new SimpleStringProperty(courseName);
        this.date = new SimpleStringProperty(date);
        this.amount = new SimpleStringProperty(amount);
    }

    // Getter methods
    public String getStudentName() { return studentName.get(); }
    public String getCourseName() { return courseName.get(); }
    public String getDate() { return date.get(); }
    public String getAmount() { return amount.get(); }

    // Property getters
    public SimpleStringProperty studentNameProperty() { return studentName; }
    public SimpleStringProperty courseNameProperty() { return courseName; }
    public SimpleStringProperty dateProperty() { return date; }
    public SimpleStringProperty amountProperty() { return amount; }
}