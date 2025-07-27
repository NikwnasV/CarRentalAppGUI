/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package View;

import DatabaseConfig.Database;
import Model.Car;
import Model.Client;
import Model.Rent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.sql.*;
import java.util.HashMap;
        
/**
 *
 * @author nikwn
 */

public class ViewCarsGUI extends JFrame {
    private final Database database;
    //private final Client client;
    private final JComboBox<String> carDropdown;
    private final JTextField daysField;
    private final JLabel imageLabel;
    private final JTextArea carDetailsArea;
    private final HashMap<String, Car> carMap = new HashMap<>();

    public ViewCarsGUI(Database database) {
        this.database = database;
        //this.client = client;

        setTitle("Rent a Car");
        setSize(800, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
setLayout(new BorderLayout());

// --- TOP: Big Image ---
imageLabel = new JLabel();
imageLabel.setPreferredSize(new Dimension(640, 360));
imageLabel.setHorizontalAlignment(JLabel.CENTER);
add(imageLabel, BorderLayout.NORTH);


// --- CENTER: Car Info ---
carDetailsArea = new JTextArea();
carDetailsArea.setEditable(false);
carDetailsArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
carDetailsArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
add(new JScrollPane(carDetailsArea), BorderLayout.CENTER);

// --- SOUTH: Dropdown + Days + Button ---
JPanel bottomPanel = new JPanel();
bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

// Row 1: Car Dropdown
JPanel carRow = new JPanel(new BorderLayout(5, 5));
carDropdown = new JComboBox<>();
carDropdown.addActionListener(this::updateCarPreview);
carRow.add(new JLabel("Select Car:"), BorderLayout.WEST);
carRow.add(carDropdown, BorderLayout.CENTER);
bottomPanel.add(carRow);

// Row 2: Days input
JPanel daysRow = new JPanel(new BorderLayout(5, 5));
daysField = new JTextField();
daysRow.add(new JLabel("Number of Days:"), BorderLayout.WEST);
daysRow.add(daysField, BorderLayout.CENTER);
bottomPanel.add(Box.createVerticalStrut(8));
bottomPanel.add(daysRow);

// Row 3: Rent button
JButton rentButton = new JButton("Confirm Rent");
//rentButton.addActionListener(this::handleRent);
JPanel buttonRow = new JPanel();
buttonRow.add(rentButton);
bottomPanel.add(Box.createVerticalStrut(10));
bottomPanel.add(buttonRow);

add(bottomPanel, BorderLayout.SOUTH);


        loadCars();
        setVisible(true);
    }

    private void loadCars() {
        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM cars WHERE 1");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Car car = new Car();
                car.setID(rs.getInt("ID"));
                car.setBrand(rs.getString("Brand"));
                car.setModel(rs.getString("Model"));
                car.setYear(rs.getInt("Year"));
                car.setFuel(rs.getString("Fuel"));
                car.setGearbox(rs.getString("Gearbox"));
                car.setEnginecc(rs.getInt("EngineCC"));
                car.setHorsepower(rs.getInt("Horsepower"));
                car.setConsumption(rs.getFloat("Consumption"));
                car.setPrice(rs.getFloat("Price"));
                car.setAvailable(rs.getInt("Available"));
                car.setImgPath(rs.getString("image_path"));

                String key = car.getID() + " - " + car.getBrand() + " " + car.getModel();
                carDropdown.addItem(key);
                carMap.put(key, car);
            }

            if (carDropdown.getItemCount() > 0)
                updateCarPreview(null);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load cars.");
        }
    }

    private void updateCarPreview(ActionEvent e) {
        String selectedKey = (String) carDropdown.getSelectedItem();
        Car car = carMap.get(selectedKey);
        if (car == null) return;

        try {
            URL imageURL = getClass().getClassLoader().getResource("resources/" + car.getImgPath());
            if (imageURL != null) {
                ImageIcon icon = new ImageIcon(imageURL);
                Image scaled = icon.getImage().getScaledInstance(640, 360, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaled));
            } else {
                imageLabel.setIcon(null);
                System.err.println("Image not found: " + car.getImgPath());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            imageLabel.setIcon(null);
        }

        carDetailsArea.setText(String.format(
                "Brand: %s\nModel: %s\nYear: %d\nFuel: %s\nGearbox: %s\nEngine CC: %d\nHorsepower: %d\nConsumption: %.1f\nPrice/Day: %.2f€\nAvailable: %d",
                car.getBrand(), car.getModel(), car.getYear(), car.getFuel(), car.getGearbox(),
                car.getEnginecc(), car.getHorsepower(), car.getConsumption(), car.getPrice(), car.getAvailable()
        ));
    }

//    private void handleRent(ActionEvent e) {
//        String selectedKey = (String) carDropdown.getSelectedItem();
//        Car car = carMap.get(selectedKey);
//
//        if (car == null) {
//            JOptionPane.showMessageDialog(this, "Invalid car selection.");
//            return;
//        }
//
//        int days;
//        try {
//            days = Integer.parseInt(daysField.getText().trim());
//            if (days <= 0) throw new NumberFormatException();
//        } catch (NumberFormatException ex) {
//            JOptionPane.showMessageDialog(this, "Please enter a valid number of days.");
//            return;
//        }
//
//        float total = car.getPrice() * days;
//
//        int confirm = JOptionPane.showConfirmDialog(this,
//                "Total Price: €" + total + "\nConfirm Rent?",
//                "Confirm Rent", JOptionPane.YES_NO_OPTION);
//
//        if (confirm == JOptionPane.YES_OPTION) {
//            try (Connection conn = database.getConnection()) {
//                PreparedStatement countStmt = conn.prepareStatement("SELECT COUNT(*) AS count FROM rents");
//                ResultSet rs = countStmt.executeQuery();
//                int rentID = 1;
//                if (rs.next()) rentID = rs.getInt("count") + 1;
//
//                Rent rent = new Rent();
//                PreparedStatement insert = conn.prepareStatement(
//                        "INSERT INTO rents (ID, client, car, dateTime, days, total, status) VALUES (?, ?, ?, ?, ?, ?, ?)"
//                );
//                insert.setInt(1, rentID);
//                insert.setInt(2, client.getID());
//                insert.setInt(3, car.getID());
//                insert.setString(4, rent.getDateTime());
//                insert.setInt(5, days);
//                insert.setFloat(6, total);
//                insert.setInt(7, 0);
//                insert.executeUpdate();
//
//                PreparedStatement update = conn.prepareStatement("UPDATE cars SET available = available - 1 WHERE ID = ?");
//                update.setInt(1, car.getID());
//                update.executeUpdate();
//
//                JOptionPane.showMessageDialog(this, "Car rented successfully!");
//                dispose();
//            } catch (SQLException ex) {
//                ex.printStackTrace();
//                JOptionPane.showMessageDialog(this, "An error occurred during the rent process.");
//            }
//        }
//    }
}
