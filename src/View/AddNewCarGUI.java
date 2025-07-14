/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package View;

/**
 *
 * @author nikwn
 */

import Controller.AddNewCar;
import DatabaseConfig.Database;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddNewCarGUI extends JFrame {

    public AddNewCarGUI(Database database) {
        setTitle("Add New Car");
        setSize(400, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(12, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField brandField = new JTextField();
        JTextField modelField = new JTextField();
        JTextField yearField = new JTextField();
        JComboBox<String> fuelBox = new JComboBox<>(new String[]{"Petrol", "Diesel", "Electric", "Hybrid"});
        JComboBox<String> gearboxBox = new JComboBox<>(new String[]{"Manual", "Automatic"});
        JTextField engineccField = new JTextField();
        JTextField horsepowerField = new JTextField();
        JTextField consumptionField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField availableField = new JTextField();

        JButton submitBtn = new JButton("Add Car");

        panel.add(new JLabel("Brand:")); panel.add(brandField);
        panel.add(new JLabel("Model:")); panel.add(modelField);
        panel.add(new JLabel("Year:")); panel.add(yearField);
        panel.add(new JLabel("Fuel Type:")); panel.add(fuelBox);
        panel.add(new JLabel("Gearbox:")); panel.add(gearboxBox);
        panel.add(new JLabel("Engine CC:")); panel.add(engineccField);
        panel.add(new JLabel("Horsepower:")); panel.add(horsepowerField);
        panel.add(new JLabel("Consumption:")); panel.add(consumptionField);
        panel.add(new JLabel("Daily Price:")); panel.add(priceField);
        panel.add(new JLabel("Available Units:")); panel.add(availableField);

        panel.add(new JLabel());
        panel.add(submitBtn);

        add(panel);
        setVisible(true);

        submitBtn.addActionListener(e -> {
            try {
                String brand = brandField.getText().trim();
                String model = modelField.getText().trim();
                int year = Integer.parseInt(yearField.getText().trim());
                String fuel = fuelBox.getSelectedItem().toString();
                String gearbox = gearboxBox.getSelectedItem().toString();
                int enginecc = Integer.parseInt(engineccField.getText().trim());
                int horsepower = Integer.parseInt(horsepowerField.getText().trim());
                float consumption = Float.parseFloat(consumptionField.getText().trim());
                float price = Float.parseFloat(priceField.getText().trim());
                int available = Integer.parseInt(availableField.getText().trim());

                AddNewCar logic = new AddNewCar();
                logic.insert(database, brand, model, year, fuel, gearbox, enginecc, horsepower, consumption, price, available);

                JOptionPane.showMessageDialog(this, "Car added successfully!");
                dispose();

            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error occurred.", "DB Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
    }
}

