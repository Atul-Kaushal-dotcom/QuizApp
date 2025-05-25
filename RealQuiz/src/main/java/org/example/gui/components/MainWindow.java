package com.realquiz.gui;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 700;

    public MainWindow() {
        initializeWindow();
        setupComponents();
    }

    private void initializeWindow() {
        setTitle("RealQuiz - Quiz Management System");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setResizable(true);

        // Set application icon (optional)
        // setIconImage(new ImageIcon("icon.png").getImage());
    }

    private void setupComponents() {
        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Create content panel (this will change based on current view)
        JPanel contentPanel = createWelcomePanel();
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Create footer panel
        JPanel footerPanel = createFooterPanel();
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 73, 94)); // Dark blue
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Logo/Title
        JLabel titleLabel = new JLabel("RealQuiz");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        // User info panel (will be populated after login)
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);

        JButton loginButton = new JButton("Login");
        styleButton(loginButton, new Color(52, 152, 219), Color.WHITE);

        userPanel.add(loginButton);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createWelcomePanel() {
        JPanel welcomePanel = new JPanel(new GridBagLayout());
        welcomePanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        // Welcome title
        JLabel welcomeLabel = new JLabel("Welcome to RealQuiz!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 36));
        welcomeLabel.setForeground(new Color(52, 73, 94));
        gbc.gridx = 0;
        gbc.gridy = 0;
        welcomePanel.add(welcomeLabel, gbc);

        // Description
        JLabel descLabel = new JLabel("<html><center>Create and take quizzes with ease.<br>Get started by logging in or creating an account.</center></html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        descLabel.setForeground(new Color(127, 140, 141));
        gbc.gridy = 1;
        welcomePanel.add(descLabel, gbc);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout());
        buttonsPanel.setOpaque(false);

        JButton loginBtn = new JButton("Login");
        JButton signupBtn = new JButton("Sign Up");

        styleButton(loginBtn, new Color(52, 152, 219), Color.WHITE);
        styleButton(signupBtn, new Color(46, 204, 113), Color.WHITE);

        buttonsPanel.add(loginBtn);
        buttonsPanel.add(signupBtn);

        gbc.gridy = 2;
        welcomePanel.add(buttonsPanel, gbc);

        return welcomePanel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(new Color(236, 240, 241));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel footerLabel = new JLabel("© 2024 RealQuiz - Built with Java Swing");
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        footerLabel.setForeground(new Color(127, 140, 141));

        footerPanel.add(footerLabel);

        return footerPanel;
    }

    // Utility method to style buttons consistently
    private void styleButton(JButton button, Color bgColor, Color textColor) {
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(120, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalColor = button.getBackground();

            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor);
            }
        });
    }

    public static void main(String[] args) {
        // Set Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create and show GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new MainWindow().setVisible(true);
        });
    }
}