package org.example.p5_grafico;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class ControllerLogin {
    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPass;
    @FXML
    private Button btnEnter;
    @FXML
    private Button btnExit;
    @FXML
    private Button btnRegister;


    private static String username;
    private static String password;



    public static ControllerMsgGui controllerMsgGui;

    @FXML
    private void initialize() {
        Cliente c = Cliente.Get();
        btnEnter.setOnAction(event -> {
            username = txtUsername.getText();
            password = txtPass.getText();
            if (username.isEmpty() || password.isEmpty()) {
                showErrorAlert();
                return;
            }
            if (c.connect(username, password)) {
                openMainGUI(); // Abrir la ventana principal
                closeWindow(); // Cerrar la ventana
            }else{
                showErrorAlert();
            }
        });

        btnExit.setOnAction(event -> {
            closeWindow();
            System.exit(0);
        });

        btnRegister.setOnAction(event -> {
            username = txtUsername.getText();
            password = txtPass.getText();
            if (username.isEmpty() || password.isEmpty()) {
                showErrorAlert();
                return;
            }
            boolean status = Cliente.Get().register(username, password);
            if (!status) {
                showErrorAlert();
            }
            btnEnter.fire();
        });
    }

    private void openMainGUI() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MsgGui.fxml")); // Archivo FXML de la GUI principal
            Stage stage = new Stage();
            Scene scene = new Scene(loader.load());

            // Obtener el controlador de la GUI principal para pasar datos
            controllerMsgGui = loader.getController();
            controllerMsgGui.setStage(stage);
            controllerMsgGui.initializeData(Cliente.Get()); // Pasar el nombre de usuario

            stage.setTitle("P2P Chat - Principal");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showErrorAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error de Login");
        alert.setHeaderText("Credenciales Incorrectas");
        alert.setContentText("Por favor verifica tu nombre de usuario y contraseña.");
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
    }
}