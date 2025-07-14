package View;

import DatabaseConfig.Database;
import Utils.SecurityUtil;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AddNewAccountGUI extends JFrame {

    public AddNewAccountGUI(Database database, int accRole) {
        setTitle("Create New Account");
        setSize(400, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Create " + (accRole == 1 ? "Admin" : "Client") + " Account", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        JTextField firstNameField = new JTextField();
        firstNameField.putClientProperty("JTextField.placeholderText", "First Name");
        JTextField lastNameField = new JTextField();
        lastNameField.putClientProperty("JTextField.placeholderText", "Last Name");
        JTextField emailField = new JTextField();
        emailField.putClientProperty("JTextField.placeholderText", "Email");
        JTextField phoneField = new JTextField();
        phoneField.putClientProperty("JTextField.placeholderText", "Phone Number");

        JPasswordField passwordField = new JPasswordField();
        passwordField.putClientProperty("JTextField.placeholderText", "Password");
        JPasswordField confirmPasswordField = new JPasswordField();
        confirmPasswordField.putClientProperty("JTextField.placeholderText", "Confirm Password");

        JButton createBtn = new JButton("Create Account");

        Component[] fields = {
            firstNameField, lastNameField, emailField, phoneField,
            passwordField, confirmPasswordField, Box.createVerticalStrut(10), createBtn
        };

        for (Component field : fields) {
            if (field instanceof JTextField || field instanceof JPasswordField) {
                ((JComponent) field).setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            }
            formPanel.add(field);
            formPanel.add(Box.createVerticalStrut(10));
        }

        createBtn.addActionListener(e -> {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields must be filled.");
                return;
            }

            if (!email.contains("@")) {
                JOptionPane.showMessageDialog(this, "Please enter a valid email.");
                return;
            }

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match.");
                return;
            }

            try {
                Connection conn = database.getConnection();

                // Check if email already exists
                PreparedStatement checkEmail = conn.prepareStatement("SELECT email FROM users WHERE email = ?");
                checkEmail.setString(1, email);
                ResultSet rs = checkEmail.executeQuery();
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "An account with this email already exists.");
                    return;
                }

                // Get next ID
                PreparedStatement countStmt = conn.prepareStatement("SELECT COUNT(*) AS count FROM users");
                ResultSet countRs = countStmt.executeQuery();
                int newId = 1;
                if (countRs.next()) {
                    newId = countRs.getInt("count") + 1;
                }

                // Insert account
                String hashedPassword = SecurityUtil.hashPassword(password);
                PreparedStatement insert = conn.prepareStatement(
                        "INSERT INTO users (ID, firstName, lastName, email, phoneNumber, password, role) VALUES (?, ?, ?, ?, ?, ?, ?)"
                );
                insert.setInt(1, newId);
                insert.setString(2, firstName);
                insert.setString(3, lastName);
                insert.setString(4, email);
                insert.setString(5, phone);
                insert.setString(6, hashedPassword);
                insert.setInt(7, accRole);

                insert.executeUpdate();

                JOptionPane.showMessageDialog(this, "Account created successfully!");
                dispose(); // Close the window
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "An error occurred while creating the account.");
            }
        });

        add(title, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        formPanel.getRootPane().setDefaultButton(createBtn);
        setVisible(true);
    }
}
