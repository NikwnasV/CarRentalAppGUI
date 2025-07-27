/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package View;

import DatabaseConfig.Database;
import Utils.ImageUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;

/**
 * 
 *@author: nikwn
 */

public class AddNewCarGUI extends JFrame {

    private File selectedImageFile = null;

    public AddNewCarGUI(Database database) {
        setTitle("Add New Car");
        setSize(400, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(13, 2, 10, 10));
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
        //JTextField availableField = new JTextField();

        JLabel imageStatusLabel = new JLabel("No image selected");
        JButton chooseImageBtn = new JButton("Choose Image");

        chooseImageBtn.addActionListener(e -> {
            File file = ImageUtil.chooseImage(this);
            if (file != null) {
                selectedImageFile = file;
                imageStatusLabel.setText("Selected: " + file.getName());
            }
        });

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
        //panel.add(new JLabel("Available Units:")); panel.add(availableField);

        panel.add(chooseImageBtn); panel.add(imageStatusLabel);
        panel.add(new JLabel()); panel.add(submitBtn);

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
                int available = 1;

                if (selectedImageFile == null) {
                    JOptionPane.showMessageDialog(this, "Please choose an image.", "Image Required", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Connection conn = database.getConnection();
                int newID = 0;

                try (PreparedStatement ps = conn.prepareStatement("SELECT MAX(ID) AS lastID FROM cars");
                     ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getObject("lastID") != null) {
                        newID = rs.getInt("lastID") + 1;
                    }
                }

                // Copy selected image to src/resources/{carID}.png
//                String targetPath = "src/resources/" + newID + ".png";
//                File targetFile = new File(targetPath);
//                Files.copy(selectedImageFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                String img_path = ImageUtil.saveImage(selectedImageFile, newID);

                // Save car with image path (just filename, for later GUI loading)
                String insertSQL = "INSERT INTO cars (ID, brand, model, year, fuel, gearbox, enginecc, horsepower, consumption, price, available, image_path) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement insert = conn.prepareStatement(insertSQL)) {
                    insert.setInt(1, newID);
                    insert.setString(2, brand);
                    insert.setString(3, model);
                    insert.setInt(4, year);
                    insert.setString(5, fuel);
                    insert.setString(6, gearbox);
                    insert.setInt(7, enginecc);
                    insert.setInt(8, horsepower);
                    insert.setFloat(9, consumption);
                    insert.setFloat(10, price);
                    insert.setInt(11, available);
                    insert.setString(12, img_path);
                    insert.executeUpdate();
                }

                JOptionPane.showMessageDialog(this, "Car added successfully with image!");
                dispose();

            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database error occurred.", "DB Error", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ioex) {
                ioex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to save image.", "Image Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}