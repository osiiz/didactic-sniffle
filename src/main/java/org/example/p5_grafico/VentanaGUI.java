package org.example.p5_grafico;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.io.IOException;

public class VentanaGUI extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(VentanaGUI.class.getResource("MsgGui.fxml"));
        stage.setTitle("Ventana GUI");
        stage.setScene(fxmlLoader.load());
        stage.show();
    }
}
