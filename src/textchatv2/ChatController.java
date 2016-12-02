package textchatv2;

import java.net.URL;
import java.util.ResourceBundle;
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
    
    private Text createText(String content, boolean sending) {
        Color textColor = sending ? Color.GREEN : Color.BLUE;
        content = (sending ? "You: " : "Them: ") + content;
        Text text = new Text(content);
        text.setFill(textColor);
        return text;
    }
    
    private void showText(String content, boolean sending) {
        Color textColor = sending ? Color.GREEN : Color.BLUE;
        content = (sending ? "You: " : "Them: ") + content;
        Text text = new Text(content);
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
        // do something with it
        // byte[] ciphertext = encrypt(textToSend) : encrypt the text
        // connection.send(ciphertext) : send the ciphertext
        // show(textToSend)  : show text we sent on our screen as "You: " + text
        
    }

    /**
     * @return the connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * @param connection the connection to set
     */
    public void setConnection(Connection connection) {
        this.connection = connection;
        this.securitySolution = SecuritySuite.getSecuritySolution(0, connection.getPrivateNumber().toByteArray());
    }
    
}
