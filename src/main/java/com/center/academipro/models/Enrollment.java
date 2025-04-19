package com.center.academipro.models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Enrollment {
    private SimpleIntegerProperty id;
    private SimpleStringProperty courseName;
    private SimpleStringProperty status;

    public Enrollment(int id, String courseName, String status) {
        this.id = new SimpleIntegerProperty(id);
        this.courseName = new SimpleStringProperty(courseName);
        this.status = new SimpleStringProperty(status);
    }

    public int getId() {
        return id.get();
    }

    public SimpleStringProperty courseNameProperty() {
        return courseName;
    }

    public SimpleStringProperty statusProperty() {
        return status;
    }
}
