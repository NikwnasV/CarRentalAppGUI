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
import Model.Client;

import javax.swing.*;
import java.awt.*;

public class ClientDashboard extends JFrame {

    private final Database database;
    private final Client client;

    public ClientDashboard(Database db, Client clientUser) {
        this.database = db;
        this.client = clientUser;

        setTitle("Client Dashboard - Welcome " + client.getFirstName());
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        JLabel title = new JLabel("Welcome, " + client.getFirstName(), SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        JPanel menuPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 20, 40));

        JButton viewCars = new JButton("View All Cars");
        JButton rentCar = new JButton("Rent Car");
        JButton returnCar = new JButton("Return Car");
        JButton showRents = new JButton("Show My Rents");
        JButton editData = new JButton("Edit My Data (coming soon)");
        JButton logout = new JButton("Logout");

        menuPanel.add(viewCars);
        menuPanel.add(rentCar);
        menuPanel.add(returnCar);
        menuPanel.add(showRents);
        menuPanel.add(editData);
        menuPanel.add(logout);

        add(menuPanel, BorderLayout.CENTER);

        viewCars.addActionListener(e -> new ViewCarsGUI(new Database()));
        rentCar.addActionListener(e -> new RentCarGUI(new Database(), client));
        returnCar.addActionListener(e -> new ReturnCarGUI(new Database(), client));
        showRents.addActionListener(e -> new ShowUserRentsGUI(new Database(), client));

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