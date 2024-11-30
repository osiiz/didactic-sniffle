package org.example.p5_grafico.db;

public class ClientData {
    private final String username;
    private final String password;
    public ClientData(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "ClientData{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
