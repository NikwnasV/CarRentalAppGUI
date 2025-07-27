/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package View;

import DatabaseConfig.Database;
import Model.Car;
import Utils.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
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
    private final JTextField imagePathField = new JTextField();
    private File selectedImageFile = null;

    public UpdateCarGUI(Database database) {
        this.database = database;

        setTitle("Update Car");
        setSize(500, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(14, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        carIdCombo = new JComboBox<>();
        loadCars();
        carIdCombo.addActionListener(this::fillFormFromSelectedCar);

        form.add(new JLabel("Select Car ID:")); form.add(carIdCombo);
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

        imagePathField.setEditable(false);
        JButton chooseImageButton = new JButton("Choose New Image");
        chooseImageButton.addActionListener(e -> chooseImage());

        form.add(new JLabel("Image Path:")); form.add(imagePathField);
        form.add(new JLabel()); form.add(chooseImageButton);

        JButton updateBtn = new JButton("Update Car");
        updateBtn.addActionListener(e -> updateCarInDB());

        add(form, BorderLayout.CENTER);
        add(updateBtn, BorderLayout.SOUTH);

        setVisible(true);
        form.getRootPane().setDefaultButton(updateBtn);
    }

    private void chooseImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose New Car Image");
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedImageFile = chooser.getSelectedFile();
            imagePathField.setText(selectedImageFile.getName());
        }
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
                car.setImgPath(rs.getString("image_path"));

                carsMap.put(id, car);
                carIdCombo.addItem(String.valueOf(id));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading car list.");
        }
    }

    private void fillFormFromSelectedCar(ActionEvent e) {
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
        imagePathField.setText(car.getImgPath());
    }

    private void updateCarInDB() {
        int id = Integer.parseInt((String) carIdCombo.getSelectedItem());

        try (Connection conn = database.getConnection()) {
            String imagePath = imagePathField.getText();

            // If a new image was selected, overwrite the old one and update the path
            if (selectedImageFile != null) {
                imagePath = ImageUtil.saveImage(selectedImageFile, id); // returns e.g. "7.png"
            }

            PreparedStatement ps = conn.prepareStatement(
                "UPDATE cars SET brand = ?, model = ?, year = ?, fuel = ?, gearbox = ?, enginecc = ?, " +
                "horsepower = ?, consumption = ?, price = ?, available = ?, image_path = ? WHERE ID = ?"
            );
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
            ps.setString(11, imagePath);
            ps.setInt(12, id);

            int result = ps.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Car updated successfully.");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update car.");
            }

        } catch (SQLException | IOException | NumberFormatException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating car.");
        }
    }
}
