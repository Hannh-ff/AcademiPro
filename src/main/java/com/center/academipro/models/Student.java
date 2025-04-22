package com.center.academipro.models;

import java.time.LocalDate;

public class Student {
    private int id;
    private String fullName;
    private String username;
    private String email;
    private LocalDate birthday;
    private String phone;
    private String course;
    private String className;

    public Student(int id, String fullName, String username, String email, LocalDate birthday,
                   String phone, String course, String className) {
        this.id = id;
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.birthday = birthday;
        this.phone = phone;
        this.course = course;
        this.className = className;
    }

    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public LocalDate getBirthday() { return birthday; }
    public String getPhone() { return phone; }
    public String getCourse() { return course; }
    public String getClassName() { return className; }

    public void setId(int id) {
        this.id = id;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
