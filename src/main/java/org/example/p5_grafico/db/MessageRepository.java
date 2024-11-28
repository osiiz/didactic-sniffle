package org.example.p5_grafico.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MessageRepository {
    public MessageRepository() {}

    /**
     *
     * @param client1 username de uno de los clientes
     * @param client2 username del otro cliente
     * @return lista de mensajes entre dos clientes
     */
    public List<Message> getChat(String client1, String client2) {
        Connection conn = Database.getConnection();
        List<Message> messages = new ArrayList<>();
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM messages WHERE (from_client like ? and to_client like ?) or (from_client like ? and to_client like ?)");
            stmt.setString(1, client1);
            stmt.setString(2, client2);
            stmt.setString(3, client2);
            stmt.setString(4, client1);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Message msg = new Message(rs.getString("from_client"), rs.getString("to_client"), rs.getTimestamp("time"), rs.getString("content"))
                messages.add(msg);
            }
            stmt.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public void saveMessage(Message msg) {
        Connection conn = Database.getConnection();
        try {
           PreparedStatement stmt = conn.prepareStatement("INSERT INTO messages(from_client, to_client, time, content) VALUES (?, ?, ?, ?)");
           stmt.setString(1,msg.getFrom());
           stmt.setString(2, msg.getTo());
           stmt.setTimestamp(3, msg.getTimestamp());
           stmt.setString(4, msg.getContent());
           stmt.executeUpdate();
           stmt.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveMessages(List<Message> msgs) {
        for(Message msg : msgs) {
            saveMessage(msg);
        }
    }
}
