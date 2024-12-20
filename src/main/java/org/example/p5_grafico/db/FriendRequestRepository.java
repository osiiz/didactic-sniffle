package org.example.p5_grafico.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FriendRequestRepository {
    public FriendRequestRepository() {}

    public void addFriendRequest(FriendRequest friendRequest) {
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

    public List<FriendRequest> getFriendRequestsToClient(String client) {
        Connection connection = Database.getConnection();
        List<FriendRequest> friendRequests = new ArrayList<>();

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM friend_requests WHERE to_client=?");
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

    public void removeFriendRequest(FriendRequest friendRequest) {
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

    public List<String> getFriendsFrom(String client) {
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

    public void removeFriends(String from, String other) {
        Connection connection = Database.getConnection();
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM friends WHERE (id like ? and friend like ?) or (id like ? and friend like ?)");
            stmt.setString(1, from);
            stmt.setString(2, other);
            stmt.setString(3, other);
            stmt.setString(4, from);
            stmt.executeUpdate();
            stmt.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void addFriend(String one, String two) {
        Connection connection = Database.getConnection();
        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO friends(id, friend) VALUES(?,?),(?, ?)");
            stmt.setString(1, one);
            stmt.setString(2, two);
            stmt.setString(3, two);
            stmt.setString(4, one);
            stmt.executeUpdate();
            stmt.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
