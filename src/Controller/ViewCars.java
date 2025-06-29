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
import java.util.ArrayList;


import DatabaseConfig.Database;
import Model.Operation;
import Model.User;
import Model.Car;

/**
 *
 * @author Agisilaos
 */
public class ViewCars implements Operation {
    
            public static final String GREEN = "\u001B[32m";
        public static final String RED = "\u001B[31m";
        public static final String RESET = "\u001B[0m";
    ArrayList<Car> cars = new ArrayList<>();
    
    @Override
    public void operation(Database database, Scanner s, User user){
        Connection connection = database.getConnection();
        System.out.println();
        try{
            PreparedStatement pr = connection.prepareStatement("SELECT * FROM cars;");
            ResultSet rs = pr.executeQuery();
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
                cars.add(car);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        for (Car c : cars) {
            System.out.printf("%-25s %s\n", "ID:", c.getID());
            System.out.printf("%-25s %s\n", "Brand:", c.getBrand());
            System.out.printf("%-25s %s\n", "Model:", c.getModel());
            System.out.printf("%-25s %s\n", "Year:", c.getYear());
            System.out.printf("%-25s %s\n", "Fuel Type:", c.getFuel());
            System.out.printf("%-25s %s\n", "Gearbox:", c.getGearbox());
            System.out.printf("%-25s %d\n", "Engine CC:", c.getEnginecc());
            System.out.printf("%-25s %d\n", "Horsepower:", c.getHorsepower());
            System.out.printf("%-25s %.1f\n", "Consumption (l/100km):", c.getConsumption());
            System.out.printf("%-25s %.1f\n", "Price per Day (\u20ac):", c.getPrice()); // \u20ac = euro sign
            String status = (c.getAvailable() > 0) ? GREEN + "Available" + RESET : RED + "Unavailable" + RESET;
            System.out.printf("%-25s %s\n", "Status:", status);
            System.out.println("--------------------------------------------------");
        }
        System.out.println("----------------------\n");
    }
}
