package org.example.p5_grafico;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ChangePassWindow extends Application{
    private final Cliente client;

    public ChangePassWindow(Cliente client){
        this.client = client;
    }

    @Override
    public void start(Stage stage) {
        // Etiquetas y campos de texto
        Label lblContrasenaActual = new Label("Contraseña Actual:");
        PasswordField txtContrasenaActual = new PasswordField();
        txtContrasenaActual.setPromptText("Ingrese su contraseña actual");

        Label lblNuevaContrasena = new Label("Nueva Contraseña:");
        PasswordField txtNuevaContrasena = new PasswordField();
        txtNuevaContrasena.setPromptText("Ingrese la nueva contraseña");

        Label lblConfirmarContrasena = new Label("Confirmar Contraseña:");
        PasswordField txtConfirmarContrasena = new PasswordField();
        txtConfirmarContrasena.setPromptText("Confirme la nueva contraseña");

        // Botón para guardar cambios
        Button btnGuardar = new Button("Guardar Cambios");
        btnGuardar.setDisable(true); // Deshabilitado hasta que los campos sean válidos

        // Etiqueta para mostrar mensajes al usuario
        Label lblMensaje = new Label();

        // Listener para habilitar/deshabilitar el botón
        txtNuevaContrasena.textProperty().addListener((obs, oldText, newText) ->
                validarCampos(txtContrasenaActual, txtNuevaContrasena, txtConfirmarContrasena, btnGuardar)
        );
        txtConfirmarContrasena.textProperty().addListener((obs, oldText, newText) ->
                validarCampos(txtContrasenaActual, txtNuevaContrasena, txtConfirmarContrasena, btnGuardar)
        );
        txtContrasenaActual.textProperty().addListener((obs, oldText, newText) ->
                validarCampos(txtContrasenaActual, txtNuevaContrasena, txtConfirmarContrasena, btnGuardar)
        );

        // Acción del botón Guardar
        btnGuardar.setOnAction(e -> {
            if (txtNuevaContrasena.getText().equals(txtConfirmarContrasena.getText())
                    && client.updatePassword(txtNuevaContrasena.getText())) {
                lblMensaje.setText("Contraseña cambiada exitosamente.");
                lblMensaje.setStyle("-fx-text-fill: green;");
            } else {
                lblMensaje.setText("Ha ocurrido un error fatal o las contraseñas no coinciden.");
                lblMensaje.setStyle("-fx-text-fill: red;");
            }
        });

        // Diseño de la ventana
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(lblContrasenaActual, 0, 0);
        grid.add(txtContrasenaActual, 1, 0);
        grid.add(lblNuevaContrasena, 0, 1);
        grid.add(txtNuevaContrasena, 1, 1);
        grid.add(lblConfirmarContrasena, 0, 2);
        grid.add(txtConfirmarContrasena, 1, 2);

        HBox botones = new HBox(10, btnGuardar);
        botones.setPadding(new Insets(10, 0, 0, 0));
        grid.add(botones, 1, 3);

        grid.add(lblMensaje, 1, 4);

        // Configuración de la escena
        Scene scene = new Scene(grid, 400, 250);
        stage.setTitle("Cambiar Contraseña");
        stage.setScene(scene);
        stage.show();
    }

    private void validarCampos(PasswordField txtContrasenaActual, PasswordField txtNuevaContrasena,
                               PasswordField txtConfirmarContrasena, Button btnGuardar) {
        boolean esValido = !txtContrasenaActual.getText().isEmpty()
                && !txtNuevaContrasena.getText().isEmpty()
                && !txtConfirmarContrasena.getText().isEmpty();
        btnGuardar.setDisable(!esValido);
    }
}
