package main;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import DatabaseConfig.Database;
import Model.Admin;
import Model.Client;
import Model.User;
import Utils.SecurityUtil;
import View.AddNewAccountGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.ArrayList;

public class CarRentalApp {

    private static final Color PURPLE = new Color(0x9c84bc);
    private static final Dimension FIELD_SIZE = new Dimension(320, 40);
    private static final Dimension BUTTON_SIZE = new Dimension(320, 40);
    private static final Font DEFAULT_FONT = new Font("SansSerif", Font.PLAIN, 14);

    public static void main(String[] args) {
        // Set global UI properties BEFORE look and feel setup
        UIManager.put("Button.arc", 20);
        UIManager.put("Component.arc", 20);
        UIManager.put("Component.focusColor", PURPLE);
        UIManager.put("Button.focusColor", new Color(0, 0, 0, 0));
        UIManager.put("Button.default.focusColor", new Color(0, 0, 0, 0));
        UIManager.put("Button.hoverBackground", PURPLE);
        UIManager.put("Button.hoverForeground", Color.WHITE);
        UIManager.put("Button.default.background", PURPLE);
        UIManager.put("Button.default.foreground", Color.WHITE);
        UIManager.put("Button.default.borderColor", PURPLE);

        try {
            UIManager.setLookAndFeel(new FlatMacLightLaf());
            FlatLaf.setUseNativeWindowDecorations(true);
        } catch (Exception e) {
            System.err.println("Failed to set FlatLaf macOS Light theme");
            e.printStackTrace();
        }

        Database database = new Database();

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Car Rental Login");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setMinimumSize(new Dimension(400, 500));

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

            // Logo
            ImageIcon logoIcon = new ImageIcon(CarRentalApp.class.getResource("/resources/logo3.png"));
            JLabel logoLabel = new JLabel(logoIcon);
            logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Input fields
            JTextField emailField = new JTextField();
            emailField.putClientProperty("JTextField.placeholderText", "Email");
            emailField.setMaximumSize(FIELD_SIZE);
            emailField.setFont(DEFAULT_FONT);

            JPasswordField passwordField = new JPasswordField();
            passwordField.putClientProperty("JTextField.placeholderText", "Password");
            passwordField.setMaximumSize(FIELD_SIZE);
            passwordField.setFont(DEFAULT_FONT);

            // Buttons
            JButton loginButton = createButton("Login", PURPLE, DEFAULT_FONT);
            JButton createAccButton = createButton("Create Account", PURPLE.darker(), DEFAULT_FONT);

            loginButton.addActionListener((ActionEvent e) -> handleLogin(frame, database, emailField, passwordField));
            createAccButton.addActionListener((ActionEvent e) -> new AddNewAccountGUI(database, 0)); // 0 = client account

            panel.add(logoLabel);
            panel.add(Box.createVerticalStrut(30));
            panel.add(emailField);
            panel.add(Box.createVerticalStrut(10));
            panel.add(passwordField);
            panel.add(Box.createVerticalStrut(30));
            panel.add(loginButton);
            panel.add(Box.createVerticalStrut(10));
            panel.add(createAccButton);

            frame.setContentPane(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            frame.getRootPane().setDefaultButton(loginButton);
        });
    }

    private static JButton createButton(String text, Color bgColor, Font font) {
        JButton btn = new JButton(text);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(font);
        btn.setFocusPainted(false);
        btn.setMaximumSize(BUTTON_SIZE);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }

    private static void handleLogin(JFrame frame, Database database, JTextField emailField, JPasswordField passwordField) {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (!email.contains("@")) {
            JOptionPane.showMessageDialog(frame, "Please enter a valid email address.");
            return;
        }

        try {
            String hashedPassword = SecurityUtil.hashPassword(password);
            PreparedStatement pr = database.getConnection().prepareStatement(
                    "SELECT * FROM users WHERE email = ? AND password = ?"
            );
            pr.setString(1, email);
            pr.setString(2, hashedPassword);
            ResultSet rs = pr.executeQuery();

            User user = null;
            if (rs.next()) {
                int role = rs.getInt("role");
                user = switch (role) {
                    case 0 -> new Client();
                    case 1 -> new Admin();
                    default -> null;
                };
                if (user != null) {
                    user.setID(rs.getInt("ID"));
                    user.setFirstName(rs.getString("firstName"));
                    user.setLastName(rs.getString("lastName"));
                    user.setEmail(rs.getString("email"));
                    user.setPhoneNumber(rs.getString("phoneNumber"));
                    user.setPassword(rs.getString("password"));
                }
            }

            if (user == null) {
                JOptionPane.showMessageDialog(frame, "Email or password is incorrect.");
            } else {
                if (user instanceof Client clientUser) {
                    new View.ClientDashboard(database, clientUser);
                } else if (user instanceof Admin adminUser) {
                    new View.AdminDashboard(database, adminUser);
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Database error occurred.");
        }
    }
}
