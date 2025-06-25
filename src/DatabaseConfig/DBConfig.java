package DatabaseConfig;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConfig {
   public static Connection getConnection() {
       try {
           try {
               Class.forName("com.mysql.cj.jdbc.Driver");
           } catch (ClassNotFoundException ex) {
               Logger.getLogger(DBConfig.class.getName()).log(Level.SEVERE, null, ex);
           }
           return DriverManager.getConnection("jdbc:mysql://localhost:3306/carrental_db?user=root&password=");
           
       } catch (SQLException ex) {
           Logger.getLogger(DBConfig.class.getName()).log(Level.SEVERE, null, ex);
       }
       return null;
   }
}
