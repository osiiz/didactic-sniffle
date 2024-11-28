package org.example.p5_grafico;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Scanner;

public class Cliente extends UnicastRemoteObject implements InterfazCliente{

    private String username;
    public static void main(String[] args) throws  RemoteException{
        new Cliente().start();
    }
    Cliente() throws RemoteException {
        super();
    }

    public String getName() {
        return username;
    }

    @Override
    public void sendMessage(InterfazCliente client, String msg) throws RemoteException{
        System.out.println("[+]" + client.getName() + ": " + msg);
    }

    public void start() {
        // Configurar el callback de login
        ControllerLogin.setOnLoginCallback(() -> {
            username = ControllerLogin.getUsername(); // Obtener el username
            iniciarCliente(); // Continuar con la lógica después del login
        });

        // Llamar a VentanaLogin en un nuevo hilo
        new Thread(() -> VentanaLogin.launch(VentanaLogin.class)).start();
    }

    public void iniciarCliente() {
        String registryURL = "rmi://localhost:" + Servidor.PORT + "/p2p";

        try {
            InterfazServidor p2p = (InterfazServidor) Naming.lookup(registryURL);
            System.out.println("Lookup completed " + p2p);
            p2p.connect(this, username, "password");

            List<InterfazCliente> clients = p2p.listClients(this);
            System.out.println(clients);
            for (InterfazCliente client : clients) {
                client.sendMessage(this, "Hola");
            }

        } catch (Exception e) {
            System.out.println("Exception in Cliente: " + e);
            e.printStackTrace();
        }
    }

    public void recibirClientes(){

    }
}

