package textchatv2;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
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
import javafx.stage.Stage;

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

    private Stage stage;

    private SecuritySolution securitySolution;

    @Override
    public void initialize(URL url, ResourceBundle rb) { }

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
        showText(textToSend, true);
        byte[] message = this.securitySolution.startEncryption(textToSend);
        this.connection.sendMessage(message);
    }

    private void receiveText(byte[] data) {
        String message = this.securitySolution.startDecryption(data);
        showText(message, false);
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection, SecuritySolution securitySolution) {
        this.connection = connection;
        this.securitySolution = securitySolution;
        connection.setReceiverCallback(() -> {
            while (!connection.getClientConnection().isClosed()) {
                byte[] data;
                try {
                    data = connection.getMessage();
                    Platform.runLater(() -> {
                        receiveText(data);
                    });
                } catch (IOException ex) {
                    Platform.exit();
                }
                
            }
        });
    }

    /**
     * @return the stage
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * @param stage the stage to set
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
