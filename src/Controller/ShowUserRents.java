package Controller;


import static Controller.ViewCars.BLUE;
import static Controller.ViewCars.GREEN;
import static Controller.ViewCars.RED;
import static Controller.ViewCars.RESET;
import DatabaseConfig.Database;
import Model.Car;
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

public class ShowUserRents implements Operation {

    @Override
    public void operation(Database database, Scanner sc, User user) {
        Connection connection = database.getConnection();
        System.out.println();
        ArrayList<Rent> rents = new ArrayList<>();

        try {
            // Get user's rentals
            PreparedStatement pr = connection.prepareStatement("SELECT * FROM rents WHERE client = ?;");
            pr.setInt(1, user.getID());
            ResultSet rs = pr.executeQuery();

            while (rs.next()) {
                Rent rent = new Rent();
                rent.setID(rs.getInt("ID"));
                int carID = rs.getInt("car");
                rent.setCar(getCarByID(carID, connection));
                rent.setDateTime(rs.getString("dateTime"));
                rent.setDays(rs.getInt("days"));
                rent.setTotal(rs.getFloat("total"));
                rent.setStatus(rs.getInt("status"));
                rent.setUser(user);

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

            // Display rentals
            for (Rent r : rents) {
                System.out.printf("%-25s %s\n", "Rent ID:", r.getID());
                System.out.printf("%-25s %s %s\n", "Client:", r.getUser().getFirstName(), r.getUser().getLastName());

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
                System.out.println(RED + "No rentals found for this user." + RESET);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("----------------------\n");
    }

    // Helper method to fetch a Car by ID
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
}
