package org.example.p5_grafico;

import javafx.application.Platform;
import org.example.p5_grafico.db.ClientData;
import org.example.p5_grafico.db.ClientRepository;
import org.example.p5_grafico.db.Message;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.RemoteRef;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Timestamp;
import java.util.*;

public class Cliente extends UnicastRemoteObject implements InterfazCliente {
    private String username;
    private String password;
    private final Map<String, List<InterfazMessage>> chats;
    private final Map<String, InterfazCliente> connectedClients;
    private static Cliente instancia;

    public Cliente() throws RemoteException {
        super();
        this.chats = new HashMap<>();
        this.connectedClients = new HashMap<>();
    }

    public static Cliente Get() {
        if (instancia == null) {
            try {
                instancia = new Cliente();
            } catch (RemoteException e) {
                System.out.println(e);
            }
        }
        return instancia;
    }

    public static void main(String[] args) throws RemoteException {
        Cliente.Get().start();
    }

    public String getName() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public void receiveMessage(InterfazCliente client, InterfazMessage msg) throws RemoteException {
        String name = client.getName();
        if (!this.chats.containsKey(name)) {
            this.chats.put(name, new ArrayList<>());
        }
        this.chats.get(name).add(msg);
        System.out.println("[+]" + name + ": " + msg);
        Platform.runLater(() -> {
            try {
                ControllerLogin.trapallada.receiveMessage(new Message(name, username, new Timestamp(System.currentTimeMillis()), msg.getContent()));
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public Boolean sendMessage(String other, String content) throws RemoteException {
        InterfazServidor server = getServer();
        Message msg = new Message(username, other, new Timestamp(System.currentTimeMillis()), content);
        try {

            // Caso en el que sabemos si esta conectado
            if (this.connectedClients.containsKey(other) && this.connectedClients.get(other) != null) {
                InterfazCliente client = this.connectedClients.get(other);
                client.receiveMessage(this, msg);

                if (!this.chats.containsKey(other)) {
                    this.chats.put(other, new ArrayList<>());
                }
                this.chats.get(other).add(msg);

                return true;
            }
            // Caso si no sabemos que esta conectado(p.e si se conecto antes de nosotros)
            InterfazCliente client = server.isConnected(this, this.password, other);
            if (client != null) {
                this.connectedClients.put(other, client);
                client.receiveMessage(this, msg);

                if (!this.chats.containsKey(other)) {
                    this.chats.put(other, new ArrayList<>());
                }
                this.chats.get(other).add(msg);

                return true;
            }

            // Caso desconectado
            server.saveMessages(this, this.password, List.of(msg));

        } catch (RemoteException e) {
            System.out.println("[!] Error(sendMessage): " + e.getMessage());
            return false;
        }


        if (!this.chats.containsKey(other)) {
            this.chats.put(other, new ArrayList<>());
        }
        this.chats.get(other).add(msg);

        return false;
    }


    public void start() {
        new Thread(() -> VentanaLogin.launch(VentanaLogin.class)).start();
    }


    public InterfazServidor getServer() {
        String registryURL = "rmi://localhost:" + Servidor.PORT + "/p2p";
        try {
            return (InterfazServidor) Naming.lookup(registryURL);
        } catch (Exception e) {
            System.out.println("[!] Error(getServer): " + e.getMessage());
        }
        return null;
    }

    public boolean connect(String username, String password) {
        InterfazServidor server = getServer();
        try {

            boolean status = server.connect(this, username, password);
            if (status) {
                this.username = username;
                this.password = password;
                fillChats();
            }
            return status;
        } catch (Exception e) {
            System.out.println("[!] Error(connect): " + e.getMessage());
        }

        return false;
    }

    private void fillChats() {
        InterfazServidor server = getServer();
        try {
            Set<String> clients = server.listClients(this);
            for (String c : clients) {
                List<InterfazMessage> msgs = server.getChat(this, this.password, c);
                this.chats.put(c, msgs);
            }
        } catch (RemoteException e) {
            System.out.println("[!] Error(fillchats): " + e.getMessage());
        }
    }


    public List<InterfazMessage> getChatFrom(String username) {
        return this.chats.get(username);
    }

    public List<String> getFriends() {
        return this.chats.keySet().stream().toList();
    }

    public void saveMessages(List<InterfazMessage> msgs) {
        try {
            getServer().saveMessages(this, this.password, msgs);
        } catch (RemoteException e) {
            System.out.println("[!] Error(saveMessages): " + e.getMessage());
        }
    }

    @Override
    public void notifyConnection(InterfazCliente other, String username) throws RemoteException {
        if (!this.chats.containsKey(username)) {
            List<InterfazMessage> msgs = getServer().getChat(this, this.password, username);
            this.chats.put(username, msgs);
        }

        if (!this.connectedClients.containsKey(username) || this.connectedClients.get(username) == null) {
           this.connectedClients.put(username, other);
        }

        // TODO: Igual poner que se abra una ventana en la gui para avisar

    }

    @Override
    public void notifyDisconnection(InterfazCliente other) throws RemoteException {
        this.connectedClients.remove(other.getName());
    }

    // TODO: Usar esta funcion cuando se le da a la X o cuando se hace exit
    public void disconnect(List<InterfazMessage> liveMsgs) {
        try {
            getServer().saveMessages(this, this.password, liveMsgs);
            getServer().disconnect(this, this.password); // Notifica que se desconecta al cliente
            System.exit(0);
        } catch (RemoteException e) {
            System.out.println("[!] Error(disconnect): " + e.getMessage());
        }
    }
    public void sendFriendRequest(String other) {
        InterfazServidor server = getServer();
        try {
            server.sendFriendRequest(this, this.password, other);
        } catch (RemoteException e) {
            System.out.println("[!] Error(sendFriendRequest): " + e.getMessage());
        }
    }


    public Set<String> getPendingRequests() {
        try {
            return getServer().getFriendRequests(this, this.password);
        } catch(RemoteException e) {
            System.out.println("[!] Error(getPendingRequests): " + e.getMessage());
        }
        return Set.of();
    }

    public void removeFriend(String other) {
        try {
            getServer().removeFriends(this, this.password, other);
        } catch(RemoteException e) {
            System.out.println("[!] Error(removeFriend): " +  e.getMessage());
        }
    }

    public void acceptFriendRequest(String other) {
        try {
            if (getPendingRequests().contains(other)) {
                getServer().acceptFriendRequest(this, this.password, other);
            }
        } catch(RemoteException e) {
            System.out.println("[!] Error(acceptFriendRequest): " +  e.getMessage());
        }
    }
}

