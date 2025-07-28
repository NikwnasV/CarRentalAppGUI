package DatabaseConfig;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Database {

//    private final String url = "jdbc:mysql://localhost:3306/carrental_db";
//    private final String user = "root";
//    private final String password = "";
    
    private final String url = "jdbc:mysql://upuzhi99houbnisb:7ypBceVPNmV3051izC7X@bounergip6f0rrnu1lek-mysql.services.clever-cloud.com:3306/bounergip6f0rrnu1lek";
    private final String user = "upuzhi99houbnisb";
    private final String password = "7ypBceVPNmV3051izC7X";

    public Database() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
