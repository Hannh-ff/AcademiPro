package com.center.academipro.models;

import javafx.beans.property.*;

public class Attendance {
    private final IntegerProperty id;
    private final IntegerProperty timetableId;
    private final IntegerProperty studentId;
    private final StringProperty studentName;
    private final StringProperty date;
    private final StringProperty status;
    private final StringProperty notes;
    private final BooleanProperty present;
    private final BooleanProperty absent;
    private final BooleanProperty late;
    private final BooleanProperty excused;

    // Constructor đơn giản hóa
    public Attendance(int studentId, String studentName, int timetableId, String date) {
        this.id = new SimpleIntegerProperty(0);
        this.timetableId = new SimpleIntegerProperty(timetableId);
        this.studentId = new SimpleIntegerProperty(studentId);
        this.studentName = new SimpleStringProperty(studentName);
        this.date = new SimpleStringProperty(date);
        this.status = new SimpleStringProperty("");
        this.notes = new SimpleStringProperty("");

        this.present = new SimpleBooleanProperty(false);
        this.absent = new SimpleBooleanProperty(false);
        this.late = new SimpleBooleanProperty(false);
        this.excused = new SimpleBooleanProperty(false);

        initListeners();
    }

    private void initListeners() {
        present.addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                absent.set(false);
                late.set(false);
                excused.set(false);
                status.set("Present");
            }
        });

        absent.addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                present.set(false);
                late.set(false);
                excused.set(false);
                status.set("Absent");
            }
        });

        late.addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                present.set(false);
                absent.set(false);
                excused.set(false);
                status.set("Late");
            }
        });

        excused.addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                present.set(false);
                absent.set(false);
                late.set(false);
                status.set("Excused");
            }
        });
    }

    // Getter methods
    public int getId() { return id.get(); }
    public int getTimetableId() { return timetableId.get(); }
    public int getStudentId() { return studentId.get(); }
    public String getStudentName() { return studentName.get(); }
    public String getDate() { return date.get(); }
    public String getStatus() { return status.get(); }
    public String getNotes() { return notes.get(); }
    public boolean isPresent() { return present.get(); }
    public boolean isAbsent() { return absent.get(); }
    public boolean isLate() { return late.get(); }
    public boolean isExcused() { return excused.get(); }

    // Property getters
    public IntegerProperty idProperty() { return id; }
    public IntegerProperty timetableIdProperty() { return timetableId; }
    public IntegerProperty studentIdProperty() { return studentId; }
    public StringProperty studentNameProperty() { return studentName; }
    public StringProperty dateProperty() { return date; }
    public StringProperty statusProperty() { return status; }
    public StringProperty notesProperty() { return notes; }
    public BooleanProperty presentProperty() { return present; }
    public BooleanProperty absentProperty() { return absent; }
    public BooleanProperty lateProperty() { return late; }
    public BooleanProperty excusedProperty() { return excused; }


    // Setter methods
    public void setNotes(String notes) { this.notes.set(notes); }
    public void setStatus(String status) { this.status.set(status); }
}