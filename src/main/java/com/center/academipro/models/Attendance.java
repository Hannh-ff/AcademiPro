package com.center.academipro.models;

public class Attendance {
    private int id;
    private int classId;



    private int studentId;
    private String studentName;
    private String date;
    private String status;

    public Attendance(int id, int classId, int studentId, String studentName, String date, String status) {
        this.id = id;
        this.classId = classId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.date = date;
        this.status = status;
    }

    public Attendance(int studentId, String studentName) {
        this.studentId = studentId;
        this.studentName = studentName;
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

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
