package com.center.academipro.models;


public class SessionCourse {
    private static int courseId;
    private static String courseName;
    private static double coursePrice;
    private static String courseDescription;
    private static String courseImage;

    public static int getCourseId() {
        return courseId;
    }

    public static void setCourseId(int courseId) {
        SessionCourse.courseId = courseId;
    }

    // Tương tự cho các getter/setter khác
    public static String getCourseName() { return courseName; }
    public static void setCourseName(String name) { courseName = name; }

    public static double getCoursePrice() { return coursePrice; }
    public static void setCoursePrice(double price) { coursePrice = price; }

    public static String getCourseDescription() { return courseDescription; }
    public static void setCourseDescription(String desc) { courseDescription = desc; }

    public static String getCourseImage() { return courseImage; }
    public static void setCourseImage(String image) { courseImage = image; }
}