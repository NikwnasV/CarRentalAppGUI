package Controller;

import DatabaseConfig.Database;
import Model.Operation;
import Model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class DeleteCar implements Operation {
    @Override
    public void operation(Database database, Scanner s, User user){
        Connection connection = database.getConnection();     

        System.out.println("Enter car ID you want to delete, or -1 to view all cars:" );
        int ID = s.nextInt(); 
        while (ID == -1) {
            new ViewCars().operation(database, s, user);
            System.out.println("Enter car ID you want to delete:" );
            ID = s.nextInt();
        }

        try {
            // First, check if car exists
            PreparedStatement check = connection.prepareStatement("SELECT * FROM cars WHERE ID = ?");
            check.setInt(1, ID);
            ResultSet rs = check.executeQuery();

            if (!rs.next()) {
                System.out.println("No car found with ID " + ID);
                return;
            }

            // Display some basic info before confirming deletion
            System.out.println("You are about to delete the following car:");
            System.out.println("Brand: " + rs.getString("brand"));
            System.out.println("Model: " + rs.getString("model"));
            System.out.println("Year: " + rs.getInt("year"));
            System.out.print("Are you sure you want to delete it? (yes/no): ");
            String confirm = s.next();

            if (!confirm.equalsIgnoreCase("yes")) {
                System.out.println("Deletion cancelled.");
                return;
            }

            // Proceed with deletion
            PreparedStatement pr = connection.prepareStatement("DELETE FROM cars WHERE ID = ?");
            pr.setInt(1, ID);
            int rowsAffected = pr.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Car deleted successfully.");
            } else {
                System.out.println("Car deletion failed.");
            }

        } catch(SQLException e){
            e.printStackTrace();
        }
    }
}
