package com.center.academipro.models;


import javafx.beans.property.*;

import java.time.LocalDate;
import java.time.LocalTime;
public class StudentTimetable {
    private final IntegerProperty id;
    private final StringProperty className;
    private final ObjectProperty<LocalDate> date;
    private final ObjectProperty<LocalTime> startTime;
    private final ObjectProperty<LocalTime> endTime;
    private final StringProperty status;

    public StudentTimetable(int id, String className, LocalDate date, LocalTime startTime, LocalTime endTime, String status) {
        this.id = new SimpleIntegerProperty(id);
        this.className = new SimpleStringProperty(className);
        this.date = new SimpleObjectProperty<>(date);
        this.startTime = new SimpleObjectProperty<>(startTime);
        this.endTime = new SimpleObjectProperty<>(endTime);
        this.status = new SimpleStringProperty(status); // Chỉ Student mới có
    }

    public String getStatus() { return status.get(); }
    public StringProperty statusProperty() { return status; }
    public void setStatus(String status) { this.status.set(status); }
}