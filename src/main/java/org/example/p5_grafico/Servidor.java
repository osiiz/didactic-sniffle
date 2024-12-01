package org.example.p5_grafico;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


public class Servidor extends UnicastRemoteObject {
    public static final int PORT = 7777;

    public Servidor() throws RemoteException {
        super();
    }

    public static void main(String[] args) throws RemoteException{
        new Servidor().start();
    }
    public void start() {
        try{
            startRegistry();
            String registryURL = "rmi://localhost:" + PORT + "/p2p";
            ImplServidor sv = new ImplServidor();
            Naming.rebind(registryURL, sv);
            listRegistry(registryURL);
            System.out.println("[+]Server ready");
        }
        catch (Exception re) {
            System.out.println("[!]Exception: " + re);
        }

    }

    private void startRegistry() throws RemoteException{
        try {
            Registry registry = LocateRegistry.getRegistry(Servidor.PORT);
            registry.list();  // This call will throw an exception if the registry does not already exist
        }
        catch (RemoteException e) {
            Registry registry = LocateRegistry.createRegistry(Servidor.PORT);
        }
    }

    // This method lists the names registered with a Registry object
    private void listRegistry(String registryURL) throws RemoteException, MalformedURLException {
        System.out.println("[.]Registry " + registryURL + " contains: ");
        String[] names = Naming.list(registryURL);
        for (String name : names) System.out.println(name);
    }
}

