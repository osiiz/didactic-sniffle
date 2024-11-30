package org.example.p5_grafico.db;

import java.sql.Timestamp;

public class Message {
    private final String from;
    private final String to;
    private final Timestamp timestamp;
    private final String content;
    public Message(String from, String to, Timestamp timestamp, String content) {
        this.from = from;
        this.to = to;
        this.timestamp = timestamp;
        this.content = content;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s: %s", timestamp, from, content);
    }
}
