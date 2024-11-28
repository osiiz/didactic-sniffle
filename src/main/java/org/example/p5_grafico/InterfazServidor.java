package org.example.p5_grafico;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface InterfazServidor extends Remote {
    void connect(InterfazCliente cliente, String username, String password) throws RemoteException;
    List<InterfazCliente> listClients(InterfazCliente client) throws RemoteException;
}
