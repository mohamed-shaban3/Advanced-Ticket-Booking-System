package DataBase;

import java.sql.Connection;
import java.sql.DriverManager;

public class DB {
    public static Connection DBConnection() {
        Connection conn = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");  
            conn = DriverManager.getConnection("jdbc:oracle:thin:BOOKING-SYSTEM/123@localhost:1521/XE");
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
        return conn;
    } 
} 