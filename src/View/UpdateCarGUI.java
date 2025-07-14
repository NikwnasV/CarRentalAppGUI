/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package View;

import DatabaseConfig.Database;
import Model.Car;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.HashMap;

/**
 *
 * @author nikwn
 */

public class UpdateCarGUI extends JFrame {
    private final Database database;
    private final JComboBox<String> carIdCombo;
    private final HashMap<Integer, Car> carsMap = new HashMap<>();

    private final JTextField brandField = new JTextField();
    private final JTextField modelField = new JTextField();
    private final JTextField yearField = new JTextField();
    private final JComboBox<String> fuelBox = new JComboBox<>(new String[]{"Petrol", "Diesel", "Electric", "Hybrid"});
    private final JComboBox<String> gearboxBox = new JComboBox<>(new String[]{"Manual", "Automatic"});
    private final JTextField engineccField = new JTextField();
    private final JTextField hpField = new JTextField();
    private final JTextField consumptionField = new JTextField();
    private final JTextField priceField = new JTextField();
    private final JTextField availableField = new JTextField();

    public UpdateCarGUI(Database database) {
        this.database = database;

        setTitle("Update Car");
        setSize(500, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel form = new JPanel();
        form.setLayout(new GridLayout(12, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        carIdCombo = new JComboBox<>();
        loadCars();

        carIdCombo.addActionListener((ActionEvent e) -> fillFormFromSelectedCar());

        form.add(new JLabel("Select Car ID:"));
        form.add(carIdCombo);
        form.add(new JLabel("Brand:")); form.add(brandField);
        form.add(new JLabel("Model:")); form.add(modelField);
        form.add(new JLabel("Year:")); form.add(yearField);
        form.add(new JLabel("Fuel Type:")); form.add(fuelBox);
        form.add(new JLabel("Gearbox:")); form.add(gearboxBox);
        form.add(new JLabel("Engine CC:")); form.add(engineccField);
        form.add(new JLabel("Horsepower:")); form.add(hpField);
        form.add(new JLabel("Consumption (l/100km):")); form.add(consumptionField);
        form.add(new JLabel("Price per Day (€):")); form.add(priceField);
        form.add(new JLabel("Available Units:")); form.add(availableField);

        JButton updateBtn = new JButton("Update Car");
        updateBtn.addActionListener(e -> updateCarInDB());

        add(form, BorderLayout.CENTER);
        add(updateBtn, BorderLayout.SOUTH);

        setVisible(true);
        form.getRootPane().setDefaultButton(updateBtn);

    }

    private void loadCars() {
        try (Connection connection = database.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM cars");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Car car = new Car();
                int id = rs.getInt("ID");
                car.setID(id);
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

                carsMap.put(id, car);
                carIdCombo.addItem(String.valueOf(id));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading car list.");
        }
    }

    private void fillFormFromSelectedCar() {
        int id = Integer.parseInt((String) carIdCombo.getSelectedItem());
        Car car = carsMap.get(id);

        brandField.setText(car.getBrand());
        modelField.setText(car.getModel());
        yearField.setText(String.valueOf(car.getYear()));
        fuelBox.setSelectedItem(car.getFuel());
        gearboxBox.setSelectedItem(car.getGearbox());
        engineccField.setText(String.valueOf(car.getEnginecc()));
        hpField.setText(String.valueOf(car.getHorsepower()));
        consumptionField.setText(String.valueOf(car.getConsumption()));
        priceField.setText(String.valueOf(car.getPrice()));
        availableField.setText(String.valueOf(car.getAvailable()));
    }

    private void updateCarInDB() {
        int id = Integer.parseInt((String) carIdCombo.getSelectedItem());

        try (Connection conn = database.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("UPDATE cars SET brand = ?, model = ?, year = ?, fuel = ?, gearbox = ?, enginecc = ?, horsepower = ?, consumption = ?, price = ?, available = ? WHERE ID = ?");
            ps.setString(1, brandField.getText());
            ps.setString(2, modelField.getText());
            ps.setInt(3, Integer.parseInt(yearField.getText()));
            ps.setString(4, (String) fuelBox.getSelectedItem());
            ps.setString(5, (String) gearboxBox.getSelectedItem());
            ps.setInt(6, Integer.parseInt(engineccField.getText()));
            ps.setInt(7, Integer.parseInt(hpField.getText()));
            ps.setFloat(8, Float.parseFloat(consumptionField.getText()));
            ps.setFloat(9, Float.parseFloat(priceField.getText()));
            ps.setInt(10, Integer.parseInt(availableField.getText()));
            ps.setInt(11, id);

            int result = ps.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Car updated successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update car.");
            }
            dispose();
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database: Error updating car.");
        }
    }
}