package com.center.academipro.models;

import java.time.LocalDateTime;

public class PurchaseHistory {
    private int id;
    private int userId;
    private int courseId;
    private LocalDateTime purchaseDate;

    public PurchaseHistory(){}

    public PurchaseHistory(int id, int userId, int courseId, LocalDateTime purchaseDate) {
        this.id = id;
        this.userId = userId;
        this.courseId = courseId;
        this.purchaseDate = purchaseDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
}
