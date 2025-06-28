/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import DatabaseConfig.Database;
import Model.Operation;
import Model.User;
import Utils.SecurityUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;
import java.sql.SQLException;

/**
 *
 * @author nikwn
 */
public class AddNewAccount implements Operation {
    
    private int accType;
    
    public AddNewAccount(int accType) {
        this.accType = accType;
    }
    
    @Override
    public void operation(Database database, Scanner sc, User user){
        System.out.println("Enter First Name: ");
        String firstName = sc.next();
        System.out.println("Enter Last Name: ");
        String lastName = sc.next();
        System.out.println("Enter Email: ");
        String email = sc.next();
        System.out.println("Enter Phone Number: ");
        String phoneNumber = sc.next();
        System.out.println("Enter Password: ");
        String password = sc.next();
        System.out.println("Confirm Password: ");
        String confirmPassword = sc.next();
        while(!password.equals(confirmPassword)){
            System.out.println("Password doesn't match");
            System.out.println("Enter Password: ");
            password = sc.next();
            System.out.println("Confirm Password: ");
            confirmPassword = sc.next();
        }
        int role = accType;
        Connection connection = database.getConnection();
        try {
            PreparedStatement pr = connection.prepareStatement("SELECT COUNT(*) AS count FROM users;");
            ResultSet rs = pr.executeQuery();
    
            int ID = 1;
            if (rs.next()) {
                ID = rs.getInt("count")+1;  // count will be 0 if table is empty
            }
            pr = connection.prepareStatement("insert into users "
                    + "(ID, firstName, lastName, email, phoneNumber, password, role) VALUES (?, ?, ?, ?, ?, ?, ?)");
            String hashedPassword = SecurityUtil.hashPassword(password);
            pr.setInt(1, ID);
            pr.setString(2, firstName);
            pr.setString(3, lastName);
            pr.setString(4, email);
            pr.setString(5, phoneNumber);
            pr.setString(6, hashedPassword);
            pr.setInt(7, role);
            pr.executeUpdate();
            System.out.println("Account Created Successfully");
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

}
