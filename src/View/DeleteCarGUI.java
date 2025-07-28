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

// Import FlatLaf themes
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;

/**
 *
 * @author nikwn
 */

public class DeleteCarGUI extends JFrame {

    private final Database database;
    private final JComboBox<String> carDropdown;
    private final Map<String, Integer> carIdMap = new HashMap<>();

    // Define the PURPLE color consistent with CarRentalApp
    private static final Color PURPLE = new Color(0x9c84bc);

    public DeleteCarGUI(Database db) {
        this.database = db;

        // --- Apply global UI properties matching the CarRentalApp login theme ---
        // These settings MUST be applied BEFORE setting the Look and Feel
        UIManager.put("Button.arc", 20);
        UIManager.put("Component.arc", 20);
        UIManager.put("Component.focusColor", PURPLE);
        UIManager.put("Button.focusColor", new Color(0, 0, 0, 0)); // No focus indication for buttons
        UIManager.put("Button.default.focusColor", new Color(0, 0, 0, 0));
        UIManager.put("Button.hoverBackground", PURPLE);
        UIManager.put("Button.hoverForeground", Color.WHITE);
        UIManager.put("Button.default.background", PURPLE); // Default buttons will be purple
        UIManager.put("Button.default.foreground", Color.WHITE);
        UIManager.put("Button.default.borderColor", PURPLE);

        // --- Set the FlatLaf macOS Light theme ---
        try {
            UIManager.setLookAndFeel(new FlatMacLightLaf());
            FlatLaf.setUseNativeWindowDecorations(true); // For consistent window decorations
        } catch (Exception e) {
            System.err.println("Failed to set FlatLaf macOS Light theme. Using fallback.");
            try {
                // Fallback to Nimbus if FlatLaf is not available or fails to load
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception ex) {
                System.err.println("Could not set Nimbus Look and Feel: " + ex.getMessage());
            }
        }

        setTitle("Delete Car");
        // Adjusted size to accommodate longer dropdown items
        setSize(550, 250); 
        setMinimumSize(new Dimension(450, 200)); // Ensure it doesn't shrink too much
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel label = new JLabel("Select a car to delete:", SwingConstants.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        add(label, BorderLayout.NORTH);

        carDropdown = new JComboBox<>();
        carDropdown.setFont(new Font("SansSerif", Font.PLAIN, 14)); // Slightly larger font for readability
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
        carDropdown.removeAllItems(); // Clear existing items
        carIdMap.clear(); // Clear existing map
        try {
            Connection conn = database.getConnection();
            // Retrieve more car details for the dropdown
            String sql = "SELECT * FROM `cars` WHERE `available` = '1'";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                boolean hasResults = false;
                while (rs.next()) {
                    hasResults = true;
                    int id = rs.getInt("ID");
                    String brand = rs.getString("brand");
                    String model = rs.getString("model");
                    int year = rs.getInt("year");
                    String fuel = rs.getString("fuel");
                    float price = rs.getFloat("price");
                    
                    // Format the label to show more stats
                    String label = String.format("%s %s (%d, %s, %.2f€/day) - ID: %d", 
                                                brand, model, year, fuel, price, id);
                    carDropdown.addItem(label);
                    carIdMap.put(label, id);
                }

                if (!hasResults) {
                    JOptionPane.showMessageDialog(this, "No cars available for deletion.", "No Cars Found", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading cars from database.", "Database Error", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }

    private void deleteSelectedCar() {
        String selectedItem = (String) carDropdown.getSelectedItem();
        if (selectedItem == null || carIdMap.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a car to delete.", "No Car Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get the ID from the map, using the selected string
        Integer carId = carIdMap.get(selectedItem);
        if (carId == null) {
            JOptionPane.showMessageDialog(this, "Selected car ID not found. Please reload or select another car.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this car?\n" + selectedItem,
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE // Use WARNING_MESSAGE icon for deletion confirmation
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM cars WHERE ID = ?")) {
            stmt.setInt(1, carId);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Car deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Close window after successful deletion
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete car. It might no longer exist.", "Deletion Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error occurred during deletion.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}