/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package View;

import DatabaseConfig.Database;
import Model.Car;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

/**
 *
 * @author nikwn
 */

public class ViewCarsGUI extends JFrame {

    private final Database database;
    private final JTable table;
    private final DefaultTableModel model;
    private final ArrayList<Car> cars = new ArrayList<>();

    public ViewCarsGUI(Database database) {
        this.database = database;

        setTitle("All Cars");
        setSize(950, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Non-editable model
        model = new DefaultTableModel(new Object[]{
                "ID", "Brand", "Model", "Year", "Fuel", "Gearbox",
                "Engine CC", "HP", "Consumption", "Price (€)", "Available"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // all cells non-editable
            }
        };

        table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true); // Optional: enables sorting
        JScrollPane scrollPane = new JScrollPane(table);

        add(scrollPane, BorderLayout.CENTER);

        loadCarsFromDatabase();
        populateTableFromList();

        setVisible(true);
    }

    private void loadCarsFromDatabase() {
        //cars.clear();
        try (Connection connection = database.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM cars");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
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
                cars.add(car);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching car data from database.");
        }
    }

    private void populateTableFromList() {
        model.setRowCount(0); // Clear table

        for (Car c : cars) {
            model.addRow(new Object[]{
                    c.getID(), c.getBrand(), c.getModel(), c.getYear(), c.getFuel(), c.getGearbox(),
                    c.getEnginecc(), c.getHorsepower(), c.getConsumption(), c.getPrice(),
                    (c.getAvailable() > 0 ? "Yes" : "No")
            });
        }
    }
}
