package org.example.p5_grafico;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class VentanaLogin extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        URL url = VentanaLogin.class.getResource("login.fxml");
        System.out.println(url);
        FXMLLoader fxmlLoader = new FXMLLoader(VentanaLogin.class.getResource("login.fxml"));
        Pane root = fxmlLoader.load();
        Scene scene = new Scene(root);

        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}