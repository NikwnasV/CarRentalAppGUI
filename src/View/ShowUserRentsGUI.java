/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package View;

import DatabaseConfig.Database;
import Model.Car;
import Model.Rent;
import Model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 *
 * @author nikwn
 */
public class ShowUserRentsGUI extends JFrame {

    private final Database database; // Added database field for consistency and proper closing
    private final User currentUser;  // Store the user whose rents are being displayed
    private final DefaultTableModel model;

    public ShowUserRentsGUI(Database database, User user) {
        this.database = database;
        this.currentUser = user; // Assign the provided user

        // Apply UIManager properties for table selection, mirroring ShowRentsGUI
        UIManager.put("Table.selectionBackground", new Color(0xE1BEE7)); // optional: custom selection color
        UIManager.put("Table.selectionForeground", Color.BLACK);          // text when selected

        setTitle("My Rentals - " + user.getFirstName() + " " + user.getLastName()); // Dynamic title
        setSize(850, 450); // Slightly adjusted size for better display
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        model = new DefaultTableModel(
                new String[]{"Rent ID", "Car", "Price/Day (€)", "Start Date", "Days", "Total (€)", "Status"}, 0
        ) {
            // Make all cells non-editable
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        styleTable(table); // Apply styling to the table

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        loadUserRents(); // Call the loading method
        setVisible(true);
    }

    // Extracted table styling into a private method for reusability and clarity
    private void styleTable(JTable table) {
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setRowHeight(28);
        table.setAutoCreateRowSorter(true); // Allow sorting

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 15));
        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);

        // Center align text in all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Set preferred column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(60);  // Rent ID
        table.getColumnModel().getColumn(1).setPreferredWidth(180); // Car
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Price/Day (€)
        table.getColumnModel().getColumn(3).setPreferredWidth(140); // Start Date
        table.getColumnModel().getColumn(4).setPreferredWidth(60);  // Days
        table.getColumnModel().getColumn(5).setPreferredWidth(80);  // Total (€)
        table.getColumnModel().getColumn(6).setPreferredWidth(100); // Status

        table.setSelectionBackground(new Color(225, 190, 231, 128)); // Translucent selection
        table.setFocusable(false); // Make table not focusable (optional, but common for display tables)
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(false);
    }

    private void loadUserRents() {
        ArrayList<Rent> rents = new ArrayList<>();
        model.setRowCount(0); // Clear existing rows before loading

        try (Connection connection = database.getConnection();
             PreparedStatement pr = connection.prepareStatement("SELECT * FROM rents WHERE client = ?")) {
            pr.setInt(1, currentUser.getID()); // Filter by the current user's ID
            
            try (ResultSet rs = pr.executeQuery()) {
                while (rs.next()) {
                    Rent rent = new Rent();
                    rent.setID(rs.getInt("ID"));
                    rent.setUser(currentUser); // Already have the user object
                    rent.setDateTime(rs.getString("dateTime"));
                    rent.setDays(rs.getInt("days"));
                    rent.setTotal(rs.getFloat("total"));
                    rent.setStatus(rs.getInt("status"));

                    int carID = rs.getInt("car");
                    Car car = getCarByID(carID, connection); // Fetch car details
                    rent.setCar(car);

                    // --- Check for delay and update status if necessary ---
                    LocalDateTime rentDate = rent.getRawDateTime();
                    LocalDateTime returnDate = rentDate.plusDays(rent.getDays());

                    if (rent.getStatus() == 0 && LocalDateTime.now().isAfter(returnDate)) {
                        rent.setStatus(2); // Set to Delayed
                        // Update status in the database
                        try (PreparedStatement update = connection.prepareStatement("UPDATE rents SET status = 2 WHERE ID = ?")) {
                            update.setInt(1, rent.getID());
                            update.executeUpdate();
                        }
                    }

                    rents.add(rent);
                }
            }

            if (rents.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No rentals found for your account.", "Info", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                return;
            }

            // --- Fill Table with retrieved and processed rents ---
            for (Rent r : rents) {
                Car c = r.getCar();
                String carText = c != null ? c.getBrand() + " " + c.getModel() + " (" + c.getYear() + ")" : "Unknown Car";

                String statusStr = switch (r.getStatus()) {
                    case 0 -> "Running";
                    case 1 -> "Returned";
                    case 2 -> "Delayed";
                    default -> "Unknown";
                };

                model.addRow(new Object[]{
                        r.getID(),
                        carText,
                        c != null ? String.format("%.2f", c.getPrice()) : "-", // Display car's price per day
                        r.getDateTime(),
                        r.getDays(),
                        String.format("%.2f", r.getTotal()),
                        statusStr
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading your rentals: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Helper method to get Car details by ID
    private Car getCarByID(int carID, Connection connection) throws SQLException {
        try (PreparedStatement carStmt = connection.prepareStatement("SELECT * FROM cars WHERE ID = ?")) {
            carStmt.setInt(1, carID);
            try (ResultSet carRs = carStmt.executeQuery()) {
                if (carRs.next()) {
                    Car car = new Car();
                    car.setID(carRs.getInt("ID"));
                    car.setBrand(carRs.getString("brand"));
                    car.setModel(carRs.getString("model"));
                    car.setYear(carRs.getInt("year"));
                    car.setFuel(carRs.getString("fuel"));
                    car.setGearbox(carRs.getString("gearbox"));
                    car.setEnginecc(carRs.getInt("enginecc"));
                    car.setHorsepower(carRs.getInt("horsepower"));
                    car.setConsumption(carRs.getFloat("consumption"));
                    car.setPrice(carRs.getFloat("price"));
                    car.setAvailable(carRs.getInt("available"));
                    return car;
                }
            }
        }
        return null;
    }
}