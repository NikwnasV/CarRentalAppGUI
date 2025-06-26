/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import DatabaseConfig.Database;
import Model.Operation;
import Model.User;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Scanner;
import java.sql.SQLException;

/**
 *
 * @author nikwn
 */
public class AddNewClient implements Operation {
    
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
        int role = 0;
        Connection connection = database.getConnection();
        try {
            PreparedStatement pr = connection.prepareStatement("insert into users "
                    + "(firstName, lastName, email, phoneNumber, password, role) VALUES (?, ?, ?, ?)");
            String hashedPassword = hashPassword(user.getPassword());
            pr.setString(1, firstName);
            pr.setString(2, lastName);
            pr.setString(3, email);
            pr.setString(4, phoneNumber);
            pr.setString(5, hashedPassword);
            pr.setInt(6, role);
            pr.executeUpdate();
            System.out.println("Admin Account Created Successfully");
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
    
    
    
          private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] hashedBytes = md.digest(password.getBytes("UTF-8"));

            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b)); // Convert byte to hex
            }
            return sb.toString();

        } catch (NoSuchAlgorithmException | java.io.UnsupportedEncodingException ex) {
            throw new RuntimeException("Error hashing password", ex);
        }
    }
}
