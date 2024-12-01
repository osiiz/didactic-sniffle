package org.example.p5_grafico;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Set;

public class RequestsWindow extends Application {
    // Lista de peticiones de amistad existentes
    private ObservableList<String> peticiones;

    // Lista de usuarios disponibles para enviar solicitudes
    private ObservableList<String> usuariosDisponibles;
    private Cliente client;
    private InterfazServidor servidor;

    public RequestsWindow(Cliente client, InterfazServidor servidor) {
        this.peticiones = FXCollections.observableArrayList(client.getPendingRequests());
        this.client = client;
    }

    @Override
    public void start(Stage stage) {
        // ListView para mostrar las peticiones
        ListView<String> listViewPeticiones = new ListView<>(peticiones);

        // Personalizar las celdas para incluir botones de "Aceptar" y "Rechazar"
        listViewPeticiones.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Texto de la petición
                    Label label = new Label(item);

                    // Botón para aceptar la petición
                    Button btnAceptar = new Button("Aceptar");
                    btnAceptar.setOnAction(e -> {
                        peticiones.remove(item);
                        System.out.println("Aceptada: " + item);
                        client.acceptFriendRequest(item);
                    });

                    // Botón para rechazar la petición
                    Button btnRechazar = new Button("Rechazar");
                    btnRechazar.setOnAction(e -> {
                        peticiones.remove(item);
                        System.out.println("Rechazada: " + item);
                    });

                    // Contenedor para los botones
                    HBox acciones = new HBox(10, btnAceptar, btnRechazar);
                    acciones.setStyle("-fx-alignment: center-right;");

                    // Contenedor para toda la celda
                    HBox celda = new HBox(10, label, acciones);
                    celda.setStyle("-fx-alignment: center-left; -fx-padding: 5;");

                    setGraphic(celda);
                }
            }
        });

        // ListView para mostrar los usuarios disponibles
        ListView<String> listViewUsuarios = new ListView<>();

        // Personalizar las celdas para incluir botón de "Enviar Solicitud"
        listViewUsuarios.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Texto del usuario
                    Label label = new Label(item);

                    // Botón para enviar solicitud
                    Button btnEnviarSolicitud = new Button("Enviar Solicitud");
                    btnEnviarSolicitud.setOnAction(e -> {
                        peticiones.add(item + " te envió una solicitud");
                        System.out.println("Solicitud enviada a: " + item);
                    });

                    // Contenedor para la celda
                    HBox celda = new HBox(10, label, btnEnviarSolicitud);
                    celda.setStyle("-fx-alignment: center-left; -fx-padding: 5;");

                    setGraphic(celda);
                }
            }
        });

        // Campo de texto para buscar usuarios
        TextField txtBuscar = new TextField();
        txtBuscar.setPromptText("Buscar usuarios...");
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            // Filtrar usuarios disponibles
            listViewUsuarios.setItems(usuariosDisponibles.filtered(user ->
                    user.toLowerCase().contains(newValue.toLowerCase())
            ));
        });

        // Diseño para la búsqueda
        VBox panelBusqueda = new VBox(10, txtBuscar, listViewUsuarios);
        panelBusqueda.setStyle("-fx-padding: 10;");

        // Diseño principal
        BorderPane root = new BorderPane();
        root.setCenter(listViewPeticiones);
        root.setRight(panelBusqueda);
        root.setStyle("-fx-padding: 10;");

        // Escena y configuración de la ventana
        Scene scene = new Scene(root, 600, 400);
        stage.setTitle("Peticiones de Amistad");
        stage.setScene(scene);
        stage.show();
    }
}
