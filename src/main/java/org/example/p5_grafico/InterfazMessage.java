package org.example.p5_grafico;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.Timestamp;

public interface InterfazMessage extends Remote {
    String getFrom() throws RemoteException;
    String getTo() throws RemoteException;
    Timestamp getTimestamp() throws RemoteException;
    String getContent() throws RemoteException;
}
