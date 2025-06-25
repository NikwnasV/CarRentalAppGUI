package DatabaseConfig;

import Model.Client;
import Model.User;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.*;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAO {
    
    private static UserDAO instance;
    
    private UserDAO() {
    
    }
    
    public static synchronized UserDAO getInstance() {
        if(instance == null) {
            instance = new UserDAO();
        }
        return instance;
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
      
    public void save(User user) {
        Connection connection = DBConfig.getConnection();
        try {
            PreparedStatement pr = connection.prepareStatement("insert into users "
                    + "(username, password, email, role) VALUES (?, ?, ?, ?)");
            String hashedPassword = hashPassword(user.getPassword());
            pr.setString(1, user.getFirstName());
            pr.setString(2, hashedPassword);
            pr.setString(3, user.getEmail());
            //pr.setString(4, user.getRole());
            pr.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public User findByUnameAndPassword(String uname, String password) throws SQLException {
    Connection connection = DBConfig.getConnection();
    User user = null;
    try {
        String hashedPassword = hashPassword(password);
        PreparedStatement pr = connection.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
            pr.setString(1, uname);
            pr.setString(2, hashedPassword);
        ResultSet rs = pr.executeQuery();
        if (rs.next()) {
            user = new User() {
                @Override
                public void showList() {
                    throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
                }
            };
            user.setFirstName(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setEmail(rs.getString("email"));
            //user.setRole(rs.getString("role"));
        } else {
            System.out.println("No user found with username: " + uname + " and provided password.");
        }
    } catch (SQLException ex) {
        Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, "Database error during login", ex);
    } 
    return user;
}

    //Admin methods
    
     public void showList() {
        Connection connection = DBConfig.getConnection();
        List<Client> users = new ArrayList<>();
        try {
            PreparedStatement pr = connection.prepareStatement("SELECT * FROM users");
            ResultSet rs = pr.executeQuery();

            while (rs.next()) {
                Client user = new Client() {};
                user.setFirstName(rs.getString("firstName"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                //user.setRole(rs.getString("role"));
                users.add(user);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, "Database error during fetching all users", ex);
        }
        for (Client user : users) {
            System.out.println("Username: " + user.getFirstName());
            System.out.println("Email: " + user.getEmail());
            System.out.println("Password: " + user.getPassword());
            System.out.println("------------------------");
        }

        //return users;
     }
     
    public void delete(String username) {
        Connection connection = DBConfig.getConnection();
        try {
        PreparedStatement pr = connection.prepareStatement("DELETE FROM users WHERE username = ?");
        pr.setString(1, username);
            pr.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
