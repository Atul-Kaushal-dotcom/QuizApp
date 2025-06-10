
// src/main/java/com/quizapp/util/DatabaseManager.java
package com.quizapp.util;

import com.quizapp.model.Question;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages all database interactions for the QuizApp.
 * Uses JDBC to connect to a MySQL database.
 */
public class DatabaseManager {
    // JDBC URL, username, and password of MySQL database
    private static final String DB_URL = "jdbc:mysql://localhost:3306/QuizAppDB";
    private static final String USER = "your_mysql_username"; // !!! CHANGE THIS
    private static final String PASS = "your_mysql_password"; // !!! CHANGE THIS

    /**
     * Establishes a connection to the database.
     * @return A valid Connection object, or null if connection fails.
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    /**
     * Loads all questions from the database.
     * @return A list of Question objects.
     */
    public List<Question> loadQuestions() {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT id, question_text, option_a, option_b, option_c, option_d, correct_option FROM questions";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String questionText = rs.getString("question_text");
                String optionA = rs.getString("option_a");
                String optionB = rs.getString("option_b");
                String optionC = rs.getString("option_c");
                String optionD = rs.getString("option_d");
                char correctOption = rs.getString("correct_option").charAt(0);

                questions.add(new Question(id, questionText, optionA, optionB, optionC, optionD, correctOption));
            }
        } catch (SQLException e) {
            System.err.println("Error loading questions from database: " + e.getMessage());
            // In a real app, you might show a GUI error message or log it
        }
        return questions;
    }

    /**
     * Adds a new question to the database.
     * @param question The Question object to add.
     * @return true if successful, false otherwise.
     */
    public boolean addQuestion(Question question) {
        String sql = "INSERT INTO questions (question_text, option_a, option_b, option_c, option_d, correct_option) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, question.getQuestionText());
            pstmt.setString(2, question.getOptionA());
            pstmt.setString(3, question.getOptionB());
            pstmt.setString(4, question.getOptionC());
            pstmt.setString(5, question.getOptionD());
            pstmt.setString(6, String.valueOf(question.getCorrectOption()));

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                // Get the generated ID and update the Question object
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        question.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding question to database: " + e.getMessage());
        }
        return false;
    }

    /**
     * Updates an existing question in the database.
     * @param question The Question object with updated details.
     * @return true if successful, false otherwise.
     */
    public boolean updateQuestion(Question question) {
        String sql = "UPDATE questions SET question_text = ?, option_a = ?, option_b = ?, option_c = ?, option_d = ?, correct_option = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, question.getQuestionText());
            pstmt.setString(2, question.getOptionA());
            pstmt.setString(3, question.getOptionB());
            pstmt.setString(4, question.getOptionC());
            pstmt.setString(5, question.getOptionD());
            pstmt.setString(6, String.valueOf(question.getCorrectOption()));
            pstmt.setInt(7, question.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating question in database: " + e.getMessage());
        }
        return false;
    }

    /**
     * Deletes a question from the database by its ID.
     * @param questionId The ID of the question to delete.
     * @return true if successful, false otherwise.
     */
    public boolean deleteQuestion(int questionId) {
        String sql = "DELETE FROM questions WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, questionId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting question from database: " + e.getMessage());
        }
        return false;
    }
}
