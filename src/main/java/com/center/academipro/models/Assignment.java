package com.center.academipro.models;

import java.time.LocalDateTime;

public class Assignment {
    private int id;
    private int classId;
    private int teacherId;
    private String title;
    private String description;
    private LocalDateTime deadline;
    private LocalDateTime createdAt;

    // Constructors
    public Assignment() {}

    public Assignment(int classId, int teacherId, String title, String description, LocalDateTime deadline) {
        this.classId = classId;
        this.teacherId = teacherId;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getClassId() { return classId; }
    public void setClassId(int classId) { this.classId = classId; }

    public int getTeacherId() { return teacherId; }
    public void setTeacherId(int teacherId) { this.teacherId = teacherId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}