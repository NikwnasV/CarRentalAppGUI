package Controller;

import static Controller.ViewCars.BLUE;
import static Controller.ViewCars.GREEN;
import static Controller.ViewCars.RED;
import static Controller.ViewCars.RESET;
import DatabaseConfig.Database;
import Model.Car;
import Model.Client;
import Model.Operation;
import Model.Rent;
import Model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

public class ShowRents implements Operation {

    @Override
    public void operation(Database database, Scanner sc, User user) {
        Connection connection = database.getConnection();
        System.out.println();
        ArrayList<Rent> rents = new ArrayList<>();

        try {
            // Get all rents, no filter by user
            PreparedStatement pr = connection.prepareStatement("SELECT * FROM rents;");
            ResultSet rs = pr.executeQuery();

            while (rs.next()) {
                Rent rent = new Rent();
                rent.setID(rs.getInt("ID"));
                int carID = rs.getInt("car");
                int clientID = rs.getInt("client");

                rent.setCar(getCarByID(carID, connection));
                rent.setDateTime(rs.getString("dateTime"));
                rent.setDays(rs.getInt("days"));
                rent.setTotal(rs.getFloat("total"));
                rent.setStatus(rs.getInt("status"));
                rent.setUser(getUserByID(clientID, connection)); // fetch user info

                // --- Check if delayed ---
                LocalDateTime rentDate = rent.getRawDateTime();
                LocalDateTime returnDate = rentDate.plusDays(rent.getDays());

                if (rent.getStatus() == 0 && LocalDateTime.now().isAfter(returnDate)) {
                    rent.setStatus(2); // Delayed
                    PreparedStatement updateStatus = connection.prepareStatement("UPDATE rents SET status = 2 WHERE ID = ?");
                    updateStatus.setInt(1, rent.getID());
                    updateStatus.executeUpdate();
                }

                rents.add(rent);
            }

            // Display all rents
            for (Rent r : rents) {
                System.out.printf("%-25s %s\n", "Rent ID:", r.getID());
                User u = r.getUser();
                if (u != null) {
                    System.out.printf("%-25s %s %s (ID: %d)\n", "Client:", u.getFirstName(), u.getLastName(), u.getID());
                } else {
                    System.out.printf("%-25s %s\n", "Client:", "Unknown");
                }

                Car c = r.getCar();
                if (c != null) {
                    System.out.printf("%-25s %s %s (%d)\n", "Car:", c.getBrand(), c.getModel(), c.getYear());
                    System.out.printf("%-25s %.2f €/day\n", "Price per Day:", c.getPrice());
                } else {
                    System.out.printf("%-25s %s\n", "Car:", "Not found");
                }

                System.out.printf("%-25s %s\n", "Date:", r.getDateTime());
                System.out.printf("%-25s %d\n", "Days:", r.getDays());
                System.out.printf("%-25s %.2f €\n", "Total:", r.getTotal());

                String statusStr = switch (r.getStatus()) {
                    case 0 -> BLUE + "Running" + RESET;
                    case 1 -> GREEN + "Returned" + RESET;
                    case 2 -> RED + "Delayed" + RESET;
                    default -> "Unknown";
                };
                System.out.printf("%-25s %s\n", "Status:", statusStr);
                System.out.println("--------------------------------------------------");
            }

            if (rents.isEmpty()) {
                System.out.println(RED + "No rentals found." + RESET);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("----------------------\n");
    }

    // Helper method to fetch Car by ID
    private Car getCarByID(int carID, Connection connection) throws SQLException {
        PreparedStatement carStmt = connection.prepareStatement("SELECT * FROM cars WHERE ID = ?");
        carStmt.setInt(1, carID);
        ResultSet carRs = carStmt.executeQuery();

        if (carRs.next()) {
            Car car = new Car();
            car.setID(carRs.getInt("ID"));
            car.setBrand(carRs.getString("brand"));
            car.setModel(carRs.getString("model"));
            car.setYear(carRs.getInt("year"));
            car.setFuel(carRs.getString("fuel"));
            car.setGearbox(carRs.getString("gearbox"));
            car.setEnginecc(carRs.getInt("enginecc"));
            car.setHorsepower(carRs.getInt("horsepower"));
            car.setConsumption(carRs.getFloat("consumption"));
            car.setPrice(carRs.getFloat("price"));
            car.setAvailable(carRs.getInt("available"));
            return car;
        }

        return null;
    }

    // Helper method to fetch User by ID
    private User getUserByID(int userID, Connection connection) throws SQLException {
        PreparedStatement userStmt = connection.prepareStatement("SELECT * FROM users WHERE ID = ?");
        userStmt.setInt(1, userID);
        ResultSet userRs = userStmt.executeQuery();

        if (userRs.next()) {
            User user = new Client(); // or Client/Admin if you want roles handled
            user.setID(userRs.getInt("ID"));
            user.setFirstName(userRs.getString("firstName"));
            user.setLastName(userRs.getString("lastName"));
            user.setEmail(userRs.getString("email"));
            user.setPhoneNumber(userRs.getString("phoneNumber"));
            return user;
        }

        return null;
    }
}
