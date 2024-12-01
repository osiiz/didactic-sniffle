package org.example.p5_grafico;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.example.p5_grafico.db.ClientData;
import org.example.p5_grafico.db.ClientRepository;
import org.example.p5_grafico.db.Message;
import org.example.p5_grafico.db.MessageRepository;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ControllerMsgGui {
    private ClientRepository clientRepository = new ClientRepository();
    private MessageRepository messageRepository = new MessageRepository();
    private List<InterfazMessage> liveMsgs; // Mensajes que se envian mientras el cliente esta activo. Estos luego se irian a la BD
    private Cliente client; // Cliente al que se le esta enviando

    @FXML
    private TextField txtMsg;
    @FXML
    private Button btnSend;
    @FXML
    private Button btnExit;
    @FXML
    private ChoiceBox<String> cbUsers;
    @FXML
    private ListView<HBox> lvMsgs;
    @FXML
    private Label txtWelcome;
    @FXML
    public void initialize() {
    }


    public void initializeData(Cliente client) {
        this.client = client;
        liveMsgs = new ArrayList<>();
        lvMsgs.setCellFactory(param -> new ListCell<HBox>() {
            @Override
            protected void updateItem(HBox item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    setGraphic(item);
                }
            }
        });

        txtWelcome.setText("Bienvenido, " + client.getName() + "!");
        cbUsers.getItems().addAll(client.getFriends());

        cbUsers.setOnShowing(event -> {
            cbUsers.getItems().clear();
            cbUsers.getItems().addAll(client.getFriends());
            System.out.println(client.getFriends());
        });

        cbUsers.setOnAction(event -> {
            try {
                System.out.println(cbUsers.getValue());
                if (cbUsers.getValue() == null) {
                    return;
                }

                List<InterfazMessage> msgs = client.getChatFrom(cbUsers.getValue());
                lvMsgs.getItems().clear();
                for (InterfazMessage msg : msgs) {
                    HBox msgBox;
                    if (msg.getFrom().equals(client.getName())) {
                        msgBox = new HBox(new Label("[" + msg.getFrom() + "]: " + msg.getContent()));
                        msgBox.setAlignment(Pos.CENTER_RIGHT);
                    } else {
                        msgBox = new HBox(new Label("[" + msg.getFrom() + "]: " + msg.getContent()));
                        msgBox.setAlignment(Pos.CENTER_LEFT);
                    }
                    lvMsgs.getItems().add(msgBox);
                }
                lvMsgs.scrollTo(msgs.size() - 1);
            } catch (RemoteException e) {
                System.out.println(e.getMessage());
            }
        });

        btnSend.setOnAction(event -> {
            String to = cbUsers.getValue();
            String content = txtMsg.getText();
            if (to == null || content.isEmpty()) {
                return;
            }
            Boolean enviado = null;
            try {
                enviado = client.sendMessage(cbUsers.getValue(), txtMsg.getText());
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
            if (enviado){
                HBox msgBox = new HBox(new Label("[" + client.getName() + "]: " + content));
                msgBox.setAlignment(Pos.CENTER_RIGHT);
                lvMsgs.getItems().add(msgBox);
                try {
                    liveMsgs.add(new Message(client.getName(), to, new Timestamp(System.currentTimeMillis()), content));
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
                lvMsgs.scrollTo(lvMsgs.getItems().size() - 1);
            }
            txtMsg.clear();
        });

        btnExit.setOnAction(event -> {
            client.saveMessages(liveMsgs);
            System.exit(0);
        });
    }


    public void receiveMessage(Message msg) {
        HBox msgBox;
        msgBox = new HBox(new Label("[" + msg.getTo() + "]: " + msg.getContent()));
        System.out.println("[" + msg.getTo() + "]: " + msg.getContent());
        msgBox.setAlignment(Pos.CENTER_LEFT);
        lvMsgs.getItems().add(msgBox);
        lvMsgs.refresh();
        lvMsgs.scrollTo(lvMsgs.getItems().size() - 1);
    }

    public void updateUsers() {
        cbUsers.getItems().clear();
        cbUsers.getItems().addAll(client.getFriends());
    }


}
