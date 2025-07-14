/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package View;

/**
 *
 * @author nikwn
 */

import DatabaseConfig.Database;
import Model.Admin;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

    private final Database database;
    private final Admin admin;

    public AdminDashboard(Database db, Admin adminUser) {
        this.database = db;
        this.admin = adminUser;

        setTitle("Admin Dashboard - Welcome " + admin.getFirstName());
        setSize(500, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        JLabel title = new JLabel("Welcome, Admin " + admin.getFirstName(), SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        JPanel menuPanel = new JPanel(new GridLayout(8, 1, 10, 10));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 20, 40));

        // Buttons for each operation
        JButton addCar = new JButton("Add New Car");
        JButton viewCars = new JButton("View All Cars");
        JButton updateCar = new JButton("Update Car");
        JButton deleteCar = new JButton("Delete Car");
        JButton addAdmin = new JButton("Add New Admin");
        JButton showRents = new JButton("Show Rents");
        JButton showStats = new JButton("Show Stats (coming soon)");
        JButton logout = new JButton("Logout");

        // Add buttons to panel
        menuPanel.add(addCar);
        menuPanel.add(viewCars);
        menuPanel.add(updateCar);
        menuPanel.add(deleteCar);
        menuPanel.add(addAdmin);
        menuPanel.add(showRents);
        menuPanel.add(showStats);
        menuPanel.add(logout);

        add(menuPanel, BorderLayout.CENTER);

        // Button actions
        addCar.addActionListener(e -> new AddNewCarGUI(new Database()));
        viewCars.addActionListener(e -> new ViewCarsGUI(new Database()));
        updateCar.addActionListener(e -> new UpdateCarGUI(new Database()));
        deleteCar.addActionListener(e -> new DeleteCarGUI(new Database()));
        addAdmin.addActionListener(e -> new AddNewAccountGUI(new Database(), 1));
        //showRents.addActionListener(e -> operations[5].operation(database, this, admin));

        logout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                main.CarRentalApp.main(null);
            }
        });

        setVisible(true);
    }
}
