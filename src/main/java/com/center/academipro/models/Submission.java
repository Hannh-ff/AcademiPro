package com.center.academipro.models;

public class Submission {
    private int id;
    private String studentName;
    private String assignmentTitle;
    private String submittedAt;
    private String fileLink;
    private String status;
    private String comment;

    public Submission(int id, String studentName, String assignmentTitle, String submittedAt, String fileLink, String status, String comment) {
        this.id = id;
        this.studentName = studentName;
        this.assignmentTitle = assignmentTitle;
        this.submittedAt = submittedAt;
        this.fileLink = fileLink;
        this.status = status;
        this.comment = comment;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getAssignmentTitle() {
        return assignmentTitle;
    }

    public void setAssignmentTitle(String assignmentTitle) {
        this.assignmentTitle = assignmentTitle;
    }

    public String getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(String submittedAt) {
        this.submittedAt = submittedAt;
    }

    public String getFileLink() {
        return fileLink;
    }

    public void setFileLink(String fileLink) {
        this.fileLink = fileLink;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
