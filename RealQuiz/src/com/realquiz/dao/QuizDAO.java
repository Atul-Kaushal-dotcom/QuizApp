package com.realquiz.dao;

import com.realquiz.model.Quiz;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizDAO extends BaseDAO {

    // Create a new quiz
    public boolean createQuiz(Quiz quiz) {
        String sql = "INSERT INTO quizzes (title, description, created_by) VALUES (?, ?, ?)";
        Connection conn = null;

        try {
            conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, quiz.getTitle());
            stmt.setString(2, quiz.getDescription());
            stmt.setInt(3, quiz.getCreatedBy());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    quiz.setId(generatedKeys.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error creating quiz: " + e.getMessage());
        } finally {
            closeConnection(conn);
        }

        return false;
    }

    // Get all quizzes
    public List<Quiz> getAllQuizzes() {
        List<Quiz> quizzes = new ArrayList<>();
        String sql = "SELECT * FROM quizzes ORDER BY created_at DESC";
        Connection conn = null;

        try {
            conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Quiz quiz = new Quiz();
                quiz.setId(rs.getInt("id"));
                quiz.setTitle(rs.getString("title"));
                quiz.setDescription(rs.getString("description"));
                quiz.setCreatedBy(rs.getInt("created_by"));
                quiz.setCreatedAt(rs.getTimestamp("created_at"));

                quizzes.add(quiz);
            }

        } catch (SQLException e) {
            System.err.println("Error getting quizzes: " + e.getMessage());
        } finally {
            closeConnection(conn);
        }

        return quizzes;
    }

    // Get quizzes by user ID
    public List<Quiz> getQuizzesByUser(int userId) {
        List<Quiz> quizzes = new ArrayList<>();
        String sql = "SELECT * FROM quizzes WHERE created_by = ? ORDER BY created_at DESC";
        Connection conn = null;

        try {
            conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Quiz quiz = new Quiz();
                quiz.setId(rs.getInt("id"));
                quiz.setTitle(rs.getString("title"));
                quiz.setDescription(rs.getString("description"));
                quiz.setCreatedBy(rs.getInt("created_by"));
                quiz.setCreatedAt(rs.getTimestamp("created_at"));

                quizzes.add(quiz);
            }

        } catch (SQLException e) {
            System.err.println("Error getting user quizzes: " + e.getMessage());
        } finally {
            closeConnection(conn);
        }

        return quizzes;
    }
}