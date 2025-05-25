package com.realquiz;

import com.realquiz.util.DatabaseUtil;

public class RealQuizApp {
    public static void main(String[] args) {
        System.out.println("Welcome to RealQuiz!");
        System.out.println("Testing database connection...");

        // Test database connection
        if (DatabaseUtil.testConnection()) {
            System.out.println("Great! Database is connected.");
        } else {
            System.out.println("Oops! Database connection failed.");
            System.out.println("Check your MySQL password in DatabaseUtil.java");
        }
    }
}