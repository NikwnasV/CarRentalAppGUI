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

/**
 *
 * @author nikwn
 */
public class AdminStatsGUI extends JFrame {

    private final JLabel totalRentsLabel = new JLabel("...");
    private final JLabel revenueLabel = new JLabel("...");
    private final JLabel activeRentsLabel = new JLabel("...");
    private final JLabel delayedRentsLabel = new JLabel("...");

    private final DefaultTableModel topCarsModel = new DefaultTableModel(
            new String[]{"Rank", "Brand", "Model", "Times Rented"}, 0
    );

    public AdminStatsGUI(Database database) {
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

        JTable table = new JTable(topCarsModel);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));

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
        panel.setBackground(new Color(198,182,207));
        panel.setBorder(BorderFactory.createLineBorder(new Color(73, 36, 109), 2));
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

            // Total revenue
            ResultSet rs2 = conn.createStatement().executeQuery("SELECT SUM(total) FROM rents WHERE status > 0");
            if (rs2.next()) revenueLabel.setText(String.format("%.2f", rs2.getFloat(1)));

            // Active rents
            ResultSet rs3 = conn.createStatement().executeQuery("SELECT COUNT(*) FROM rents WHERE status = 0");
            if (rs3.next()) activeRentsLabel.setText(String.valueOf(rs3.getInt(1)));

            // Delayed rents
            ResultSet rs4 = conn.createStatement().executeQuery("SELECT COUNT(*) FROM rents WHERE status = 2");
            if (rs4.next()) delayedRentsLabel.setText(String.valueOf(rs4.getInt(1)));

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
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
