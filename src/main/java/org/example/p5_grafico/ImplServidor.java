package org.example.p5_grafico;

import javafx.application.Platform;
import org.example.p5_grafico.db.ClientRepository;
import org.example.p5_grafico.db.Message;
import org.example.p5_grafico.db.MessageRepository;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
public class ImplServidor extends UnicastRemoteObject implements InterfazServidor {

    private Map<String, InterfazCliente> clients;
    private ClientRepository clientRepo;
    private MessageRepository msgRepo;


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
                //System.out.println("Notifying connection to " + c.getName());
                c.notifyConnection(username);
            }
            clients.put(username, cliente);
            System.out.println("[+]Client \"" + username + "\" connected.");
            //status();
            return true;
        }
        return false;
    }

    @Override
    public List<InterfazCliente> listClients(InterfazCliente client) throws RemoteException {
        if (!clients.containsKey(client.getName())) {
            return List.of();
        }
        List<InterfazCliente> r = new ArrayList<>();
        //status();
        for (Map.Entry<String, InterfazCliente> entry: clients.entrySet()) {
            if (entry.getKey().equals(client.getName())) {
                continue;
            }
            r.add(entry.getValue());
            System.out.println(entry.getKey());
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

    public void status() {
        System.out.println("[+] Status:\n");
        for (Map.Entry<String, InterfazCliente> e : this.clients.entrySet()) {
            System.out.println(String.format("\t[+] %s\n", e.getKey()));
        }
    }
}

