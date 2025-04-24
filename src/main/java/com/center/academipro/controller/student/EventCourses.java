package com.center.academipro.controller.student;

import com.center.academipro.models.Course;
import com.center.academipro.session.SessionManager;
import com.center.academipro.utils.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.List;

public class EventCourses {

    public static List<Course> getAllCourses() {
        ObservableList<Course> courses = FXCollections.observableArrayList();
        String sql = "SELECT * FROM courses";

        try (Connection conn = DBConnection.getConn();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Course course = new Course();
                course.setId(rs.getInt("id"));
                course.setCourseName(rs.getString("course_name"));
                course.setDescription(rs.getString("description"));
                course.setImage(rs.getString("image")); // ảnh là tên file trong thư mục resources
                course.setPrice(rs.getDouble("price"));
                courses.add(course);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    public static List<Course> getPurchasedCoursesByUserId() {
        ObservableList<Course> purchasedCourses = FXCollections.observableArrayList();
        int userId = SessionManager.getInstance().getUserId();

        String sql = "SELECT c.* FROM courses c " +
                "JOIN purchase_history ph ON c.id = ph.course_id " +
                "WHERE ph.user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Course course = new Course(
                        rs.getInt("id"),
                        rs.getString("course_name"),
                        rs.getString("description"),
                        rs.getString("image"),
                        rs.getDouble("price")
                );
                purchasedCourses.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return purchasedCourses;
    }

    public static void cancelCourse(int courseId) {
        int userId = SessionManager.getInstance().getUserId();
        String sql = "DELETE FROM purchase_history WHERE user_id = ? AND course_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, courseId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
