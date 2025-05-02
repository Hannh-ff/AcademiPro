package com.center.academipro.session;

public class SessionCourse {
    private static int courseId;
    private static int userId; // nếu sau này cần
    private static int classId; // nếu sau này cần

    public static int getCourseId() {
        return courseId;
    }

    public static void setCourseId(int courseId) {
        SessionCourse.courseId = courseId;
    }

    public static int getUserId() {
        return userId;
    }

    public static void setUserId(int userId) {
        SessionCourse.userId = userId;
    }

    public static int getClassId() {
        return classId;
    }

    public static void setClassId(int classId) {
        SessionCourse.classId = classId;
    }
}
