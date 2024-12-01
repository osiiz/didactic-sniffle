package org.example.p5_grafico;
import org.example.p5_grafico.db.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Timestamp;
import java.util.*;

public class ImplServidor extends UnicastRemoteObject implements InterfazServidor {

    private final Map<String, InterfazCliente> clients;
    private final ClientRepository clientRepo;
    private final MessageRepository msgRepo;
    private final FriendRequestRepository frRepo;


    public ImplServidor() throws RemoteException {
        super();
        clients = new HashMap<>();
        clientRepo = new ClientRepository();
        msgRepo = new MessageRepository();
        frRepo = new FriendRequestRepository();
    }

    @Override
    public Set<String> listAllClients(InterfazCliente cliente) throws RemoteException{
        if (!clients.containsKey(cliente.getName())) {
            return Set.of();
        }
        return new HashSet<>(clientRepo.getClients().stream().map(ClientData::getUsername).toList());
    }

    @Override
    public boolean connect(InterfazCliente cliente, String username, String password) throws RemoteException {
        if (clientRepo.verifyClient(username, password)) {
            List<String> friends = frRepo.getFriendsFrom(username);
            for (InterfazCliente c: clients.values()) {
                if (friends.contains(c.getName())) {
                    c.notifyConnection(cliente, username);
                }
            }

            clients.put(username, cliente);
            System.out.println("[+] Client \"" + username + "\" connected.");
            return true;
        }
        return false;
    }

    @Override
    public boolean register(InterfazCliente cliente, String username, String password) throws RemoteException {
        for (ClientData c : clientRepo.getClients()) {
           if (c.getUsername().equals(username)) {
               return false;
           }
        }
        clientRepo.registerClient(username, password);
        return true;
    }

    @Override
    public void disconnect(InterfazCliente cliente, String password) throws RemoteException {
        if(!clientRepo.verifyClient(cliente.getName(), password) || !clients.containsKey(cliente.getName())) {
            return;
        }
        clients.remove(cliente.getName());
        List<String> friends = frRepo.getFriendsFrom(cliente.getName());
        for (InterfazCliente c: clients.values()) {
            if (friends.contains(c.getName())) {
                c.notifyDisconnection(cliente);
            }
        }
        System.out.println("[-] Client \"" + cliente.getName() + "\" disconnected.");
    }

    @Override
    public Set<String> listClients(InterfazCliente client) throws RemoteException {
        if (!clients.containsKey(client.getName())) {
            return Set.of();
        }
        return new HashSet<>(frRepo.getFriendsFrom(client.getName()));
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

    @Override
    public void sendFriendRequest(InterfazCliente client, String password, String other) throws RemoteException {
        if(!clientRepo.verifyClient(client.getName(), password) || !clients.containsKey(client.getName())) {
            return;
        }
        frRepo.addFriendRequest(new FriendRequest(new Timestamp(System.currentTimeMillis()), client.getName(), other));
    }

    @Override
    public Set<String> getFriendRequests(InterfazCliente client, String password) throws RemoteException {
        if(!clientRepo.verifyClient(client.getName(), password) || !clients.containsKey(client.getName())) {
            return Set.of();
        }
        List<FriendRequest> requests = frRepo.getFriendRequestsToClient(client.getName());
        Set<String> result = new HashSet<>();
        for (FriendRequest fr : requests) {
            result.add(fr.getFrom());
        }
        return result;
    }

    @Override
    public void removeFriends(InterfazCliente client, String password, String other) throws RemoteException {
        if(!clientRepo.verifyClient(client.getName(), password) || !clients.containsKey(client.getName())) {
            return;
        }
        frRepo.removeFriends(client.getName(), other);
    }


    @Override
    public void acceptFriendRequest(InterfazCliente client, String password, String other) throws RemoteException {
        if(!clientRepo.verifyClient(client.getName(), password) || !clients.containsKey(client.getName())) {
            return;
        }
        List<FriendRequest> requests = frRepo.getFriendRequestsToClient(client.getName());
        FriendRequest fr = null;
        for (FriendRequest request: requests) {
            if (request.getFrom().equals(other)) {
                fr = request;
                break;
            }
        }
        if (fr == null) {
            return;
        }
        frRepo.removeFriendRequest(fr);
        frRepo.addFriend(client.getName(), other);
    }

    @Override
    public boolean updatePassword(InterfazCliente cliente, String password, String newPassword) throws RemoteException {
        if(!clientRepo.verifyClient(cliente.getName(), password) || !clients.containsKey(cliente.getName())) {
            return false;
        }
        clientRepo.updatePassword(cliente.getName(), password, newPassword);
        return true;
    }
}

