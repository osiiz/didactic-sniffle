package org.example.p5_grafico.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FriendRequestRepository {
    public FriendRequestRepository() {}

    void addFriendRequest(FriendRequest friendRequest) {
        Connection connection = Database.getConnection();
        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO friend_requests(time, from_client, to_client) VALUES (?, ?, ?)");
            stmt.setTimestamp(1, friendRequest.getTimestamp());
            stmt.setString(2, friendRequest.getFrom());
            stmt.setString(3, friendRequest.getTo());
            stmt.executeUpdate();
            stmt.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
    List<FriendRequest> getFriendRequestsFromClient(String client) {
        Connection connection = Database.getConnection();
        List<FriendRequest> friendRequests = new ArrayList<>();

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM friend_requests WHERE from_client=?");
            stmt.setString(1, client);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                FriendRequest rq = new FriendRequest(rs.getTimestamp("time"),rs.getString("from_client"),rs.getString("to_client"));
                friendRequests.add(rq);
            }
            stmt.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return friendRequests;
    }

    void removeFriendRequest(FriendRequest friendRequest) {
        Connection connection = Database.getConnection();
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM friend_requests WHERE from_client=? and time=?");
            stmt.setString(1, friendRequest.getFrom());
            stmt.setTimestamp(2, friendRequest.getTimestamp());
            stmt.executeUpdate();
            stmt.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    List<String> getFriendsFrom(String client) {
        Connection connection = Database.getConnection();
        List<String> friends = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT friend FROM friends WHERE id like ?");
            stmt.setString(1, client);
            ResultSet set = stmt.executeQuery();
            while(set.next()) {
                friends.add(set.getString("friend"));
            }
            stmt.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return friends;
    }
}
