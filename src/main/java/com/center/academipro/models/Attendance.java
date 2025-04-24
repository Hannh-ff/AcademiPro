package com.center.academipro.models;

import javafx.beans.property.*;

public class Attendance {
    private final IntegerProperty id;
    private final IntegerProperty classId;
    private final IntegerProperty studentId;
    private final StringProperty studentName;
    private final StringProperty date;
    private final StringProperty status;

    private final BooleanProperty present;
    private final BooleanProperty absent;

    public Attendance(int id, int classId, int studentId, String studentName, String date, String status) {
        this.id = new SimpleIntegerProperty(id);
        this.classId = new SimpleIntegerProperty(classId);
        this.studentId = new SimpleIntegerProperty(studentId);
        this.studentName = new SimpleStringProperty(studentName);
        this.date = new SimpleStringProperty(date);
        this.status = new SimpleStringProperty(status);

        this.present = new SimpleBooleanProperty("present".equalsIgnoreCase(status));
        this.absent = new SimpleBooleanProperty("absent".equalsIgnoreCase(status));

        // Đồng bộ trạng thái checkbox
        initListeners();
    }

    // Constructor khi chỉ load tên và ID
    public Attendance(int studentId, String studentName) {
        this.id = new SimpleIntegerProperty(0);
        this.classId = new SimpleIntegerProperty(0);
        this.studentId = new SimpleIntegerProperty(studentId);
        this.studentName = new SimpleStringProperty(studentName);
        this.date = new SimpleStringProperty("");
        this.status = new SimpleStringProperty("");

        this.present = new SimpleBooleanProperty(false);
        this.absent = new SimpleBooleanProperty(false);

        initListeners();
    }

    private void initListeners() {
        present.addListener((obs, oldVal, newVal) -> {
            if (newVal) absent.set(false);
        });

        absent.addListener((obs, oldVal, newVal) -> {
            if (newVal) present.set(false);
        });
    }

    // Getter & Setter dạng property
    public int getId() { return id.get(); }
    public int getClassId() { return classId.get(); }
    public int getStudentId() { return studentId.get(); }
    public String getStudentName() { return studentName.get(); }
    public String getDate() { return date.get(); }
    public String getStatus() {
        if (present.get()) return "present";
        if (absent.get()) return "absent";
        return "unknown";
    }

    public void setClassId(int classId) { this.classId.set(classId); }
    public void setDate(String date) { this.date.set(date); }
    public void setStatus(String status) { this.status.set(status); }

    public BooleanProperty presentProperty() { return present; }
    public BooleanProperty absentProperty() { return absent; }
    public StringProperty studentNameProperty() { return studentName; }

    // Có thể dùng thêm nếu cần hiển thị chi tiết:
    public IntegerProperty idProperty() { return id; }
    public IntegerProperty studentIdProperty() { return studentId; }
    public StringProperty dateProperty() { return date; }
    public StringProperty statusProperty() { return status; }
}
