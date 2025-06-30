package main;

import Controller.AddNewAccount;
import Model.Admin;
import DatabaseConfig.Database;
import Model.Client;
import Model.User;
import Utils.SecurityUtil;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Scanner;

public class CarRentalApp {
    public static void main(String[] args) {
        Database database = new Database();
        
        Scanner sc = new Scanner(System.in);
        
        boolean loggedIn = false;
        while(!loggedIn) {
            System.out.println("Welcome to Car Rental App");
            System.out.println("Enter your email:\n (-1) to create new account:");
            String email = sc.next();
            
            if(email.equals("0")){
                break;
            }

            while (!email.contains("@") && !email.equals("-1")) {
                System.out.println("Please enter a valid email address!:");
                email = sc.next();
            }

            if (email.equals("-1")) {
                new AddNewAccount(0).operation(database, sc, null);
                continue; // επιστρέφει στην αρχή του login loop
            }

            System.out.println("Enter Password:");
            String password = sc.next();
        
            ArrayList<User> users = new ArrayList<>();
            try {
                String hashedPassword = SecurityUtil.hashPassword(password);
                PreparedStatement pr = database.getConnection().prepareStatement("SELECT * FROM users WHERE email = ? AND password = ?");
                pr.setString(1, email);
                pr.setString(2, hashedPassword);
                ResultSet rs = pr.executeQuery();
                while (rs.next()) {
                    User user;
                    int ID = rs.getInt("ID");
                    String firstName = (rs.getString("firstName"));
                    String lastName = (rs.getString("lastName"));
                    String em = (rs.getString("email"));
                    String phoneNumber = (rs.getString("phoneNumber"));
                    String passwd = (rs.getString("password"));
                    int role = (rs.getInt("role")); 
                    switch(role) {
                        case 0:
                            user = new Client();
                            break;
                        case 1:
                            user = new Admin();
                            break;
                        default:
                            System.out.println("Account doesn't exist!\n");
                            return;
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
            if(users.isEmpty()){
                System.out.println("Email or Password is incorrect!\n");
            } else {
                for(User u : users){
                    if(u.getEmail().equals(email) && u.getPassword().equals(SecurityUtil.hashPassword(password))){
                        loggedIn = true;
                        System.out.println("Welcome " + u.getFirstName()+ " !");
                        u.showList(database, sc);        
                    }
                }
            }
        }
        sc.close();
    }
}
