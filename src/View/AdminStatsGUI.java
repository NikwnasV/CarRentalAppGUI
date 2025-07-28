/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package View;

import DatabaseConfig.Database;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

// Import FlatLaf (Make sure you've added the FlatLaf JAR to your project's classpath)
import com.formdev.flatlaf.FlatLightLaf; // Keep FlatLightLaf as per original

/**
 *
 * @author nikwn
 */
public class AdminStatsGUI extends JFrame {

    private final JLabel totalRentsLabel = new JLabel("...");
    private final JLabel revenueLabel = new JLabel("...");
    private final JLabel activeRentsLabel = new JLabel("...");
    private final JLabel delayedRentsLabel = new JLabel("...");

    private final DefaultTableModel topCarsModel; // Declared as final here

    public AdminStatsGUI(Database database) {
        // --- 1. Set a modern Look and Feel (FlatLaf for simple beauty) ---
        // Keeping FlatLightLaf.setup() as per your original code
        try {
            FlatLightLaf.setup();
        } catch (Exception e) {
            System.err.println("Failed to set FlatLaf Look and Feel. Using default or Nimbus.");
            try {
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

        setTitle("Admin Statistics Dashboard");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // --- STATS PANEL ---
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        statsPanel.add(createStatCard("Total Rents", totalRentsLabel));
        statsPanel.add(createStatCard("Total Revenue (€)", revenueLabel));
        statsPanel.add(createStatCard("Active Rents", activeRentsLabel));
        statsPanel.add(createStatCard("Delayed Rents", delayedRentsLabel));

        // --- TABLE PANEL ---
        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
        tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        JLabel tableTitle = new JLabel("Top 5 Most Rented Cars");
        tableTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        tableTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Initialize topCarsModel as non-editable
        topCarsModel = new DefaultTableModel(
                new String[]{"Rank", "Brand", "Model", "Times Rented"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };

        JTable table = new JTable(topCarsModel);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(235, 235, 235)); // Original header background
        table.getTableHeader().setForeground(new Color(70, 70, 70)); // Original header foreground
        table.getTableHeader().setReorderingAllowed(false); // Keep this for user experience

        table.setGridColor(new Color(230, 230, 230)); // Original grid color
        table.setShowVerticalLines(false); // Original vertical lines setting
        table.setRowSelectionAllowed(true); // Original row selection setting
        table.setColumnSelectionAllowed(false); // Original column selection setting
        
        // Original Cell Renderers (if any were present or desired)
        // No explicit cell renderers were in your original code, so none added here for consistency.

        JScrollPane tableScroll = new JScrollPane(table);

        tablePanel.add(tableTitle, BorderLayout.NORTH);
        tablePanel.add(tableScroll, BorderLayout.CENTER);

        add(statsPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);

        loadStats(database);
        setVisible(true);
    }

    private JPanel createStatCard(String title, JLabel valueLabel) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(198,182,207)); // Original background color
        panel.setBorder(BorderFactory.createLineBorder(new Color(73, 36, 109), 2)); // Original border color
        panel.setPreferredSize(new Dimension(200, 100));

        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        valueLabel.setHorizontalAlignment(JLabel.CENTER);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);

        return panel;
    }

    private void loadStats(Database db) {
        try (Connection conn = db.getConnection()) {
            // Total rents
            ResultSet rs1 = conn.createStatement().executeQuery("SELECT COUNT(*) FROM rents");
            if (rs1.next()) totalRentsLabel.setText(String.valueOf(rs1.getInt(1)));
            rs1.close(); // Close ResultSet

            // Total revenue
            ResultSet rs2 = conn.createStatement().executeQuery("SELECT SUM(total) FROM rents WHERE status > 0");
            if (rs2.next()) revenueLabel.setText(String.format("%.2f", rs2.getFloat(1)));
            rs2.close(); // Close ResultSet

            // Active rents
            ResultSet rs3 = conn.createStatement().executeQuery("SELECT COUNT(*) FROM rents WHERE status = 0");
            if (rs3.next()) activeRentsLabel.setText(String.valueOf(rs3.getInt(1)));
            rs3.close(); // Close ResultSet

            // Delayed rents
            ResultSet rs4 = conn.createStatement().executeQuery("SELECT COUNT(*) FROM rents WHERE status = 2");
            if (rs4.next()) delayedRentsLabel.setText(String.valueOf(rs4.getInt(1)));
            rs4.close(); // Close ResultSet

            // Top 5 cars
            PreparedStatement topCarsStmt = conn.prepareStatement("""
                SELECT car, COUNT(*) as rent_count
                FROM rents
                GROUP BY car
                ORDER BY rent_count DESC
                LIMIT 5
            """);
            ResultSet rs5 = topCarsStmt.executeQuery();

            topCarsModel.setRowCount(0); // Clear previous rows
            int rank = 1;

            while (rs5.next()) {
                int carID = rs5.getInt("car");
                int count = rs5.getInt("rent_count");

                PreparedStatement carInfoStmt = conn.prepareStatement("SELECT brand, model FROM cars WHERE ID = ?");
                carInfoStmt.setInt(1, carID);
                ResultSet carRS = carInfoStmt.executeQuery();

                if (carRS.next()) {
                    String brand = carRS.getString("brand");
                    String model = carRS.getString("model");
                    topCarsModel.addRow(new Object[]{rank++, brand, model, count});
                }
                carRS.close(); // Close ResultSet
                carInfoStmt.close(); // Close PreparedStatement
            }
            rs5.close(); // Close ResultSet
            topCarsStmt.close(); // Close PreparedStatement

        } catch (SQLException e) {
            e.printStackTrace();
            // Original code did not show a JOptionPane here, so keeping it as is.
        }
    }
}