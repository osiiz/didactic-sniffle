package org.example.p5_grafico;

import javafx.application.Platform;
import org.example.p5_grafico.db.Message;

import java.rmi.Naming;
import java.rmi.RemoteException;
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
                System.out.println("[!]Error (Get): "+ e);
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

    public boolean register(String username, String password){
        boolean status = false;
        try {
            status = getServer().register(this, username, password);
        } catch(RemoteException e) {
            System.out.println(e.getMessage());
        }
        return status;
    }

    @Override
    public void receiveMessage(InterfazCliente client, InterfazMessage msg) throws RemoteException {
        String name = client.getName();
        if (!this.chats.containsKey(name)) {
            this.chats.put(name, new ArrayList<>());
        }
        this.chats.get(name).add(msg);
        Platform.runLater(() -> {
            try {
                ControllerLogin.controllerMsgGui.receiveMessage(new Message(name, username, new Timestamp(System.currentTimeMillis()), msg.getContent()));
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

    public boolean isConnected(String other){
        boolean result = this.connectedClients.containsKey(other) && this.connectedClients.get(other) != null;
        if (result){
            return true;
        }
        try{
            InterfazCliente client = getServer().isConnected(this, this.password, other);
            if (client != null) {
                result = true;
            }
        } catch (RemoteException e) {
            System.out.println("[!] Error(isConnected): " + e.getMessage());
        }
        return result;
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
        if (!this.chats.containsKey(username)){
            try {
                this.chats.put(username, getServer().getChat(this, this.password, username));
            } catch (RemoteException e) {
                System.out.println("[!] Error(getChatFrom): " + e.getMessage());
            }
        }

        return this.chats.get(username);
    }

    public List<String> getFriends() {
        try {
            return new ArrayList<>(getServer().listClients(this));
        } catch (RemoteException e) {
            System.out.println("[!] Error(getFriends): " + e.getMessage());
        }
        return List.of();
    }


    public Set<String> listAllClients(){
        try {
            return getServer().listAllClients(this);
        }catch(RemoteException e)  {
            System.out.println(e.getMessage());
        }
        return new HashSet<>();
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

    }

    @Override
    public void notifyDisconnection(InterfazCliente other) throws RemoteException {
        this.connectedClients.remove(other.getName());
    }

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

    public void removeFriend(String other, List<InterfazMessage> liveMsgs) {
        try {
            getServer().removeFriends(this, this.password, other);
            this.connectedClients.remove(other);
            getServer().saveMessages(this, this.password, liveMsgs);
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
    public boolean updatePassword(String newPassword) {
        try {
           boolean status = getServer().updatePassword(this, this.password, newPassword);
           if (status) {
               this.password = newPassword;
           }
           return status;
        } catch (RemoteException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public Set<String> getConnectedClients() {
        return this.connectedClients.keySet();
    }
}

