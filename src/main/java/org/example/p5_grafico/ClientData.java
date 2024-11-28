package org.example.p5_grafico;

public class ClientData {
    private final String username;
    private final String password;
    ClientData(String username, String password) {
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
