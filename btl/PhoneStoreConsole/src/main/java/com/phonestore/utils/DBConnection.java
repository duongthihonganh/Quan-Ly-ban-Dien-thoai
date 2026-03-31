package com.phonestore.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // Thông tin cấu hình XAMPP MySQL mặc định
    private static final String URL = "jdbc:mysql://localhost:3306/phone_store_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() {
        Connection connection = null;
        try {
            // Load MySQL Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("Lỗi: Không tìm thấy thư viện MySQL JDBC Driver.");
        } catch (SQLException e) {
            System.err.println("Lỗi kết nối CSDL MySQL: " + e.getMessage());
            System.err.println("Vui lòng đảm bảo XAMPP MySQL đã được bật và database 'phone_store_db' đã được tạo.");
        }
        return connection;
    }
}
