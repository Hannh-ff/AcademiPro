package com.center.academipro.models;


import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


import java.time.LocalDate;

public class Teacher {
    private IntegerProperty id;
    private IntegerProperty userId;
    private StringProperty fullname;
    private StringProperty username;
    private StringProperty email;
    private ObjectProperty<LocalDate> birthday;
    private StringProperty phone;
    private StringProperty courses;
    private ObservableList<Course> courseList = FXCollections.observableArrayList();


    public Teacher(){
        this.id = new SimpleIntegerProperty();
        this.userId = new SimpleIntegerProperty();
        this.fullname = new SimpleStringProperty();
        this.username = new SimpleStringProperty();
        this.email = new SimpleStringProperty();
        this.birthday = new SimpleObjectProperty<>();
        this.phone = new SimpleStringProperty();
        this.courses = new SimpleStringProperty();
    }

    public Teacher(int id, int userId, String fullname, String username, String email, LocalDate birthday, String phone, String courses) {
        this.id = new SimpleIntegerProperty(id);
        this.userId = new SimpleIntegerProperty(userId);
        this.fullname = new SimpleStringProperty(fullname);
        this.username = new SimpleStringProperty(username);
        this.email = new SimpleStringProperty(email);
        this.birthday = new SimpleObjectProperty<>(birthday);
        this.phone = new SimpleStringProperty(phone);
        this.courses = new SimpleStringProperty(courses);
    }

    public Teacher(int id, String name) {
        this.id = new SimpleIntegerProperty(id);
        this.fullname = new SimpleStringProperty(name);
    }

    public int getUserId() {
        return userId.get();
    }

    public IntegerProperty userIdProperty() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId.set(userId);
    }

    public IntegerProperty idProperty() { return id; }
    public StringProperty fullnameProperty() { return fullname; }
    public StringProperty usernameProperty() { return username; }
    public StringProperty emailProperty() { return email; }
    public ObjectProperty<LocalDate> birthdayProperty() { return birthday; }
    public StringProperty phoneProperty() { return phone; }
    public StringProperty coursesProperty() { return courses; }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getFullname() {
        return fullname.get();
    }

    public void setFullname(String fullname) {
        this.fullname.set(fullname);
    }

    public String getUsername() {
        return username.get();
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public LocalDate getBirthday() {
        return birthday.get();
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday.set(birthday);
    }

    public String getPhone() {
        return phone.get();
    }

    public void setPhone(String phone) {
        this.phone.set(phone);
    }

    public String getCourses() {
        return courses.get();
    }

    public void setCourses(String courses) {
        this.courses.set(courses);
    }

    public ObservableList<Course> getCourseList() {
        return courseList;
    }

    public void setCourseList(ObservableList<Course> courseList) {
        this.courseList = courseList;
    }

    @Override
    public String toString() {
        return this.getFullname();
    }
}
