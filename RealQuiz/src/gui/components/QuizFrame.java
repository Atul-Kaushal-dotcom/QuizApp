
// src/main/java/com/quizapp/view/QuizFrame.java
package com.quizapp.view;

import com.quizapp.model.Question;
import com.quizapp.util.DatabaseManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * The main JFrame for the Quiz Application.
 * Manages the display of different panels (QuizPanel, ResultPanel, AdminPanel).
 */
public class QuizFrame extends JFrame {
    private QuizPanel quizPanel;
    private ResultPanel resultPanel;
    private AdminPanel adminPanel;
    private JPanel cardPanel; // Panel to hold different views using CardLayout
    private CardLayout cardLayout;

    private DatabaseManager dbManager; // Database manager instance

    public QuizFrame() {
        super("Quiz App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600); // Set a preferred size
        setMinimumSize(new Dimension(700, 500)); // Set minimum size for responsiveness
        setLocationRelativeTo(null); // Center the window on the screen

        dbManager = new DatabaseManager(); // Initialize DatabaseManager

        initComponents();
        addComponents();
        addMenuBar(); // Add menu bar for navigation
        showWelcomePanel(); // Start with a welcome or initial screen
    }

    /**
     * Initializes the various panels.
     */
    private void initComponents() {
        quizPanel = new QuizPanel(this);
        resultPanel = new ResultPanel(this);
        adminPanel = new AdminPanel(this, dbManager); // Pass dbManager to AdminPanel

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.add(createWelcomePanel(), "WelcomePanel"); // Add a welcome panel first
        cardPanel.add(quizPanel, "QuizPanel");
        cardPanel.add(resultPanel, "ResultPanel");
        cardPanel.add(adminPanel, "AdminPanel");
    }

    /**
     * Creates a simple welcome panel.
     */
    private JPanel createWelcomePanel() {
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(new Color(240, 248, 255)); // AliceBlue
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JLabel titleLabel = new JLabel("Welcome to Quiz App!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 36));
        titleLabel.setForeground(new Color(51, 51, 51)); // Dark grey

        JLabel descriptionLabel = new JLabel(
                "<html><div style='text-align: center;'>" +
                "Test your knowledge with exciting questions!<br><br>" +
                "Use the 'Quiz' option to start taking quizzes.<br>" +
                "Use the 'Admin' option to manage questions." +
                "</div></html>", SwingConstants.CENTER);
        descriptionLabel.setFont(new Font("Inter", Font.PLAIN, 16));
        descriptionLabel.setForeground(new Color(75, 75, 75));

        welcomePanel.add(titleLabel, BorderLayout.NORTH);
        welcomePanel.add(descriptionLabel, BorderLayout.CENTER);
        return welcomePanel;
    }


    /**
     * Adds the card panel to the frame.
     */
    private void addComponents() {
        add(cardPanel);
    }

    /**
     * Adds a menu bar for navigation between different sections.
     */
    private void addMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(52, 58, 64)); // Dark grey background for menu

        JMenu fileMenu = new JMenu("File");
        fileMenu.setForeground(Color.WHITE);
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        JMenu viewMenu = new JMenu("View");
        viewMenu.setForeground(Color.WHITE);

        JMenuItem quizItem = new JMenuItem("Take Quiz");
        quizItem.addActionListener(e -> showQuizPanel());
        viewMenu.add(quizItem);

        JMenuItem adminItem = new JMenuItem("Admin Panel");
        adminItem.addActionListener(e -> showAdminPanel());
        viewMenu.add(adminItem);

        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        setJMenuBar(menuBar);
    }

    /**
     * Shows the welcome panel.
     */
    public void showWelcomePanel() {
        cardLayout.show(cardPanel, "WelcomePanel");
    }

    /**
     * Shows the QuizPanel and starts a new quiz.
     */
    public void showQuizPanel() {
        List<Question> questions = dbManager.loadQuestions();
        if (questions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No questions available in the database. Please add questions via Admin Panel.", "No Questions", JOptionPane.WARNING_MESSAGE);
            showWelcomePanel(); // Go back to welcome if no questions
            return;
        }
        quizPanel.startQuiz(questions);
        cardLayout.show(cardPanel, "QuizPanel");
    }

    /**
     * Shows the ResultPanel with the given score and question details.
     * @param score The user's score.
     * @param questions The list of all questions.
     * @param userAnswers The list of user's answers.
     */
    public void showResultPanel(int score, List<Question> questions, List<Character> userAnswers) {
        resultPanel.displayResults(score, questions, userAnswers);
        cardLayout.show(cardPanel, "ResultPanel");
    }

    /**
     * Shows the AdminPanel and reloads questions into its table.
     */
    public void showAdminPanel() {
        adminPanel.loadQuestionsIntoTable(); // Refresh table data
        cardLayout.show(cardPanel, "AdminPanel");
    }

    /**
     * Main method to run the application.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        // Ensure GUI updates are done on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            QuizFrame frame = new QuizFrame();
            frame.setVisible(true);
        });
    }
}
