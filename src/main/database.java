package main;

import java.sql.Connection;
import java.sql.DriverManager;

// CLASS ΓΙΑ ΣΥΝΔΕΣΗ ΣΤΗΝ ΒΑΣΗ ΔΕΔΟΜΕΝΩΝ.
// mysql-connector-j-8.3.0.jar υπάρχει στο LIBRARIES για την συνδεση.
public class database {
    
    public static Connection connectDb() {
        try {
            String url = "jdbc:mysql://localhost/gym?useSSL=false&serverTimezone=UTC"; // Updated JDBC URL
            String user = "root";
            String password = "";

            // Connect to the database
            Connection connect = DriverManager.getConnection(url, user, password);
            return connect;
        } catch (Exception e) {
            e.printStackTrace();  // Log the exception
        }
        return null;
    }
}
