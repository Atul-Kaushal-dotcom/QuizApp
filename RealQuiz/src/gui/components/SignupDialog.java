package com.realquiz.gui;

import com.realquiz.dao.UserDAO;
import com.realquiz.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SignupDialog extends JDialog {
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JComboBox<String> roleComboBox;
    private JButton signupButton;
    private JButton cancelButton;

    private User createdUser = null;
    private UserDAO userDAO;

    public SignupDialog(Frame parent) {
        super(parent, "Create New Account", true);
        this.userDAO = new UserDAO();
        initializeDialog();
        setupComponents();
        setupEventHandlers();
    }

    private void initializeDialog() {
        setSize(450, 400);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void setupComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JPanel headerPanel = new JPanel();
        JLabel titleLabel = new JLabel("Create Your Account");
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
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Username field
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(usernameField, gbc);

        // Email field
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        emailField = new JTextField(20);
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(emailField, gbc);

        // Password field
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(passwordField, gbc);

        // Confirm password field
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Confirm Password:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(confirmPasswordField, gbc);

        // Role selection
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Role:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        roleComboBox = new JComboBox<>(new String[]{"student", "teacher"});
        roleComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(roleComboBox, gbc);

        return formPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout());

        signupButton = new JButton("Create Account");
        cancelButton = new JButton("Cancel");

        styleButton(signupButton, new Color(46, 204, 113), Color.WHITE);
        styleButton(cancelButton, new Color(149, 165, 166), Color.WHITE);

        buttonPanel.add(signupButton);
        buttonPanel.add(cancelButton);

        return buttonPanel;
    }

    private void setupEventHandlers() {
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSignup();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void performSignup() {
        // Get input values
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String confirmPassword = new String(confirmPasswordField.getPassword()).trim();
        String role = (String) roleComboBox.getSelectedItem();

        // Validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showErrorMessage("All fields are required.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showErrorMessage("Passwords do not match.");
            return;
        }

        if (password.length() < 6) {
            showErrorMessage("Password must be at least 6 characters long.");
            return;
        }

        if (!isValidEmail(email)) {
            showErrorMessage("Please enter a valid email address.");
            return;
        }

        // Show loading
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        signupButton.setEnabled(false);

        // Perform signup in background
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                // Check if username already exists
                if (userDAO.usernameExists(username)) {
                    throw new Exception("Username already exists. Please choose a different one.");
                }

                // Create new user
                User newUser = new User();
                newUser.setUsername(username);