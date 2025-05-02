package com.center.academipro.models;

import javafx.beans.property.*;

public class Class {

    private IntegerProperty id;
    private StringProperty className;
    private StringProperty teacherName;
    private StringProperty courseName;
    private IntegerProperty studentCount;


    public Class() {
    }

    public Class(int id, String className, String teacherName, String courseName, int studentCount) {
        this.id = new SimpleIntegerProperty(id);
        this.className = new SimpleStringProperty(className);
        this.teacherName = new SimpleStringProperty(teacherName);
        this.courseName = new SimpleStringProperty(courseName);
        this.studentCount = new SimpleIntegerProperty(studentCount);
    }
    public Class(int id,String className){
        this.id = new SimpleIntegerProperty(id);
        this.className = new SimpleStringProperty(className);
        this.teacherName = new SimpleStringProperty("");
        this.courseName = new SimpleStringProperty("");
        this.studentCount = new SimpleIntegerProperty(0);
    }

    public Class(int id, String className, String teacherName, String courseName){
        this.id = new SimpleIntegerProperty(id);
        this.className = new SimpleStringProperty(className);
        this.teacherName = new SimpleStringProperty(teacherName);
        this.courseName = new SimpleStringProperty(courseName);
        this.studentCount = new SimpleIntegerProperty(0);
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getClassName() {
        return className.get();
    }

    public StringProperty classNameProperty() {
        return className;
    }

    public void setClassName(String className) {
        this.className.set(className);
    }

    public String getTeacherName() {
        return teacherName.get();
    }

    public StringProperty teacherNameProperty() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName.set(teacherName);
    }

    public String getCourseName() {
        return courseName.get();
    }

    public StringProperty courseNameProperty() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName.set(courseName);
    }

    public int getStudentCount() {
        return studentCount.get();
    }

    public IntegerProperty studentCountProperty() {
        return studentCount;
    }

    public void setStudentCount(int studentCount) {
        this.studentCount.set(studentCount);
    }

    @Override
    public String toString() {
        return className.get();
    }
}
