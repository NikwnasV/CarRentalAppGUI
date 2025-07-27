/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package View;

import DatabaseConfig.Database;
import Model.User;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.HashMap;

/**
 *
 * @author nikwn
 */
public class ReturnCarGUI extends JFrame {
    private final Database database;
    private final User user;

    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final HashMap<String, Integer> rentIdMap = new HashMap<>();
    private final JList<String> rentList = new JList<>(listModel);

    public ReturnCarGUI(Database database, User user) {
        this.database = database;
        this.user = user;

        if (!loadRents()) {
            JOptionPane.showMessageDialog(null, "You have no active rentals.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return; // Don't show window
        }

        setTitle("Return a Rented Car");
        setSize(450, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel instruction = new JLabel("Select a rent to return:");
        instruction.setHorizontalAlignment(JLabel.CENTER);
        add(instruction, BorderLayout.NORTH);

        rentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(rentList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        add(scrollPane, BorderLayout.CENTER);

        JButton returnButton = new JButton("Return Car");
        returnButton.addActionListener(e -> handleReturn());
        add(returnButton, BorderLayout.SOUTH);

        setVisible(true);
    }

    private boolean loadRents() {
        try (Connection conn = database.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT r.ID, r.car, c.brand, c.model " +
                "FROM rents r JOIN cars c ON r.car = c.ID " +
                "WHERE r.client = ? AND r.status = 0"
            );
            ps.setInt(1, user.getID());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int rentID = rs.getInt("ID");
                String label = "Rent #" + rentID + " - " + rs.getString("brand") + " " + rs.getString("model");
                listModel.addElement(label);
                rentIdMap.put(label, rentID);
            }

            return !listModel.isEmpty(); // true if we loaded any rents
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error while loading rentals.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void handleReturn() {
        String selected = rentList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a rent to return.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int rentID = rentIdMap.get(selected);

        try (Connection conn = database.getConnection()) {
            // Verify rent still belongs to user and is active
            PreparedStatement verify = conn.prepareStatement(
                "SELECT car FROM rents WHERE ID = ? AND client = ? AND status = 0"
            );
            verify.setInt(1, rentID);
            verify.setInt(2, user.getID());
            ResultSet rs = verify.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "This rent is invalid or already returned.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int carID = rs.getInt("car");

            // Mark rent as returned
            PreparedStatement updateRent = conn.prepareStatement("UPDATE rents SET status = 1 WHERE ID = ?");
            updateRent.setInt(1, rentID);
            int updated = updateRent.executeUpdate();

            if (updated > 0) {
                PreparedStatement updateCar = conn.prepareStatement("UPDATE cars SET available = available + 1 WHERE ID = ?");
                updateCar.setInt(1, carID);
                updateCar.executeUpdate();

                JOptionPane.showMessageDialog(this, "Car returned successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update rent status.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error during return process.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}