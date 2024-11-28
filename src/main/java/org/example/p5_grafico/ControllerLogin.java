package org.example.p5_grafico;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ControllerLogin {
    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPass;

    @FXML
    private Button btnEnter;

    @FXML
    private Button btnExit;

    private static String username;
    private static Runnable onLoginCallback;

    public static void setOnLoginCallback(Runnable callback) {
        onLoginCallback = callback; // Registrar el callback
    }

    public static String getUsername() {
        return username; // Obtener el username ingresado
    }

    @FXML
    private void initialize() {
        btnEnter.setOnAction(event -> {
            username = txtUsername.getText();
            if (onLoginCallback != null) {
                onLoginCallback.run(); // Ejecutar el callback
            }
            closeWindow(); // Cerrar la ventana
        });

        btnExit.setOnAction(event -> closeWindow());
    }

    private void closeWindow() {
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
    }

}