package org.example.p5_grafico;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.p5_grafico.db.ClientData;
import org.example.p5_grafico.db.ClientRepository;

public class ControllerMsgGui {
    private String username;
    private ClientRepository clientRepository = new ClientRepository();

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
        String[] clients = clientRepository.getClients().stream().map(ClientData::getUsername).toArray(String[]::new);
        cbUsers.getItems().addAll(clients);

    }

}
