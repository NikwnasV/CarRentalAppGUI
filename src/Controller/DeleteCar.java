/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import DatabaseConfig.Database;
import Model.Operation;
import Model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;
import java.sql.SQLException;
/**
 *
 * @author Agisilaos
 */
public class DeleteCar implements Operation {
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
        try {
            PreparedStatement pr = connection.prepareStatement("Delete * FROM 'cars' WHERE 'ID' = '+ID+';");
            ResultSet rs1 = pr.executeQuery();
            System.out.println("Car deleted successfully");
        }catch(SQLException e){
            e.printStackTrace();
        }

        
    }
    
}
