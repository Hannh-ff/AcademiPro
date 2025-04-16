package com.center.academipro.models;

public class Teacher {
    private Integer teacherId;
    private String fullName;
    private String user;
    private String email;
    private String birthday;
    private String phone;
    private String course_name;

    public Teacher(Integer teacherId, String fullName, String user, String email, String birthday, String phone, String course_name) {
        this.teacherId = teacherId;
        this.fullName = fullName;
        this.user = user;
        this.email = email;
        this.birthday = birthday;
        this.phone = phone;
        this.course_name = course_name;
    }

    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCourse_name() {
        return course_name;
    }

    public void setCourse_name(String course_name) {
        this.course_name = course_name;
    }
}
