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

public class RentCarGUI extends JFrame {
    private final Database database;
    private final Client client;
    private final JComboBox<String> carDropdown;
    private final JTextField daysField;
    private final JLabel imageLabel;
    private final JPanel carDetailsPanel;
    private final HashMap<String, JLabel> detailLabels = new HashMap<>();
    private final HashMap<String, Car> carMap = new HashMap<>();
    private static final Font DEFAULT_FONT = new Font("SansSerif", Font.PLAIN, 14);

    public RentCarGUI(Database database, Client client) {
        this.database = database;
        this.client = client;

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
carDetailsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
//carDetailsPanel.setBorder(BorderFactory.createTitledBorder("Car Details"));
carDetailsPanel.setBackground(Color.WHITE);

String[] labels = {
    "Brand", "Model", "Year", "Fuel", "Gearbox",
    "Engine CC", "Horsepower", "Consumption", "Price/Day", "Available"
};

for (String label : labels) {
    JLabel keyLabel = new JLabel(label + ": ");
    keyLabel.setFont(DEFAULT_FONT.deriveFont(Font.BOLD));
    JLabel valueLabel = new JLabel();
    valueLabel.setFont(DEFAULT_FONT);
    carDetailsPanel.add(keyLabel);
    carDetailsPanel.add(valueLabel);
    detailLabels.put(label, valueLabel);
}

JPanel centerWrapper = new JPanel(new BorderLayout());
centerWrapper.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
centerWrapper.add(carDetailsPanel, BorderLayout.CENTER);
add(centerWrapper, BorderLayout.CENTER);


// --- SOUTH: Dropdown + Days + Button ---
JPanel bottomPanel = new JPanel();
bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

// Row 1: Car Dropdown
JPanel carRow = new JPanel(new BorderLayout(5, 5));
carDropdown = new JComboBox<>();
carDropdown.setFont(DEFAULT_FONT);
carDropdown.addActionListener(this::updateCarPreview);
JLabel selectCarLabel = new JLabel("Select Car:");
selectCarLabel.setFont(DEFAULT_FONT);
carRow.add(selectCarLabel, BorderLayout.WEST);
carRow.add(carDropdown, BorderLayout.CENTER);
bottomPanel.add(carRow);

// Row 2: Days input
JPanel daysRow = new JPanel(new BorderLayout(5, 5));
daysField = new JTextField();
daysField.setFont(DEFAULT_FONT);
JLabel daysLabel = new JLabel("Number of Days:");
daysLabel.setFont(DEFAULT_FONT);
daysRow.add(daysLabel, BorderLayout.WEST);
daysRow.add(daysField, BorderLayout.CENTER);
bottomPanel.add(Box.createVerticalStrut(8));
bottomPanel.add(daysRow);

// Row 3: Rent button
JButton rentButton = new JButton("Confirm Rent");
rentButton.setFont(DEFAULT_FONT);
rentButton.addActionListener(this::handleRent);
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
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM cars WHERE available > 0");
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

        // ... existing image logic ...

detailLabels.get("Brand").setText(car.getBrand());
detailLabels.get("Model").setText(car.getModel());
detailLabels.get("Year").setText(String.valueOf(car.getYear()));
detailLabels.get("Fuel").setText(car.getFuel());
detailLabels.get("Gearbox").setText(car.getGearbox());
detailLabels.get("Engine CC").setText(car.getEnginecc() + " cc");
detailLabels.get("Horsepower").setText(car.getHorsepower() + " hp");
detailLabels.get("Consumption").setText(String.format("%.1f L/100km", car.getConsumption()));
detailLabels.get("Price/Day").setText(String.format("%.2f €", car.getPrice()));
detailLabels.get("Available").setText(String.valueOf(car.getAvailable()));

    }

    private void handleRent(ActionEvent e) {
        String selectedKey = (String) carDropdown.getSelectedItem();
        Car car = carMap.get(selectedKey);

        if (car == null) {
            JOptionPane.showMessageDialog(this, "Invalid car selection.");
            return;
        }

        int days;
        try {
            days = Integer.parseInt(daysField.getText().trim());
            if (days <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number of days.");
            return;
        }

        float total = car.getPrice() * days;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Total Price: €" + total + "\nConfirm Rent?",
                "Confirm Rent", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = database.getConnection()) {
                PreparedStatement countStmt = conn.prepareStatement("SELECT COUNT(*) AS count FROM rents");
                ResultSet rs = countStmt.executeQuery();
                int rentID = 1;
                if (rs.next()) rentID = rs.getInt("count") + 1;

                Rent rent = new Rent();
                PreparedStatement insert = conn.prepareStatement(
                        "INSERT INTO rents (ID, client, car, dateTime, days, total, status) VALUES (?, ?, ?, ?, ?, ?, ?)"
                );
                insert.setInt(1, rentID);
                insert.setInt(2, client.getID());
                insert.setInt(3, car.getID());
                insert.setString(4, rent.getDateTime());
                insert.setInt(5, days);
                insert.setFloat(6, total);
                insert.setInt(7, 0);
                insert.executeUpdate();

                PreparedStatement update = conn.prepareStatement("UPDATE cars SET available = available - 1 WHERE ID = ?");
                update.setInt(1, car.getID());
                update.executeUpdate();

                JOptionPane.showMessageDialog(this, "Car rented successfully!");
                dispose();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "An error occurred during the rent process.");
            }
        }
    }
}
