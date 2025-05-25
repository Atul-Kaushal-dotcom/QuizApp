package com.realquiz.gui;

import com.realquiz.dao.UserDAO;
import com.realquiz.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton cancelButton;
    private JButton signupButton;

    private User loggedInUser = null;
    private UserDAO userDAO;

    public LoginDialog(Frame parent) {
        super(parent, "Login to RealQuiz", true);
        this.userDAO = new UserDAO();
        initializeDialog();
        setupComponents();
        setupEventHandlers();
    }

    private void initializeDialog() {
        setSize(400, 300);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void setupComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JPanel headerPanel = new JPanel();
        JLabel titleLabel = new JLabel("Login to Your Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(52, 73, 94));
        headerPanel.add(titleLabel);

        // Form panel
        JPanel formPanel = createFormPanel();

        // Button panel
        JPanel buttonPanel = createButtonPanel();

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Username field
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(usernameField, gbc);

        // Password field
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(passwordField, gbc);

        return formPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout());

        loginButton = new JButton("Login");
        cancelButton = new JButton("Cancel");
        signupButton = new JButton("Sign Up");

        styleButton(loginButton, new Color(52, 152, 219), Color.WHITE);
        styleButton(cancelButton, new Color(149, 165, 166), Color.WHITE);
        styleButton(signupButton, new Color(46, 204, 113), Color.WHITE);

        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(signupButton);

        return buttonPanel;
    }

    private void setupEventHandlers() {
        // Login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });

        // Cancel button
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // Sign up button
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openSignupDialog();
            }
        });

        // Enter key on password field
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        // Validation
        if (username.isEmpty() || password.isEmpty()) {
            showErrorMessage("Please enter both username and password.");
            return;
        }

        // Show loading cursor
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        loginButton.setEnabled(false);

        // Perform login in background thread
        SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                return userDAO.findUser(username, password);
            }

            @Override
            protected void done() {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                loginButton.setEnabled(true);

                try {
                    User user = get();
                    if (user != null) {
                        loggedInUser = user;
                        JOptionPane.showMessageDialog(LoginDialog.this,
                                "Welcome back, " + user.getUsername() + "!");
                        dispose();
                    } else {
                        showErrorMessage("Invalid username or password.");
                        passwordField.setText("");
                        usernameField.requestFocus();
                    }
                } catch (Exception e) {
                    showErrorMessage("Login failed: " + e.getMessage());
                }
            }
        };

        worker.execute();
    }

    private void openSignupDialog() {
        SignupDialog signupDialog = new SignupDialog((Frame) getParent());
        signupDialog.setVisible(true);

        if (signupDialog.getCreatedUser() != null) {
            dispose(); // Close login dialog if signup was successful
        }
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Login Error", JOptionPane.ERROR_MESSAGE);
    }

    private void styleButton(JButton button, Color bgColor, Color textColor) {
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(100, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // Getter for the logged in user
    public User getLoggedInUser() {
        return loggedInUser;
    }
}