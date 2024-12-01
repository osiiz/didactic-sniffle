package org.example.p5_grafico;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.p5_grafico.db.ClientRepository;

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
    private static Runnable onLoginCallback;
    private final ClientRepository clientRepository = new ClientRepository();

    public static void setOnLoginCallback(Runnable callback) {
        onLoginCallback = callback; // Registrar el callback
    }

    public static String getUsername() {
        return username; // Obtener el username ingresado
    }

    public static String getPassword() {return password;}
    public static ControllerMsgGui trapallada;

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
            if (c.conect(username, password)) {
                if (onLoginCallback != null) {
                    onLoginCallback.run(); // Ejecutar el callback
                }
                openMainGUI(); // Abrir la ventana principal
                closeWindow(); // Cerrar la ventana
            }else{
                showErrorAlert();
            }
        });

        btnExit.setOnAction(event -> closeWindow());

        btnRegister.setOnAction(event -> {
            username = txtUsername.getText();
            password = txtPass.getText();
            if (username.isEmpty() || password.isEmpty()) {
                showErrorAlert();
                return;
            }
            clientRepository.registerClient(username, password);
            if (onLoginCallback != null) {
                onLoginCallback.run(); // Ejecutar el callback
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
            trapallada = loader.getController();
            trapallada.initializeData(Cliente.Get()); // Pasar el nombre de usuario

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