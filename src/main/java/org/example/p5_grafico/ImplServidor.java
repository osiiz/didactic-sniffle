package org.example.p5_grafico;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
public class ImplServidor extends UnicastRemoteObject implements InterfazServidor {

    private Map<String, InterfazCliente> clients;

    public ImplServidor() throws RemoteException {
        super();
        clients = new HashMap<>();
    }
    @Override
    public void connect(InterfazCliente cliente, String username, String password) throws RemoteException {
        clients.put(username, cliente);
        System.out.println("[+]Client \"" + username + "\" connected.");
    }

    @Override
    public List<InterfazCliente> listClients(InterfazCliente client) throws RemoteException {
        if (!clients.containsKey(client.getName())) {
            return List.of();
        }
        List<InterfazCliente> r = new ArrayList<>();
        for (Map.Entry<String, InterfazCliente> entry: clients.entrySet()) {
            if (entry.getKey().equals(client.getName())) {
                continue;
            }
            r.add(entry.getValue());
        }
        return r;
    }
}

