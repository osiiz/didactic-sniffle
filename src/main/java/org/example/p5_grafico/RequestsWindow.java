package org.example.p5_grafico;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Set;

public class RequestsWindow extends Application {
    private final ObservableList<String> peticiones;
    private final Cliente client;
    private final Set<String> usuarios;

    public RequestsWindow(Cliente client) {
        this.peticiones = FXCollections.observableArrayList(client.getPendingRequests());
        this.client = client;
        this.usuarios = client.listAllClients();
        this.usuarios.remove(client.getName());
        client.getFriends().forEach(this.usuarios::remove);
    }

    @Override
    public void start(Stage stage) {
        // ListView para mostrar las peticiones
        ListView<String> listViewPeticiones = new ListView<>(peticiones);
        ListView<String> listViewUsuarios = new ListView<>();

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
                        client.acceptFriendRequest(item);
                        usuarios.remove(item);
                        listViewUsuarios.getItems().remove(item);
                    });

                    // Botón para rechazar la petición
                    Button btnRechazar = new Button("Rechazar");
                    btnRechazar.setOnAction(e -> {
                        peticiones.remove(item);
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
        listViewUsuarios.getItems().addAll(usuarios);

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
                        client.sendFriendRequest(item);
                        usuarios.remove(item);
                        listViewUsuarios.getItems().remove(item);
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
            listViewUsuarios.setItems(FXCollections.observableArrayList(usuarios));

            if (newValue.isEmpty()) {
                return;
            }

            ObservableList<String> filtrados = listViewUsuarios.getItems().filtered(c -> c.toLowerCase().contains(newValue.toLowerCase()));
            listViewUsuarios.setItems(filtrados);
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
