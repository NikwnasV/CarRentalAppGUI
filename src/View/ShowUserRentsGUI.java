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
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 *
 * @author nikwn
 */
public class ShowUserRentsGUI extends JFrame {

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Rent ID", "Car", "Price/Day (€)", "Start Date", "Days", "Total (€)", "Status"}, 0
    );

    public ShowUserRentsGUI(Database database, User user) {
        setTitle("My Rentals");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        loadUserRents(database, user);
        setVisible(true);
    }

    private void loadUserRents(Database db, User user) {
        ArrayList<Rent> rents = new ArrayList<>();

        try (Connection connection = db.getConnection()) {
            PreparedStatement pr = connection.prepareStatement("SELECT * FROM rents WHERE client = ?");
            pr.setInt(1, user.getID());
            ResultSet rs = pr.executeQuery();

            while (rs.next()) {
                Rent rent = new Rent();
                rent.setID(rs.getInt("ID"));
                rent.setUser(user);
                rent.setDateTime(rs.getString("dateTime"));
                rent.setDays(rs.getInt("days"));
                rent.setTotal(rs.getFloat("total"));
                rent.setStatus(rs.getInt("status"));

                int carID = rs.getInt("car");
                Car car = getCarByID(carID, connection);
                rent.setCar(car);

                // --- Check for delay ---
                LocalDateTime rentDate = rent.getRawDateTime();
                LocalDateTime returnDate = rentDate.plusDays(rent.getDays());

                if (rent.getStatus() == 0 && LocalDateTime.now().isAfter(returnDate)) {
                    rent.setStatus(2); // Delayed
                    PreparedStatement update = connection.prepareStatement("UPDATE rents SET status = 2 WHERE ID = ?");
                    update.setInt(1, rent.getID());
                    update.executeUpdate();
                }

                rents.add(rent);
            }

            if (rents.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No rentals found for your account.", "Info", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                return;
            }

            // --- Fill Table ---
            for (Rent r : rents) {
                Car c = r.getCar();
                String carText = c != null ? c.getBrand() + " " + c.getModel() + " (" + c.getYear() + ")" : "Unknown";

                String statusStr = switch (r.getStatus()) {
                    case 0 -> "Running";
                    case 1 -> "Returned";
                    case 2 -> "Delayed";
                    default -> "Unknown";
                };

                model.addRow(new Object[]{
                        r.getID(),
                        carText,
                        c != null ? String.format("%.2f", c.getPrice()) : "-",
                        r.getDateTime(),
                        r.getDays(),
                        String.format("%.2f", r.getTotal()),
                        statusStr
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading your rentals.");
        }
    }

    private Car getCarByID(int carID, Connection connection) throws SQLException {
        PreparedStatement carStmt = connection.prepareStatement("SELECT * FROM cars WHERE ID = ?");
        carStmt.setInt(1, carID);
        ResultSet carRs = carStmt.executeQuery();

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

        return null;
    }
}