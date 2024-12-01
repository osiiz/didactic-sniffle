package org.example.p5_grafico;
import org.example.p5_grafico.db.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfazCliente extends Remote {

    String getName() throws RemoteException;
    void receiveMessage(InterfazCliente client, InterfazMessage msg) throws RemoteException;
    void notifyConnection(InterfazCliente other, String username) throws RemoteException;
    void notifyDisconnection(InterfazCliente other) throws RemoteException;
}
