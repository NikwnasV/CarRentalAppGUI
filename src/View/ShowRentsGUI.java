/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package View;

import DatabaseConfig.Database;
import Model.Car;
import Model.Client;
import Model.Rent;
import Model.User;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 *
 * @author nikwn
 */
public class ShowRentsGUI extends JFrame {
    private final Database database;
    private final DefaultTableModel model;

    public ShowRentsGUI(Database database) {
        this.database = database;

        //UIManager.put("Table.focusCellHighlightBorder", BorderFactory.createEmptyBorder());
        //UIManager.put("Table.focusCellBackground", UIManager.getColor("Table.background"));
        UIManager.put("Table.selectionBackground", new Color(0xE1BEE7)); // optional: custom selection color
        UIManager.put("Table.selectionForeground", Color.BLACK);         // text when selected

        setTitle("All Rentals");
        setSize(1000, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        model = new DefaultTableModel(new String[]{
                "Rent ID", "Client", "Car", "Start Date", "Days", "Total (€)", "Status"
        }, 0) {
            // Make all cells non-editable
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        loadRents();
        setVisible(true);
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setRowHeight(28);
        table.setAutoCreateRowSorter(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 15));
        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Optional: Set column widths for better readability
        table.getColumnModel().getColumn(0).setPreferredWidth(60);  // Rent ID
        table.getColumnModel().getColumn(1).setPreferredWidth(160); // Client
        table.getColumnModel().getColumn(2).setPreferredWidth(180); // Car
        table.getColumnModel().getColumn(3).setPreferredWidth(140); // Start Date
        table.getColumnModel().getColumn(4).setPreferredWidth(60);  // Days
        table.getColumnModel().getColumn(5).setPreferredWidth(80);  // Total
        table.getColumnModel().getColumn(6).setPreferredWidth(100); // Status

        table.setSelectionBackground(new Color(225, 190, 231, 128)); // with alpha channel (translucent)
        table.setFocusable(false);
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(false);
    }

    private void loadRents() {
        ArrayList<Rent> rents = new ArrayList<>();

        try (Connection connection = database.getConnection();
             PreparedStatement pr = connection.prepareStatement("SELECT * FROM rents");
             ResultSet rs = pr.executeQuery()) {

            while (rs.next()) {
                Rent rent = new Rent();
                rent.setID(rs.getInt("ID"));
                int carID = rs.getInt("car");
                int clientID = rs.getInt("client");

                rent.setCar(getCarByID(carID, connection));
                rent.setUser(getUserByID(clientID, connection));
                rent.setDateTime(rs.getString("dateTime"));
                rent.setDays(rs.getInt("days"));
                rent.setTotal(rs.getFloat("total"));
                rent.setStatus(rs.getInt("status"));

                // Delay logic
                LocalDateTime rentDate = rent.getRawDateTime();
                LocalDateTime dueDate = rentDate.plusDays(rent.getDays());
                if (rent.getStatus() == 0 && LocalDateTime.now().isAfter(dueDate)) {
                    rent.setStatus(2);
                    PreparedStatement update = connection.prepareStatement("UPDATE rents SET status = 2 WHERE ID = ?");
                    update.setInt(1, rent.getID());
                    update.executeUpdate();
                }

                rents.add(rent);
            }

            if (rents.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No rentals found.", "Info", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                return;
            }

            for (Rent r : rents) {
                String client = (r.getUser() != null)
                        ? r.getUser().getFirstName() + " " + r.getUser().getLastName() + " (ID: " + r.getUser().getID() + ")"
                        : "Unknown";

                Car c = r.getCar();
                String carStr = (c != null)
                        ? c.getBrand() + " " + c.getModel() + " (" + c.getYear() + ")"
                        : "Not Found";

                String statusStr = switch (r.getStatus()) {
                    case 0 -> "Running";
                    case 1 -> "Returned";
                    case 2 -> "Delayed";
                    default -> "Unknown";
                };

                model.addRow(new Object[]{
                        r.getID(),
                        client,
                        carStr,
                        r.getDateTime(),
                        r.getDays(),
                        String.format("%.2f", r.getTotal()),
                        statusStr
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load rentals.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Car getCarByID(int carID, Connection connection) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM cars WHERE ID = ?");
        stmt.setInt(1, carID);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            Car car = new Car();
            car.setID(rs.getInt("ID"));
            car.setBrand(rs.getString("brand"));
            car.setModel(rs.getString("model"));
            car.setYear(rs.getInt("year"));
            car.setFuel(rs.getString("fuel"));
            car.setGearbox(rs.getString("gearbox"));
            car.setEnginecc(rs.getInt("enginecc"));
            car.setHorsepower(rs.getInt("horsepower"));
            car.setConsumption(rs.getFloat("consumption"));
            car.setPrice(rs.getFloat("price"));
            car.setAvailable(rs.getInt("available"));
            return car;
        }
        return null;
    }

    private User getUserByID(int userID, Connection connection) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users WHERE ID = ?");
        stmt.setInt(1, userID);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            User user = new Client();
            user.setID(rs.getInt("ID"));
            user.setFirstName(rs.getString("firstName"));
            user.setLastName(rs.getString("lastName"));
            user.setEmail(rs.getString("email"));
            user.setPhoneNumber(rs.getString("phoneNumber"));
            return user;
        }
        return null;
    }
}