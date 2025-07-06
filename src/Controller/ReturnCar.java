package Controller;

import DatabaseConfig.Database;
import Model.Operation;
import Model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class ReturnCar implements Operation {

    @Override
    public void operation(Database database, Scanner sc, User user) {
        Connection connection = database.getConnection();

        try {
            System.out.println("Enter Rent ID to return:");
            int rentID = sc.nextInt();

            // Verify rent belongs to user and get car ID + current status
            PreparedStatement verifyStmt = connection.prepareStatement(
                "SELECT status, car FROM rents WHERE ID = ? AND client = ?"
            );
            verifyStmt.setInt(1, rentID);
            verifyStmt.setInt(2, user.getID());

            ResultSet rs = verifyStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Rent ID not found or does not belong to you.");
                return;
            }

            int status = rs.getInt("status");
            if (status == 1) {
                System.out.println("This car is already returned.");
                return;
            }

            int carID = rs.getInt("car");

            // Update rent status to returned (1)
            PreparedStatement updateRentStmt = connection.prepareStatement(
                "UPDATE rents SET status = 1 WHERE ID = ?"
            );
            updateRentStmt.setInt(1, rentID);
            int rowsUpdated = updateRentStmt.executeUpdate();

            if (rowsUpdated > 0) {
                // Increase car availability by 1
                PreparedStatement updateCarStmt = connection.prepareStatement(
                    "UPDATE cars SET available = available + 1 WHERE ID = ?"
                );
                updateCarStmt.setInt(1, carID);
                int carsUpdated = updateCarStmt.executeUpdate();

                if (carsUpdated > 0) {
                    System.out.println("Car successfully returned and availability updated.");
                } else {
                    System.out.println("Car returned, but failed to update car availability.");
                }
            } else {
                System.out.println("Failed to update rent status.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Database error while returning car.");
        } catch (Exception e) {
            System.out.println("Invalid input.");
        }
    }
}
