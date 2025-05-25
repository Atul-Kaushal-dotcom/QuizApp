package com.realquiz.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {
    // Database connection information
    private static final String URL = "jdbc:mysql://localhost:3306/realquiz_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "password@987"; // CHANGE THIS!

    private static Connection connection = null;

    public static void setConnection(Connection connection) {
        DatabaseUtil.connection = connection;
    }

    // Method to get database connection
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Load MySQL driver
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Create connection
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("Database connected successfully!");

            } catch (ClassNotFoundException e) {
                System.out.println("MySQL Driver not found!");
                e.printStackTrace();
            }
        }
        return connection;
    }

    // Method to close database connection
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to test if database connection works
    public static boolean testConnection() {
        try {
            Connection testConn = getConnection();
            if (testConn != null && !testConn.isClosed()) {
                System.out.println("✅ Database connection test successful!");
                return true;
            }
        } catch (SQLException e) {
            System.out.println("❌ Database connection test failed!");
            e.printStackTrace();
        }
        return false;
    }
}