package org.example.p5_grafico;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ControllerMsgGui {
    private String username;

    @FXML
    private TextField txtMsg;
    @FXML
    private Button btnSend;
    @FXML
    private Button btnExit;
    @FXML
    private ChoiceBox<String> cbUsers;
    @FXML
    private ListView<String> lvMsgs;
    @FXML
    private Label txtWelcome;

    public void initializeData(String username) {
        this.username = username;
        txtWelcome.setText("Bienvenido, " + username + "!");
    }

}
