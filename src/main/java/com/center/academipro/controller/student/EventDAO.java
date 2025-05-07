package com.center.academipro.controller.student;

import com.center.academipro.models.Class;
import com.center.academipro.models.Course;
import com.center.academipro.session.SessionManager;
import com.center.academipro.utils.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.List;

public class EventDAO {

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

    public static List<Class> getAllClassesByCourseId(int courseId) {
        ObservableList<Class> classes = FXCollections.observableArrayList();

        String sql = "SELECT classes.id AS class_id, classes.class_name, users.fullname AS teacher_name, courses.course_name " +
                "FROM classes " +
                "LEFT JOIN users ON classes.teacher_id = users.id " +
                "LEFT JOIN courses ON classes.course_id = courses.id " +
                "WHERE classes.course_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Class classObj = new Class(
                        rs.getInt("class_id"),
                        rs.getString("class_name"),
                        rs.getString("teacher_name"),
                        rs.getString("course_name")
                );
                classes.add(classObj);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return classes;
    }

    public static List<Class> getJoinedClassesByUserId() {
        ObservableList<Class> joinedClasses = FXCollections.observableArrayList();
        int userId = SessionManager.getInstance().getUserId();

        String sql = """
                    SELECT
                        c.id AS class_id,
                        c.class_name,
                        crs.course_name,
                        u.fullname AS teacher_name
                    FROM student_classes sc
                    JOIN students s ON sc.student_id = s.id
                    JOIN classes c ON sc.class_id = c.id
                    JOIN courses crs ON c.course_id = crs.id
                    LEFT JOIN users u ON c.teacher_id = u.id
                    WHERE s.user_id = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Class classObj = new Class(
                        rs.getInt("class_id"),
                        rs.getString("class_name"),
                        rs.getString("teacher_name"),
                        rs.getString("course_name")
                );
                joinedClasses.add(classObj);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return joinedClasses;
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
