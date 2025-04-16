package com.center.academipro.models;

import javafx.beans.property.*;

public class Course {

    private final IntegerProperty id;
    private final StringProperty courseName;
    private final StringProperty description;
    private final StringProperty image;
    private final DoubleProperty price;

    public Course() {
        this.id = new SimpleIntegerProperty();
        this.courseName = new SimpleStringProperty();
        this.description = new SimpleStringProperty();
        this.image = new SimpleStringProperty();
        this.price = new SimpleDoubleProperty();
    }

    public Course(int id, String courseName, String description, String image, double price) {
        this.id = new SimpleIntegerProperty(id);
        this.courseName = new SimpleStringProperty(courseName);
        this.description = new SimpleStringProperty(description);
        this.image = new SimpleStringProperty(image);
        this.price = new SimpleDoubleProperty(price);
    }

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

    public void setId(int id) {
        this.id.set(id);
    }

    public void setCourseName(String courseName) {
        this.courseName.set(courseName);
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public void setImage(String image) {
        this.image.set(image);
    }

    public void setPrice(double price) {
        this.price.set(price);
    }

    @Override
    public String toString() {
        return this.getCourseName();
    }
}
