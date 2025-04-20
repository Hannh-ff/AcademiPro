package com.center.academipro.models;

import javafx.beans.property.*;

public class Class {

    private final IntegerProperty id;
    private final StringProperty className;
    private final StringProperty description;
    private final StringProperty image;
    private final DoubleProperty price;

    public Class(int id, String className, String description, String image, double price) {
        this.id = new SimpleIntegerProperty(id);
        this.className = new SimpleStringProperty(className);
        this.description = new SimpleStringProperty(description);
        this.image = new SimpleStringProperty(image);
        this.price = new SimpleDoubleProperty(price);
    }

    // Getter and Setter Methods for the fields

    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty classNameProperty() {
        return className;
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

    public void setId(int id) {
        this.id.set(id);
    }

    public String getClassName() {
        return className.get();
    }

    public void setClassName(String className) {
        this.className.set(className);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public String getImage() {
        return image.get();
    }

    public void setImage(String image) {
        this.image.set(image);
    }

    public double getPrice() {
        return price.get();
    }

    public void setPrice(double price) {
        this.price.set(price);
    }
}
