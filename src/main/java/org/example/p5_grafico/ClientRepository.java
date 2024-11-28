package org.example.p5_grafico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClientRepository {
    // TODO: Obviamente la clave no deberia estar aqui
    public static final String ENCRYPTION_KEY = "mango";

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

    /**
     * No checkea si ya existe un cliente con ese nombre!
     * @param username nombre del cliente
     * @param password contrasena del cliente
     */
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
    // TODO hashear la password
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
}
