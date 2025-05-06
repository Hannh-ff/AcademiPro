package com.center.academipro.controller;

import com.center.academipro.utils.DBConnection;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import java.sql.*;

public class DashboardController {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/academipro";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    @FXML
    private Label lblTotalStudent;
    @FXML
    private Label lblTotalTeacher;
    @FXML
    private Label lblTotalCourse;
    @FXML
    private Label lblTotalIncome;
    @FXML
    private AreaChart<String, Number> AreaTimeStudent;
    @FXML
    private LineChart<String, Number> LineIncome;
    @FXML
    private BarChart<String, Number> BarChartTeachers;

    @FXML
    public void initialize() {
        int totalStudents = getTotalStudents();
        lblTotalStudent.setText(String.valueOf(totalStudents));

        int totalCourses = getTotalCourses();
        lblTotalCourse.setText(String.valueOf(totalCourses));

        int totalTeachers = getTotalTeachers();
        lblTotalTeacher.setText(String.valueOf(totalTeachers));

        loadTotalIncomeToLabel();

        // AreaChart
        XYChart.Series<String, Number> seriesStudent = new XYChart.Series<>();
        seriesStudent.setName("Study Time");

        seriesStudent.getData().add(new XYChart.Data<>("08:00", 30));
        seriesStudent.getData().add(new XYChart.Data<>("09:00", 45));
        seriesStudent.getData().add(new XYChart.Data<>("10:00", 20));
        seriesStudent.getData().add(new XYChart.Data<>("11:00", 50));
        seriesStudent.getData().add(new XYChart.Data<>("12:00", 35));
        AreaTimeStudent.getData().add(seriesStudent);

        XYChart.Series<String, Number> seriesTeacher = new XYChart.Series<>();
        seriesTeacher.setName("Teaching Hours");

        seriesTeacher.getData().add(new XYChart.Data<>("Cô Lan", 40));
        seriesTeacher.getData().add(new XYChart.Data<>("Thầy Minh", 35));
        seriesTeacher.getData().add(new XYChart.Data<>("Thầy Hùng", 25));
        seriesTeacher.getData().add(new XYChart.Data<>("Cô Trang", 30));
        seriesTeacher.getData().add(new XYChart.Data<>("Thầy Sơn", 45));
        BarChartTeachers.getData().clear();
        BarChartTeachers.getData().add(seriesTeacher);

        loadIncome();

    }

    public int getTotalStudents() {
        int total = 0;
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM students");
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;

    }

    public void setLblTotalCourse(Label lblTotalCourse) {
        this.lblTotalCourse = lblTotalCourse;
    }

    public int getTotalCourses() {
        int total = 0;
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM courses");
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }

    public void setLblTotalCourses(Label lblTotalCourse) {
        this.lblTotalCourse = lblTotalCourse;
    }

    public int getTotalTeachers() {
        int total = 0;
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM teachers");
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }

    public void setLblTotalTeachers(Label lblTotalTeacher) {
        this.lblTotalTeacher = this.lblTotalTeacher;
    }

    public void loadIncome(){
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        String query = "SELECT DATE_FORMAT(payment_date, '%Y-%m') AS month, SUM(amount) AS total_income " +
                "FROM payments WHERE payment_status = 'Completed' " +
                "GROUP BY month ORDER BY month";

        try(Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String month = rs.getString("month");
                double income = rs.getDouble("total_income");
                series.getData().add(new XYChart.Data<>(month, income));
            }

            LineIncome.getData().clear();
            LineIncome.getData().add(series);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadTotalIncomeToLabel() {
        String query = "SELECT SUM(amount) AS total_income " +
                "FROM payments WHERE payment_status = 'Completed'";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                double totalIncome = rs.getDouble("total_income");
                lblTotalIncome.setText(String.format("%.0f USD", totalIncome));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lblTotalIncome.setText("Lỗi");
        }
    }
}
