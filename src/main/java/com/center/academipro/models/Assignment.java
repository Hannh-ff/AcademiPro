package com.center.academipro.models;

import java.util.Date;

public class Assignment {
    private int id;
    private int classId;
    private int teacherId;
    private String title;
    private String description;
    private Date deadline;
    private Date createdAt;

    public Assignment() {
    }

    public Assignment(int id, int classId, int teacherId, String title, String description, Date deadline, Date createdAt) {
        this.id = id;
        this.classId = classId;
        this.teacherId = teacherId;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
