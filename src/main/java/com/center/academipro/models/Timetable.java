package com.center.academipro.models;

import javafx.beans.property.*;

import java.time.LocalDate;
import java.time.LocalTime;

public class Timetable {
    private final IntegerProperty id;
    private final StringProperty className;
    private final ObjectProperty<LocalDate> date;
    private final ObjectProperty<LocalTime> startTime;
    private final ObjectProperty<LocalTime> endTime;

    public Timetable(int id, String className, LocalDate date, LocalTime startTime, LocalTime endTime) {
        this.id = new SimpleIntegerProperty(id);
        this.className = new SimpleStringProperty(className);
        this.date = new SimpleObjectProperty<>(date);
        this.startTime = new SimpleObjectProperty<>(startTime);
        this.endTime = new SimpleObjectProperty<>(endTime);
    }

    // Getters
    public int getId() {
        return id.get();
    }

    public String getClassName() {
        return className.get();
    }

    public LocalDate getDate() {
        return date.get();
    }

    public LocalTime getStartTime() {
        return startTime.get();
    }

    public LocalTime getEndTime() {
        return endTime.get();
    }

    // Property getters (for TableView binding)
    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty classNameProperty() {
        return className;
    }

    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }

    public ObjectProperty<LocalTime> startTimeProperty() {
        return startTime;
    }

    public ObjectProperty<LocalTime> endTimeProperty() {
        return endTime;
    }

    // Setters
    public void setId(int id) {
        this.id.set(id);
    }

    public void setClassName(String className) {
        this.className.set(className);
    }

    public void setDate(LocalDate date) {
        this.date.set(date);
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime.set(startTime);
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime.set(endTime);
    }
}