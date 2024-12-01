package org.example.p5_grafico;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
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
                    //item.setMaxWidth(Region.USE_PREF_SIZE);
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
                    int charNombre = msg.getFrom().length() + 4; // 4 = 2 corchetes + 2 puntos + 1 espacio
                    Label mensaje = new Label("[" + msg.getFrom() + "]: " + dividirEnLineas(msg.getContent(), 50, charNombre));

                    if (msg.getFrom().equals(client.getName())) {
                        lvMsgs.getItems().add(rightMessage(mensaje));
                    } else {
                        lvMsgs.getItems().add(leftMessage(mensaje));
                    }
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
                int charNombre = client.getName().length() + 4; // 4 = 2 corchetes + 2 puntos + 1 espacio
                Label mensaje = new Label("[" + client.getName() + "]: " + dividirEnLineas(content, 50, charNombre));
                lvMsgs.getItems().add(rightMessage(mensaje));
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
        String content = msg.getContent();
        int charNombre = msg.getFrom().length() + 4; // 4 = 2 corchetes + 2 puntos + 1 espacio
        Label mensaje = new Label("[" + msg.getFrom() + "]: " + dividirEnLineas(content, 50, charNombre));
        lvMsgs.getItems().add(leftMessage(mensaje));
        lvMsgs.refresh();
        lvMsgs.scrollTo(lvMsgs.getItems().size() - 1);
    }

    public void updateUsers() {
        cbUsers.getItems().clear();
        cbUsers.getItems().addAll(client.getFriends());
    }

    public static String dividirEnLineas(String texto, int ancho, int charNombre) {
        StringBuilder resultado = new StringBuilder();
        int longitud = texto.length();
        int anchoFirstLine = ancho - charNombre;

        for (int i = 0; i < longitud; i += ancho) {
            if (i == 0) {
                if (i + anchoFirstLine < longitud) {
                    resultado.append(texto, i, i + anchoFirstLine).append("\n");
                } else {
                    resultado.append(texto.substring(i));
                }
            } else if (i + ancho < longitud) {
                resultado.append(texto, i, i + ancho).append("\n");
            } else {
                resultado.append(texto.substring(i));
            }
        }

        return resultado.toString();
    }


    public HBox rightMessage(Label mensaje) {
        mensaje.setStyle("-fx-background-color: lightblue; -fx-padding: 10; -fx-border-radius: 10; -fx-background-radius: 10;");
        // Crear el HBox interno (que se ajusta al contenido)
        HBox hBoxInterno = new HBox(mensaje);
        hBoxInterno.setAlignment(Pos.CENTER_RIGHT); // Alinear el contenido del HBox interno a la derecha
        hBoxInterno.setMaxWidth(Region.USE_PREF_SIZE); // Asegurar que el ancho se ajuste al contenido

        // Crear el HBox externo (que ocupa todo el ancho del ListView)
        HBox hBoxExterno = new HBox(hBoxInterno);
        hBoxExterno.setAlignment(Pos.CENTER_RIGHT); // Forzar que el HBox interno esté alineado a la derecha
        return hBoxExterno;
    }

    public HBox leftMessage(Label mensaje){
        mensaje.setStyle("-fx-background-color: lightgreen; -fx-padding: 10; -fx-border-radius: 10; -fx-background-radius: 10;");

        HBox hBoxInternoIzquierda = new HBox(mensaje);
        hBoxInternoIzquierda.setAlignment(Pos.CENTER_LEFT); // Alinear a la izquierda
        hBoxInternoIzquierda.setMaxWidth(Region.USE_PREF_SIZE);

        HBox hBoxExternoIzquierda = new HBox(hBoxInternoIzquierda);
        hBoxExternoIzquierda.setAlignment(Pos.CENTER_LEFT);
        return hBoxExternoIzquierda;
    }



}
