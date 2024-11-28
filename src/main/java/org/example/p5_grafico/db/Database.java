package org.example.p5_grafico.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static Connection connection;
    private static final String DB_URL = "jdbc:sqlite:p2p.db";
    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(DB_URL);
            } catch (SQLException e) {
                System.out.println("[!] Couldn't connect to database!");
                System.out.println(e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
        }
        return connection;
    }
}
