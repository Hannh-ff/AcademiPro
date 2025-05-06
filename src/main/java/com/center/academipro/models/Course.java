package com.center.academipro.models;

import javafx.beans.property.*;

public class Course {
    private final IntegerProperty id;
    private final StringProperty courseName;
    private final StringProperty description;
    private final StringProperty image;
    private final DoubleProperty price;

    // Constructors
    public Course() {
        this(0, "", "", "", 0.0);
    }

    public Course(int id, String courseName) {
        this(id, courseName, "", "", 0.0);
    }

    public Course(int id, String courseName, String description, String image, double price) {
        this.id = new SimpleIntegerProperty(id);
        this.courseName = new SimpleStringProperty(courseName);
        this.description = new SimpleStringProperty(description);
        this.image = new SimpleStringProperty(image);
        this.price = new SimpleDoubleProperty(price);
    }

    // Property getters
    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty courseNameProperty() {
        return courseName;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public StringProperty imageProperty() {
        return image;
    }

    public DoubleProperty priceProperty() {
        return price;
    }

    // Regular getters
    public int getId() {
        return id.get();
    }

    public String getCourseName() {
        return courseName.get();
    }

    public String getDescription() {
        return description.get();
    }

    public String getImage() {
        return image.get();
    }

    public double getPrice() {
        return price.get();
    }

    // Regular setters with null checks
    public void setId(int id) {
        this.id.set(id);
    }

    public void setCourseName(String courseName) {
        this.courseName.set(courseName != null ? courseName : "");
    }

    public void setDescription(String description) {
        this.description.set(description != null ? description : "");
    }

    public void setImage(String image) {
        this.image.set(image != null ? image : "");
    }

    public void setPrice(double price) {
        this.price.set(price);
    }

    // Utility methods
    public boolean hasImage() {
        return getImage() != null && !getImage().isEmpty();
    }

    public String getFormattedPrice() {
        return String.format("%,.0f VND", getPrice());
    }

    @Override
    public String toString() {
        return getCourseName() + " (" + getFormattedPrice() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return getId() == course.getId();
    }

    @Override
    public int hashCode() {
        return getId();
    }
}