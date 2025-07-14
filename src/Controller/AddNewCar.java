/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import DatabaseConfig.Database;
import Model.Operation;
import Model.User;
import View.AddNewCarGUI;
import java.awt.Component;
import java.util.Scanner;

/**
 *
 * @author Agisilaos
 */

public class AddNewCar implements Operation {

    public void operation(Database database, Component parent, User user) {
        new AddNewCarGUI(database);
    }

    public void insert(Database database,
                       String brand, String model, int year, String fuel, String gearbox,
                       int enginecc, int horsepower, float consumption, float price, int available) throws SQLException {

        Connection connection = database.getConnection();
        int ID = 0;
        try (PreparedStatement pr = connection.prepareStatement("SELECT MAX(ID) AS lastID FROM cars");
             ResultSet rs = pr.executeQuery()) {
            if (rs.next() && rs.getObject("lastID") != null) {
                ID = rs.getInt("lastID") + 1;
            }
        }

        String insertSQL = "INSERT INTO cars (ID, brand, model, year, fuel, gearbox, enginecc, horsepower, consumption, price, available) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement insert = connection.prepareStatement(insertSQL)) {
            insert.setInt(1, ID);
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

            insert.executeUpdate();
        }
    }

    @Override
    public void operation(Database dbconfig, Scanner sc, User user) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

