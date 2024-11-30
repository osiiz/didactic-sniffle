package org.example.p5_grafico;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.p5_grafico.db.ClientData;
import org.example.p5_grafico.db.ClientRepository;
import org.example.p5_grafico.db.Message;
import org.example.p5_grafico.db.MessageRepository;

import java.util.ArrayList;
import java.util.List;

public class ControllerMsgGui {
    private String username;
    private ClientRepository clientRepository = new ClientRepository();
    private MessageRepository messageRepository = new MessageRepository();
    private List<Message> liveMsgs; // Mensajes que se envian mientras el cliente esta activo. Estos luego se irian a la BD
    private ClientData currentClient; // Cliente al que se le esta enviando
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
        this.liveMsgs = new ArrayList<>();

        txtWelcome.setText("Bienvenido, " + username + "!");
        String[] clients = clientRepository.getClients().stream().map(ClientData::getUsername).toArray(String[]::new);
        cbUsers.getItems().addAll(clients);

        cbUsers.setOnAction(event -> {
            System.out.println(cbUsers.getValue());
            this.currentClient = new ClientData(cbUsers.getValue(), "");
            List<Message> msgs = messageRepository.getChat(username, cbUsers.getValue());
            System.out.println(username + cbUsers.getValue());
            System.out.println(msgs);
            lvMsgs.getItems().clear();
            for (Message msg : msgs) {
                lvMsgs.getItems().add(msg.toString());
            }
            for (Message msg : liveMsgs) {
                lvMsgs.getItems().add(msg.toString());
            }
        });
    }

}
