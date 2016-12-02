package textchatv2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * FXML Controller class
 *
 * @author lucas.burdell
 */
public class ChatController implements Initializable {

    @FXML
    private TextField textbox;
    @FXML
    private ScrollPane scrollpane;
    @FXML
    private TextFlow textflow;
    @FXML
    private TextField texttemplate;

    private Connection connection;

    private SecuritySolution securitySolution;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    private void showText(String content, boolean sending) {
        Color textColor = sending ? Color.GREEN : Color.BLUE;
        content = (sending ? "You: " : "Them: ") + content;
        Text text = new Text(content + "\n");
        text.setFont(new Font(14));
        text.setFill(textColor);
        textflow.getChildren().add(text);
        textflow.layout();
        scrollpane.layout();
        scrollpane.setVvalue(scrollpane.getVmax());
    }

    @FXML
    private void sendText(ActionEvent event) {
        String textToSend = textbox.getText();
        byte[] message = this.securitySolution.startEncryption(textToSend);
        String sendMessage = new String(message);
        this.connection.sendMessage(sendMessage);
        showText(textToSend, true);
        

// do something with it
                // byte[] ciphertext = encrypt(textToSend) : encrypt the text
                // connection.send(ciphertext) : send the ciphertext
                // show(textToSend)  : show text we sent on our screen as "You: " + text

    }

    private void receiveText(String data) {
        String message = this.securitySolution.startDecryption(data.getBytes());
        showText(message, false);
    }

    /**
     * @return the connection
     */
    public Connection getConnection() {
        return connection;
    }


    public void setConnection(Connection connection, SecuritySolution securitySolution) {
        this.connection = connection;
        this.securitySolution = securitySolution;
        connection.setReceiverCallback(new Runnable() {
            @Override
            public void run() {
                while (!connection.getClientConnection().isClosed()) {
                    String data = connection.getMessage();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            receiveText(data);
                        }

                    });
                }
            }

        });

    }
}
