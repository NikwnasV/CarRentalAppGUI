package main;

import Model.Admin;
import DatabaseConfig.Database;
import Model.Client;
import Model.User;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Scanner;

public class CarRentalApp {
    public static void main(String[] args) {
        Database database = new Database();
        
        Scanner sc = new Scanner(System.in);
        
        System.out.println("Welcome to Car Rental App");
        System.out.println("Enter your email:\n (-1) to create new account");
        String email = sc.next();
        System.out.println("Enter Password:");
        String password = sc.next();
        
        ArrayList<User> users = new ArrayList<>();
        try {
            //String hashedPassword = database.hashPassword(password);
            PreparedStatement pr = database.getConnection().prepareStatement("SELECT * FROM users WHERE email = ? AND password = ?");
            pr.setString(1, email);
            pr.setString(2, password);
            ResultSet rs = pr.executeQuery();
            while (rs.next()) {
                User user;
                int ID = rs.getInt("ID");
                String firstName = (rs.getString("firstName"));
                String lastName = (rs.getString("lastName"));
                String em = (rs.getString("email"));
                String phoneNumber = (rs.getString("phoneNumber"));
                String passwd = (rs.getString("password"));
                //passwd = database.hashPassword(passwd);
                int role = (rs.getInt("role")); 
                switch(role) {
                    case 0:
                        user = new Client();
                        break;
                    case 1:
                        user = new Admin();
                        break;
                    default:
                        user = new Client();
                        break;
                }
                user.setID(ID);
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setEmail(em);
                user.setPhoneNumber(phoneNumber);
                user.setPassword(passwd);
                users.add(user);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        for(User u : users){
            if(u.getEmail().equals(email) && u.getPassword().equals(password)){
                System.out.println("Welcome " + u.getFirstName()+ " !");
                u.showList(database, sc);
            }
        
        }
        //admin.showList(database, sc);

        sc.close();
    }
}
