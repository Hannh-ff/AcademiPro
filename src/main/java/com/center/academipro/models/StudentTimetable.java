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

    public StudentTimetable(int id, String className, LocalDate date,
                            LocalTime startTime, LocalTime endTime, String status) {
        this.id = new SimpleIntegerProperty(id);
        this.className = new SimpleStringProperty(className);
        this.date = new SimpleObjectProperty<>(date);
        this.startTime = new SimpleObjectProperty<>(startTime);
        this.endTime = new SimpleObjectProperty<>(endTime);
        this.status = new SimpleStringProperty(status);
    }

    // Các getter phải đúng định dạng get+<TênProperty> hoặc <tênProperty>Property()
    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }

    public String getClassName() { return className.get(); }
    public StringProperty classNameProperty() { return className; }

    public LocalDate getDate() { return date.get(); }
    public ObjectProperty<LocalDate> dateProperty() { return date; }

    public LocalTime getStartTime() { return startTime.get(); }
    public ObjectProperty<LocalTime> startTimeProperty() { return startTime; }

    public LocalTime getEndTime() { return endTime.get(); }
    public ObjectProperty<LocalTime> endTimeProperty() { return endTime; }

    public String getStatus() { return status.get(); }
    public StringProperty statusProperty() { return status; }
}