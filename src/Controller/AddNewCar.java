/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import java.util.Scanner;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;


import DatabaseConfig.Database;
import Model.Operation;
import Model.User;

/**
 *
 * @author Agisilaos
 */
public class AddNewCar implements Operation {
    @Override
    public void operation(Database database, Scanner s, User user){
        System.out.println("Enter Brand:");
        String brand = s.next();
        System.out.println("Enter Model:");
        String model = s.next();
        System.out.println("Enter year : ");
        int year = s.nextInt();
        System.out.println("Enter Fuel Type : ");
        String fuel = s.next();
        System.out.println("Enter Gearbox type: ");
        String gearbox = s.next();
        System.out.println("Enter EngineCC: ");
        int enginecc = s.nextInt();
        System.out.println("Enter Horsepower : ");
        int horsepower = s.nextInt(); 
        System.out.println("Enter Fuel Consumption");
        float consumption = Float.parseFloat(s.next());
        System.out.println("Enter daily price:");
        float price = s.nextFloat();
        System.out.println("Enter available number of cars:");
        int available = s.nextInt();
        Connection connection = database.getConnection();
        try { 
            PreparedStatement pr = connection.prepareStatement("SELECT MAX(ID) AS lastID FROM cars;");
            ResultSet rs = pr.executeQuery();
            int ID = 0; // if table is empty, start from 0

            if (rs.next() && rs.getObject("lastID") != null) {
                ID = rs.getInt("lastID") + 1;
            }

            pr = connection.prepareStatement(
                "INSERT INTO `cars` (`ID`, `brand`, `model`, `year`, `fuel`, `gearbox`, `enginecc`, `horsepower`, `consumption`, `price`, `available`) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            pr.setInt(1, ID);
            pr.setString(2, brand);
            pr.setString(3, model);
            pr.setInt(4, year);
            pr.setString(5, fuel);
            pr.setString(6, gearbox);
            pr.setInt(7, enginecc);
            pr.setInt(8, horsepower);
            pr.setFloat(9, consumption);
            pr.setFloat(10, price);
            pr.setInt(11, available);
            pr.executeUpdate();
                     System.out.println("Car added successfully");
        }catch(SQLException e){
            e.printStackTrace();
        }
        
          
        
    }
}
