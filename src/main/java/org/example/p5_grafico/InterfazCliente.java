package org.example.p5_grafico;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfazCliente extends Remote {

    String getName() throws RemoteException;
    void sendMessage(InterfazCliente client, String msg) throws RemoteException;
}
