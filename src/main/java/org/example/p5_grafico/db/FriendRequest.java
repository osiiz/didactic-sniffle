package org.example.p5_grafico.db;

import java.sql.Timestamp;

public class FriendRequest {
    private final String from;
    private final String to;
    private final Timestamp timestamp;
    public FriendRequest(Timestamp timestamp,String from, String to) {
        this.from = from;
        this.to = to;
        this.timestamp = timestamp;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "FriendRequest{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
