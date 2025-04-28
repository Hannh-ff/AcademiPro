package com.center.academipro.models;


import java.time.LocalDate;

public class Payment {
   private int id;
   private String studentName;
   private String courseName;
   private String paymentMethod;
   private String paymentStatus;
   private double price;
   private LocalDate paymentDate;

    public Payment(int id, String studentName, String courseName, String paymentMethod, String paymentStatus, double price, LocalDate paymentDate) {
         this.id = id;
         this.studentName = studentName;
         this.courseName = courseName;
         this.paymentMethod = paymentMethod;
         this.paymentStatus = paymentStatus;
         this.price = price;
         this.paymentDate = paymentDate;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getStudentName() {
        return studentName;
    }
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
    public String getCourseName() {
        return courseName;
    }
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
    public String getPaymentMethod() {
        return paymentMethod;
    }
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    public String getPaymentStatus() {
        return paymentStatus;
    }
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public LocalDate getPaymentDate() {
        return paymentDate;
    }
    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }
}
