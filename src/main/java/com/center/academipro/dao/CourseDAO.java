package com.center.academipro.dao;

import com.center.academipro.models.Course;
import com.center.academipro.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {
    public List<Course> getAllCourse(){
        List<Course> list = new ArrayList<>();
        String query ="SELECT * FROM courses";
        try(Connection conn = new DBConnection().getConn();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery()){
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("course_name");
                String description = rs.getString("description");
                String image = rs.getString("image");
                double price = rs.getDouble("price");

                Course course = new Course(id, name, description, image, price);
                list.add(course);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }
    public List<Course> getCoursesByTeacherId(int teacherId) {
        List<Course> courses = new ArrayList<>();
        String query = "SELECT c.id, c.course_name, c.description, c.image, c.price " +
                "FROM courses c " +
                "JOIN teacher_courses tc ON c.id = tc.course_id " +
                "WHERE tc.teacher_id = ?";

        try (Connection conn = new DBConnection().getConn();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, teacherId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Course course = new Course();
                course.setId(rs.getInt("id"));
                course.setCourseName(rs.getString("course_name"));
                course.setDescription(rs.getString("description"));
                course.setImage(rs.getString("image"));
                course.setPrice(rs.getDouble("price"));
                courses.add(course);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return courses;
    }


}
