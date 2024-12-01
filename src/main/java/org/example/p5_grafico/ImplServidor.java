package org.example.p5_grafico;

import javafx.application.Platform;
import org.example.p5_grafico.db.ClientData;
import org.example.p5_grafico.db.ClientRepository;
import org.example.p5_grafico.db.Message;
import org.example.p5_grafico.db.MessageRepository;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class ImplServidor extends UnicastRemoteObject implements InterfazServidor {

    private final Map<String, InterfazCliente> clients;
    private final ClientRepository clientRepo;
    private final MessageRepository msgRepo;


    public ImplServidor() throws RemoteException {
        super();
        clients = new HashMap<>();
        clientRepo = new ClientRepository();
        msgRepo = new MessageRepository();
    }

    @Override
    public boolean connect(InterfazCliente cliente, String username, String password) throws RemoteException {
        if (clientRepo.verifyClient(username, password)) {
            for (InterfazCliente c: clients.values()) {
                c.notifyConnection(cliente, username);
            }

            clients.put(username, cliente);
            System.out.println("[+] Client \"" + username + "\" connected.");
            return true;
        }
        return false;
    }

    @Override
    public void disconnect(InterfazCliente cliente, String password) throws RemoteException {
        if(!clientRepo.verifyClient(cliente.getName(), password) || !clients.containsKey(cliente.getName())) {
            return;
        }
        clients.remove(cliente.getName());
        for (InterfazCliente c: clients.values()) {
            c.notifyDisconnection(cliente);
        }
        System.out.println("[-] Client \"" + cliente.getName() + "\" disconnected.");
    }

    @Override
    public Set<String> listClients(InterfazCliente client) throws RemoteException {
        if (!clients.containsKey(client.getName())) {
            return Set.of();
        }
        Set<String> r = new HashSet<>();
        for (Map.Entry<String, InterfazCliente> entry: clients.entrySet()) {
            if (entry.getKey().equals(client.getName())) {
                continue;
            }
            r.add(entry.getKey());
        }
        for (ClientData c: clientRepo.getClients()) {
            if (c.getUsername().equals(client.getName())) {
                continue;
            }
            r.add(c.getUsername());
        }
        return r;
    }

    @Override
    public List<InterfazMessage> getChat(InterfazCliente cliente, String password, String username) throws RemoteException {
        if(!clientRepo.verifyClient(cliente.getName(), password) || !clients.containsKey(cliente.getName())) {
            return List.of();
        }
        return msgRepo.getChat(cliente.getName(), username);
    }

    @Override
    public void saveMessages(InterfazCliente cliente, String password, List<InterfazMessage> msgs) throws RemoteException {
        if(!clientRepo.verifyClient(cliente.getName(), password) || !clients.containsKey(cliente.getName())) {
            return;
        }
        msgRepo.saveMessages(msgs);

    }

    @Override
    public InterfazCliente isConnected(InterfazCliente client, String password, String other) throws RemoteException {
        if(!clientRepo.verifyClient(client.getName(), password) || !clients.containsKey(client.getName())) {
            return null;
        }
        return this.clients.get(other);
    }
}

