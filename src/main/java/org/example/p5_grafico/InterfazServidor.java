package org.example.p5_grafico;

import org.example.p5_grafico.db.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;

public interface InterfazServidor extends Remote {
    boolean connect(InterfazCliente cliente, String username, String password) throws RemoteException;
    void disconnect(InterfazCliente cliente, String password) throws RemoteException;
    Set<String> listClients(InterfazCliente client) throws RemoteException;
    List<InterfazMessage> getChat(InterfazCliente cliente, String password, String other) throws RemoteException;
    void saveMessages(InterfazCliente cliente, String password,List<InterfazMessage> msgs) throws RemoteException;
    InterfazCliente isConnected(InterfazCliente client, String password, String other) throws RemoteException;
}
