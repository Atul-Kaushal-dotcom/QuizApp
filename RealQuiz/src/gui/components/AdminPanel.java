
// src/main/java/com/quizapp/view/AdminPanel.java
package com.quizapp.view;

import com.quizapp.model.Question;
import com.quizapp.util.DatabaseManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.Vector;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * This JPanel allows administrators to manage quiz questions:
 * Add, Edit, Delete, Import, and Export questions.
 */
public class AdminPanel extends JPanel {
    private DatabaseManager dbManager;
    private QuizFrame parentFrame;

    // UI Components for Question Management
    private JTable questionsTable;
    private DefaultTableModel tableModel;
    private JScrollPane tableScrollPane;

    private JTextField questionField, optionAField, optionBField, optionCField, optionDField, correctOptionField;
    private JButton addButton, updateButton, deleteButton, clearButton;

    // UI Components for Import/Export
    private JButton importButton, exportButton;
    private JLabel statusLabel;

    public AdminPanel(QuizFrame parentFrame, DatabaseManager dbManager) {
        this.parentFrame = parentFrame;
        this.dbManager = dbManager;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initComponents();
        addComponents();
        addListeners();
        loadQuestionsIntoTable(); // Load existing questions when panel is initialized
    }

    /**
     * Initializes GUI components.
     */
    private void initComponents() {
        // Table setup
        String[] columnNames = {"ID", "Question", "Option A", "Option B", "Option C", "Option D", "Correct"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        questionsTable = new JTable(tableModel);
        questionsTable.setFont(new Font("Inter", Font.PLAIN, 12));
        questionsTable.setRowHeight(20);
        questionsTable.getTableHeader().setFont(new Font("Inter", Font.BOLD, 12));
        questionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Only one row can be selected

        tableScrollPane = new JScrollPane(questionsTable);
        tableScrollPane.setPreferredSize(new Dimension(700, 300));
        tableScrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));


        // Input fields
        questionField = new JTextField(40);
        optionAField = new JTextField(20);
        optionBField = new JTextField(20);
        optionCField = new JTextField(20);
        optionDField = new JTextField(20);
        correctOptionField = new JTextField(5); // For A, B, C, D
        correctOptionField.setDocument(new FixedCharDocument(1)); // Limit input to 1 character


        // Buttons
        addButton = createStyledButton("Add Question", new Color(40, 167, 69)); // Green
        updateButton = createStyledButton("Update Selected", new Color(0, 123, 255)); // Blue
        deleteButton = createStyledButton("Delete Selected", new Color(220, 53, 69)); // Red
        clearButton = createStyledButton("Clear Fields", new Color(108, 117, 125)); // Grey

        importButton = createStyledButton("Import Questions", new Color(23, 162, 184)); // Teal
        exportButton = createStyledButton("Export Questions", new Color(255, 193, 7)); // Yellow/Orange

        statusLabel = new JLabel("Status: Ready");
        statusLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        statusLabel.setForeground(Color.DARK_GRAY);
    }

    /**
     * Helper method to create styled JButtons.
     */
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Inter", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    /**
     * Adds components to the panel using different layouts.
     */
    private void addComponents() {
        // Input form panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Question Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding between components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; inputPanel.add(new JLabel("Question:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.gridwidth = 3; inputPanel.add(questionField, gbc);

        gbc.gridwidth = 1; // Reset gridwidth
        gbc.gridx = 0; gbc.gridy = row; inputPanel.add(new JLabel("Option A:"), gbc);
        gbc.gridx = 1; gbc.gridy = row; inputPanel.add(optionAField, gbc);
        gbc.gridx = 2; gbc.gridy = row; inputPanel.add(new JLabel("Option B:"), gbc);
        gbc.gridx = 3; gbc.gridy = row++; inputPanel.add(optionBField, gbc);

        gbc.gridx = 0; gbc.gridy = row; inputPanel.add(new JLabel("Option C:"), gbc);
        gbc.gridx = 1; gbc.gridy = row; inputPanel.add(optionCField, gbc);
        gbc.gridx = 2; gbc.gridy = row; inputPanel.add(new JLabel("Option D:"), gbc);
        gbc.gridx = 3; gbc.gridy = row++; inputPanel.add(optionDField, gbc);

        gbc.gridx = 0; gbc.gridy = row; inputPanel.add(new JLabel("Correct Option (A/B/C/D):"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; inputPanel.add(correctOptionField, gbc);

        // Action buttons panel
        JPanel actionButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        actionButtonPanel.add(addButton);
        actionButtonPanel.add(updateButton);
        actionButtonPanel.add(deleteButton);
        actionButtonPanel.add(clearButton);

        // Import/Export panel
        JPanel fileButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        fileButtonPanel.add(importButton);
        fileButtonPanel.add(exportButton);

        // Combine input and action panels
        JPanel formAndButtons = new JPanel(new BorderLayout());
        formAndButtons.add(inputPanel, BorderLayout.CENTER);
        formAndButtons.add(actionButtonPanel, BorderLayout.SOUTH);

        add(formAndButtons, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(fileButtonPanel, BorderLayout.NORTH);
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Adds event listeners to components.
     */
    private void addListeners() {
        addButton.addActionListener(e -> addQuestion());
        updateButton.addActionListener(e -> updateQuestion());
        deleteButton.addActionListener(e -> deleteQuestion());
        clearButton.addActionListener(e -> clearFields());
        importButton.addActionListener(e -> importQuestions());
        exportButton.addActionListener(e -> exportQuestions());

        // Listener for table row selection to populate fields for editing
        questionsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && questionsTable.getSelectedRow() != -1) {
                int selectedRow = questionsTable.getSelectedRow();
                questionField.setText((String) tableModel.getValueAt(selectedRow, 1));
                optionAField.setText((String) tableModel.getValueAt(selectedRow, 2));
                optionBField.setText((String) tableModel.getValueAt(selectedRow, 3));
                optionCField.setText((String) tableModel.getValueAt(selectedRow, 4));
                optionDField.setText((String) tableModel.getValueAt(selectedRow, 5));
                correctOptionField.setText(String.valueOf(tableModel.getValueAt(selectedRow, 6)));
            }
        });
    }

    /**
     * Loads questions from the database and populates the table.
     */
    public void loadQuestionsIntoTable() {
        tableModel.setRowCount(0); // Clear existing data
        List<Question> questions = dbManager.loadQuestions();
        for (Question q : questions) {
            tableModel.addRow(new Object[]{
                q.getId(),
                q.getQuestionText(),
                q.getOptionA(),
                q.getOptionB(),
                q.getOptionC(),
                q.getOptionD(),
                q.getCorrectOption()
            });
        }
        statusLabel.setText("Status: Loaded " + questions.size() + " questions.");
    }

    /**
     * Validates input fields before adding/updating a question.
     * @return true if input is valid, false otherwise.
     */
    private boolean validateInput() {
        if (questionField.getText().trim().isEmpty() ||
            optionAField.getText().trim().isEmpty() ||
            optionBField.getText().trim().isEmpty() ||
            optionCField.getText().trim().isEmpty() ||
            optionDField.getText().trim().isEmpty() ||
            correctOptionField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        char correctOption = correctOptionField.getText().trim().toUpperCase().charAt(0);
        if (correctOption != 'A' && correctOption != 'B' && correctOption != 'C' && correctOption != 'D') {
            JOptionPane.showMessageDialog(this, "Correct option must be A, B, C, or D.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * Adds a new question to the database.
     */
    private void addQuestion() {
        if (!validateInput()) return;

        Question newQuestion = new Question(
            questionField.getText().trim(),
            optionAField.getText().trim(),
            optionBField.getText().trim(),
            optionCField.getText().trim(),
            optionDField.getText().trim(),
            correctOptionField.getText().trim().toUpperCase().charAt(0)
        );

        if (dbManager.addQuestion(newQuestion)) {
            JOptionPane.showMessageDialog(this, "Question added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadQuestionsIntoTable(); // Refresh table
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add question.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Updates the selected question in the database.
     */
    private void updateQuestion() {
        int selectedRow = questionsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a question to update.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validateInput()) return;

        int questionId = (int) tableModel.getValueAt(selectedRow, 0);
        Question updatedQuestion = new Question(
            questionId,
            questionField.getText().trim(),
            optionAField.getText().trim(),
            optionBField.getText().trim(),
            optionCField.getText().trim(),
            optionDField.getText().trim(),
            correctOptionField.getText().trim().toUpperCase().charAt(0)
        );

        if (dbManager.updateQuestion(updatedQuestion)) {
            JOptionPane.showMessageDialog(this, "Question updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadQuestionsIntoTable(); // Refresh table
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update question.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Deletes the selected question from the database.
     */
    private void deleteQuestion() {
        int selectedRow = questionsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a question to delete.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this question?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int questionId = (int) tableModel.getValueAt(selectedRow, 0);
            if (dbManager.deleteQuestion(questionId)) {
                JOptionPane.showMessageDialog(this, "Question deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadQuestionsIntoTable(); // Refresh table
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete question.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Clears all input fields and table selection.
     */
    private void clearFields() {
        questionField.setText("");
        optionAField.setText("");
        optionBField.setText("");
        optionCField.setText("");
        optionDField.setText("");
        correctOptionField.setText("");
        questionsTable.clearSelection();
    }

    /**
     * Imports questions from a CSV file.
     * Expected format: question_text,option_a,option_b,option_c,option_d,correct_option
     */
    private void importQuestions() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Import Questions from CSV");
        int userSelection = fileChooser.showOpenDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToOpen = fileChooser.getSelectedFile();
            int importedCount = 0;
            try (BufferedReader br = new BufferedReader(new FileReader(fileToOpen))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",", 6); // Split into 6 parts
                    if (parts.length == 6) {
                        try {
                            Question newQuestion = new Question(
                                parts[0].trim(), // question_text
                                parts[1].trim(), // option_a
                                parts[2].trim(), // option_b
                                parts[3].trim(), // option_c
                                parts[4].trim(), // option_d
                                parts[5].trim().toUpperCase().charAt(0) // correct_option
                            );
                            if (dbManager.addQuestion(newQuestion)) {
                                importedCount++;
                            }
                        } catch (Exception e) {
                            System.err.println("Skipping malformed line: " + line + " - " + e.getMessage());
                        }
                    }
                }
                JOptionPane.showMessageDialog(this, "Successfully imported " + importedCount + " questions.", "Import Complete", JOptionPane.INFORMATION_MESSAGE);
                loadQuestionsIntoTable(); // Refresh table after import
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error reading file: " + e.getMessage(), "Import Error", JOptionPane.ERROR_MESSAGE);
                System.err.println("Error importing questions: " + e.getMessage());
            }
        }
    }

    /**
     * Exports all questions to a CSV file.
     * Format: id,question_text,option_a,option_b,option_c,option_d,correct_option
     */
    private void exportQuestions() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Questions to CSV");
        fileChooser.setSelectedFile(new File("questions_export.csv")); // Default file name
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            List<Question> questions = dbManager.loadQuestions();
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileToSave))) {
                for (Question q : questions) {
                    bw.write(String.format("%d,%s,%s,%s,%s,%s,%c%n",
                        q.getId(),
                        escapeCsv(q.getQuestionText()),
                        escapeCsv(q.getOptionA()),
                        escapeCsv(q.getOptionB()),
                        escapeCsv(q.getOptionC()),
                        escapeCsv(q.getOptionD()),
                        q.getCorrectOption()
                    ));
                }
                JOptionPane.showMessageDialog(this, "Successfully exported " + questions.size() + " questions to " + fileToSave.getAbsolutePath(), "Export Complete", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error writing file: " + e.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
                System.err.println("Error exporting questions: " + e.getMessage());
            }
        }
    }

    /**
     * Helper method to escape strings for CSV output.
     * Encloses string in double quotes if it contains commas or double quotes.
     * Doubles inner double quotes.
     */
    private String escapeCsv(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    // Custom DocumentFilter to limit JTextField to 1 character
    private static class FixedCharDocument extends PlainDocument {
        private final int limit;

        public FixedCharDocument(int limit) {
            super();
            this.limit = limit;
        }

        @Override
        public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
            if (str == null) return;
            if ((getLength() + str.length()) <= limit) {
                super.insertString(offset, str, attr);
            }
        }
    }
}
