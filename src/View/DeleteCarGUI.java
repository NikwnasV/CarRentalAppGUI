/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package View;

import DatabaseConfig.Database;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author nikwn
 */



public class DeleteCarGUI extends JFrame {

    private final Database database;
    private final JComboBox<String> carDropdown;
    private final Map<String, Integer> carIdMap = new HashMap<>();

    public DeleteCarGUI(Database db) {
        this.database = db;

        setTitle("Delete Car");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel label = new JLabel("Select a car to delete:", SwingConstants.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        add(label, BorderLayout.NORTH);

        carDropdown = new JComboBox<>();
        loadCarsIntoDropdown();

        JPanel centerPanel = new JPanel();
        centerPanel.add(carDropdown);
        add(centerPanel, BorderLayout.CENTER);

        JButton deleteBtn = new JButton("Delete");
        JButton cancelBtn = new JButton("Cancel");

        deleteBtn.addActionListener(e -> deleteSelectedCar());
        cancelBtn.addActionListener(e -> dispose());

        JPanel btnPanel = new JPanel();
        btnPanel.add(deleteBtn);
        btnPanel.add(cancelBtn);

        add(btnPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void loadCarsIntoDropdown() {
        try {
            Connection conn = database.getConnection();
            String sql = "SELECT ID, brand, model FROM cars";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                boolean hasResults = false;
                while (rs.next()) {
                    hasResults = true;
                    int id = rs.getInt("ID");
                    String label = rs.getString("brand") + " " + rs.getString("model") + " (ID: " + id + ")";
                    carDropdown.addItem(label);
                    carIdMap.put(label, id);
                }

                if (!hasResults) {
                    JOptionPane.showMessageDialog(this, "No cars available for deletion.");
                    dispose();
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading cars from database.");
            dispose();
        }
    }

    private void deleteSelectedCar() {
        String selectedItem = (String) carDropdown.getSelectedItem();
        if (selectedItem == null) return;

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this car?\n" + selectedItem,
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        int carId = carIdMap.get(selectedItem);

        try {
            Connection conn = database.getConnection();
            String sql = "DELETE FROM cars WHERE ID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, carId);
                int rows = stmt.executeUpdate();

                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Car deleted successfully.");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete car.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error during deletion.");
        }
    }
}