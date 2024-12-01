package org.example.p5_grafico;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfazCliente extends Remote {

    String getName() throws RemoteException;
    void receiveMessage(InterfazCliente client, InterfazMessage msg) throws RemoteException;
    void notifyConnection(InterfazCliente other, String username) throws RemoteException;
    void notifyDisconnection(InterfazCliente other) throws RemoteException;
}
