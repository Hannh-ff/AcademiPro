package com.center.academipro.models;


import java.time.LocalDate;

public class Payment {
    private int id;
    private int enrollmentId;
    private double amount;
    private LocalDate paymentDate;
    private String method;

    public Payment(int id, int enrollmentId, double amount, LocalDate paymentDate, String method) {
        this.id = id;
        this.enrollmentId = enrollmentId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.method = method;
    }

    public Payment(int enrollmentId, double amount, LocalDate paymentDate, String method) {
        this(-1, enrollmentId, amount, paymentDate, method);
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }



}
