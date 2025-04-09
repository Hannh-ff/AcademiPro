package com.center.academipro.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/academipro";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // Phương thức để lấy kết nối
    public Connection getConn() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            return conn; // Trả về kết nối mới mỗi lần gọi
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to connect to database.", e);
        }
    }

    // Phương thức để đóng kết nối (nếu cần)
    public static void close(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
