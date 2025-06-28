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
import Model.Car;
/**
 *
 * @author Agisilaos
 */
public class UpdateCar implements Operation {
    @Override
    public void operation(Database database, Scanner s, User user){
        
        Connection connection = database.getConnection();
        
        System.out.println("Enter car ID you want to update or -1 to view all cars:" );
        int ID= s.nextInt();
        while (ID == -1){
            new ViewCars().operation(database,s,user);
            System.out.println("Enter car ID you want to update:" );
            ID= s.nextInt();
        }
        try{
            PreparedStatement pr = connection.prepareStatement("SELECT * FROM cars WHERE ID = ?");
            pr.setInt(1, ID);
            ResultSet rs1 = pr.executeQuery();
            
            if (!rs1.next()) {
                System.out.println("Car with ID " + ID + " does not exist.");
                return;
            }
            Car car = new Car();
            car.setID(rs1.getInt("ID"));
            car.setBrand(rs1.getString("brand"));
            car.setModel(rs1.getString("model"));
            car.setYear(rs1.getInt("year"));
            car.setFuel(rs1.getString("fuel"));
            car.setGearbox(rs1.getString("gearbox"));
            car.setEnginecc(rs1.getInt("enginecc"));
            car.setHorsepower(rs1.getInt("horsepower"));
            car.setConsumption(rs1.getFloat("consumption"));
            car.setPrice(rs1.getFloat("price"));
            car.setAvailable(rs1.getInt("available"));
            if (car.getAvailable() < 1) {
                System.out.println("Car is unavailable.");
                return;
            }
            
            System.out.println("Enter Brand (-1 to keep: " + car.getBrand() + "): ");
            String brand = s.next();
            if (brand.equals("-1")) brand = car.getBrand();
            System.out.println("Enter Model (-1 to keep: " + car.getModel() + "): ");
            String model = s.next();
            if (model.equals("-1")) model = car.getModel();
            System.out.println("Enter Year (-1 to keep: " + car.getYear() + "): ");
            int year = s.nextInt();
            if (year == -1) year = car.getYear();
            System.out.println("Enter Fuel Type (-1 to keep: " + car.getFuel() + "): ");
            String fuel = s.next();
            if (fuel.equals("-1")) fuel = car.getFuel();
            System.out.println("Enter Gearbox Type (-1 to keep: " + car.getGearbox() + "): ");
            String gearbox = s.next();
            if (gearbox.equals("-1")) gearbox = car.getGearbox();
            System.out.println("Enter Engine CC (-1 to keep: " + car.getEnginecc() + "): ");
            int enginecc = s.nextInt();
            if (enginecc == -1) enginecc = car.getEnginecc();
            System.out.println("Enter Horsepower (-1 to keep: " + car.getHorsepower() + "): ");
            int horsepower = s.nextInt();
            if (horsepower == -1) horsepower = car.getHorsepower();
            System.out.println("Enter Consumption (-1 to keep: " + car.getConsumption() + "): ");
            String inputConsumption = s.next();
            float consumption = inputConsumption.equals("-1") ? car.getConsumption() : Float.parseFloat(inputConsumption);
            System.out.println("Enter Price (-1 to keep: " + car.getPrice() + "): ");
            float price = s.nextFloat();
            if (price == -1) price = car.getPrice();
            System.out.println("Enter Available count (-1 to keep: " + car.getAvailable() + "): ");
            int available = s.nextInt();
            if (available == -1) available = car.getAvailable();
            
            pr = connection.prepareStatement("UPDATE cars SET brand = ?, model = ?, year = ?, fuel = ?, gearbox = ?, " +
                    "enginecc = ?, horsepower = ?, consumption = ?, price = ?, available = ? " +
                    "WHERE ID = ?;");
            pr.setString(1, brand);
            pr.setString(2, model);
            pr.setInt(3, year);
            pr.setString(4, fuel);
            pr.setString(5, gearbox);
            pr.setInt(6, enginecc);
            pr.setInt(7, horsepower);
            pr.setFloat(8, consumption);
            pr.setFloat(9, price);
            pr.setInt(10, available);
            pr.setInt(11, ID);
            pr.executeUpdate();
            System.out.println("Car updated successfully.");
        }catch(SQLException e){
            e.printStackTrace();
        }
    }  
}
