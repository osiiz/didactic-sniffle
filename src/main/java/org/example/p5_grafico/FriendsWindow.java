package org.example.p5_grafico;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class FriendsWindow extends Application {
    private Cliente client;
    private InterfazServidor servidor;

    public FriendsWindow(Cliente client, InterfazServidor servidor) {
        this.client = client;
        this.servidor = servidor;
    }

    @Override
    public void start(Stage stage) {
        ObservableList<String> friends = FXCollections.observableArrayList(client.getFriends());

        // ListView para mostrar los amigos
        ListView<String> listView = new ListView<>(friends);
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Botón para eliminar amigo seleccionado
        Button btnRemove = new Button("Eliminar");
        btnRemove.setDisable(true);
        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            btnRemove.setDisable(newSelection == null);
        });

        btnRemove.setOnAction(e -> {
            String seleccionado = listView.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                friends.remove(seleccionado); // Eliminar el amigo de la lista
                client.removeFriend(seleccionado); // Eliminar el amigo de la base de datos
            }
        });

        // Contenedor para el botón (opcional, para diseño)
        HBox botones = new HBox(10, btnRemove);
        botones.setStyle("-fx-padding: 10; -fx-alignment: center;");

        // Diseño principal con BorderPane
        BorderPane root = new BorderPane();
        root.setCenter(listView);
        root.setBottom(botones);

        // Escena y configuración de la ventana
        Scene scene = new Scene(root, 300, 400);
        stage.setTitle("Lista de Amigos");
        stage.setScene(scene);
        stage.show();
    }
}

