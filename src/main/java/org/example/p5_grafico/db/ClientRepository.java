package org.example.p5_grafico.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClientRepository {
    public static final String ENCRYPTION_KEY = "o8N5C3e9X7;3P4;2e2&27";

    public ClientRepository() {}

    public List<ClientData> getClients() {
        Connection conn = Database.getConnection();
        List<ClientData> clients = new ArrayList<>();
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT username, password FROM clients");
            ResultSet set = stmt.executeQuery();
            while (set.next()) {
                ClientData clientData = new ClientData(set.getString("username"), set.getString("password"));
                clients.add(clientData);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return clients;
    }


    public void registerClient(String username, String password) {
        Connection conn = Database.getConnection();
        try {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO clients (username, password) VALUES (?, ?)");
            stmt.setString(1, username);
            String hashedPassword = AES.Encrypt(password, ENCRYPTION_KEY);
            stmt.setString(2, hashedPassword);
            stmt.executeUpdate();
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public boolean verifyClient(String username, String password) {
        Connection conn = Database.getConnection();
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT username, password FROM clients WHERE username like ?");
            stmt.setString(1, username);
            ResultSet set = stmt.executeQuery();
            if (set.next()) {
                return set.getString("password").equals(AES.Encrypt(password, ENCRYPTION_KEY));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
    public void updatePassword(String username, String password, String newPassword) {
        Connection conn = Database.getConnection();
        try {
            PreparedStatement stmt = conn.prepareStatement("UPDATE clients set password = ? where username like ? and password like ?");
            stmt.setString(1, AES.Encrypt(newPassword, ENCRYPTION_KEY));
            stmt.setString(2, username);
            stmt.setString(3, AES.Encrypt(password, ENCRYPTION_KEY));
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
