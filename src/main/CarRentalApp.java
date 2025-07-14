package main;

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

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatMacLightLaf());
        } catch (Exception e) {
            System.err.println("Failed to set FlatLaf macOS Light theme");
        }

        Database database = new Database();
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Car Rental Login");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

            ImageIcon logoIcon = new ImageIcon(CarRentalApp.class.getResource("/resources/logo3.png"));
            JLabel logoLabel = new JLabel(logoIcon);
            logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

//            JLabel title = new JLabel("VRS PSYCHO Rentals");
//            title.setAlignmentX(Component.CENTER_ALIGNMENT);
//            title.setFont(new Font("SansSerif", Font.BOLD, 25));

            JTextField emailField = new JTextField();
            emailField.putClientProperty("JTextField.placeholderText", "Email");
            emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

            JPasswordField passwordField = new JPasswordField();
            passwordField.putClientProperty("JTextField.placeholderText", "Password");
            passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

            JButton loginButton = new JButton("Login");
            JButton createAccButton = new JButton("Create Account");

            loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            createAccButton.setAlignmentX(Component.CENTER_ALIGNMENT);

            loginButton.addActionListener((ActionEvent e) -> {
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

                    ArrayList<User> users = new ArrayList<>();

                    while (rs.next()) {
                        int ID = rs.getInt("ID");
                        String firstName = rs.getString("firstName");
                        String lastName = rs.getString("lastName");
                        String em = rs.getString("email");
                        String phoneNumber = rs.getString("phoneNumber");
                        String passwd = rs.getString("password");
                        int role = rs.getInt("role");

                        User user = switch (role) {
                            case 0 -> new Client();
                            case 1 -> new Admin();
                            default -> null;
                        };

                        if (user != null) {
                            user.setID(ID);
                            user.setFirstName(firstName);
                            user.setLastName(lastName);
                            user.setEmail(em);
                            user.setPhoneNumber(phoneNumber);
                            user.setPassword(passwd);
                            users.add(user);
                        }
                    }

                    if (users.isEmpty()) {
                        JOptionPane.showMessageDialog(frame, "Email or password is incorrect.");
                    } else {
                        User user = users.get(0);
                        //JOptionPane.showMessageDialog(frame, "Welcome " + user.getFirstName() + "!");
                        //frame.dispose();

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
            });

            createAccButton.addActionListener((ActionEvent e) -> {
                new AddNewAccountGUI(database, 0); // 0 = client account
                //frame.dispose();
            });

            panel.add(logoLabel);
            panel.add(Box.createVerticalStrut(10));
            //panel.add(title);
            panel.add(Box.createVerticalStrut(20));
            panel.add(emailField);
            panel.add(Box.createVerticalStrut(10));
            panel.add(passwordField);
            panel.add(Box.createVerticalStrut(20));
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
}
