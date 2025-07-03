/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import DatabaseConfig.Database;
import Model.Car;
import Model.Operation;
import Model.Rent;
import Model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;
import java.sql.SQLException;

/**
 *
 * @author nikwn
 */
public class RentCar implements Operation {
    
    @Override
    public void operation(Database database, Scanner s, User user){
        Connection connection = database.getConnection();
        
        System.out.println("Enter Car ID : (-1) to show all cars");
        int carID = s.nextInt();
        while(carID == -1){
            new ViewCars().operation(database, s, user);
            System.out.println("Enter Car ID : (-1) to show all cars");
            carID = s.nextInt();
        }
        
        System.out.println("Enter days :");
        int days = s.nextInt();
        
        try{
            PreparedStatement pr = connection.prepareStatement("SELECT COUNT(*) AS count FROM rents;");
            ResultSet rs = pr.executeQuery();
            int ID = 0;
            if (rs.next()) {
                ID = rs.getInt("count")+1;  // count will be 0 if table is empty
            }
            pr = connection.prepareStatement("SELECT * FROM cars WHERE ID = ?");
            pr.setInt(1, carID);
            rs = pr.executeQuery();
            while(rs.next()){
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
                if(car.getAvailable() < 0){
                    System.out.println("Car isn't available");
                    return;
                }
                pr = connection.prepareStatement("SELECT COUNT(*) AS count FROM rents;");
                rs = pr.executeQuery(); 
                ID = 0;
                if (rs.next()) {
                    ID = rs.getInt("count")+1;  // count will be 0 if table is empty
                }
                float total = car.getPrice()*days;
                System.out.println("Total Price : " +total+"€");
                System.out.println("Days of Renting: " +days);
                System.out.println("Confirm Rent ?: (yes/no) ");
                String confirm = s.next();

                if (confirm.equalsIgnoreCase("yes")) {
                
                    Rent rent = new Rent();
                    pr = connection.prepareStatement(
                        "INSERT INTO `rents` (`ID`, `client`, `car`, `dateTime`, `days`, `total`, `status`)" +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)");
                    pr.setInt(1, ID);
                    pr.setInt(2, user.getID());
                    pr.setInt(3, carID);
                    pr.setString(4, rent.getDateTime());
                    pr.setInt(5, days);
                    pr.setFloat(6, total);
                    pr.setInt(7, 0);
                    pr.executeUpdate();
                
                    pr = connection.prepareStatement("UPDATE cars SET available = available - 1 WHERE ID = ?");
                    pr.setInt(1, carID);
                    pr.executeUpdate();

                    System.out.println("Car rented successfully!");
                } else {
                    System.out.println("Rent cancelled.");
                    return;
                }                
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
