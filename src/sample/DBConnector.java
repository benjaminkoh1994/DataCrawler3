package sample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnector {

    public static Connection getConnection() throws SQLException {
        // Using SQLite Java Database Connector to connect to DBMS
        String connURL = "jdbc:sqlite:dataCrawler.db";
        Connection conn = DriverManager.getConnection(connURL);
        return conn;
    }
}
