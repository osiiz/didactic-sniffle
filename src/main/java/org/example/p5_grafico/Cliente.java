package org.example.p5_grafico;

import javafx.application.Platform;
import org.example.p5_grafico.db.ClientData;
import org.example.p5_grafico.db.ClientRepository;
import org.example.p5_grafico.db.Message;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Timestamp;
import java.util.*;

public class Cliente extends UnicastRemoteObject implements InterfazCliente {

    private String username;
    private String password;
    private ClientRepository clientRepository = new ClientRepository();
    private ControllerMsgGui ControllerMsgGui = new ControllerMsgGui();

    private Map<String, List<InterfazMessage>> chats;

    private static Cliente instancia;

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

    Cliente() throws RemoteException {
        super();
        this.chats = new HashMap<>();
    }

    public String getName() {
        return username;
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
        Message msg = new Message(username, other, new Timestamp(System.currentTimeMillis()), content);
        try {
            List<InterfazCliente> clients = getServer().listClients(this);
            InterfazCliente client = null;
            for (InterfazCliente c : clients) {
                if (c.getName().equals(other)) {
                    client = c;
                    break;
                }
            }

            if (client == null) {
                return false;
            }
            client.receiveMessage(this, (InterfazMessage) msg);

        } catch (RemoteException e) {
            System.out.println(e.getMessage());
            return false;
        }
        if (!this.chats.containsKey(other)) {
            this.chats.put(other, new ArrayList<>());
        }
        this.chats.get(other).add(msg);
        return true;
    }


    public void start() {
        // Configurar el callback de login
        ControllerLogin.setOnLoginCallback(() -> {
            //username = ControllerLogin.getUsername(); // Obtener el username
        });

        // Llamar a VentanaLogin en un nuevo hilo
        new Thread(() -> VentanaLogin.launch(VentanaLogin.class)).start();
    }


    public InterfazServidor getServer() {
        String registryURL = "rmi://localhost:" + Servidor.PORT + "/p2p";
        try {
            return (InterfazServidor) Naming.lookup(registryURL);
        } catch (Exception e) {
            System.out.println("Exception in Cliente: " + e);
            e.printStackTrace();
        }
        return null;
    }

    public boolean conect(String username, String password) {
        String registryURL = "rmi://localhost:" + Servidor.PORT + "/p2p";
        try {
            InterfazServidor p2p = (InterfazServidor) Naming.lookup(registryURL);
            boolean status = p2p.connect(this, username, password);
            if (status) {
                this.username = username;
                System.out.println("Username>" + this.username);
                this.password = password;
                fillChats();
            }
            return status;
        } catch (Exception e) {
            System.out.println("Exception in Cliente: " + e);
            e.printStackTrace();
        }
        return false;
    }

    private void fillChats() {
        InterfazServidor server = getServer();
        try {
            List<InterfazCliente> clients = server.listClients(this);
            for (InterfazCliente c : clients) {
                List<InterfazMessage> msgs = server.getChat(this, this.password, c.getName());
                System.out.println(msgs);
                this.chats.put(c.getName(), msgs);
            }
        } catch (RemoteException e) {
            System.out.println("Exception in Cliente: " + e);
            e.printStackTrace();
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
            System.out.println("Exception in Cliente: " + e);
            e.printStackTrace();
        }
    }

    @Override
    public void notifyConnection(String other) throws RemoteException {
        if (!this.chats.containsKey(getName())) {
            List<InterfazMessage> msgs = getServer().getChat(this, this.password, other);
            this.chats.put(other, msgs);
        }
        System.out.println("[+]Connection from " + other);
        System.out.println("Amigos>" + getFriends());
    }
}

