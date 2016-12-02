package textchatv2;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

/**
 *
 * @author lucas.burdell
 */
public class ConnectViewController implements Initializable {

    @FXML
    private ToggleGroup algorithms;
    @FXML
    private TextField ipbox;

    private int selectedAlgorithm = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // code snippet from 
        // http://stackoverflow.com/questions/21374214/get-toggle-radiobutton-user-value
        
        algorithms.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> ov, 
                    Toggle oldValue, Toggle newValue) {
                selectedAlgorithm = algorithms.getToggles().indexOf(
                        algorithms.getSelectedToggle());

            }
        });
    }

    @FXML
    private void onConnect(ActionEvent event) {
        Connection con = null;
        try {
            con = Connection.connectTo(selectedAlgorithm, ipbox.getText());
        } catch (IOException ex) {
            Logger.getLogger(ConnectViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (con == null) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Connection Not Established");
            alert.setContentText("Connection to " + ipbox.getText() + " could "
                    + "not be established!");
            alert.showAndWait();
        } else {
            MainApplication.getApplication().startChatGui(con);
            ipbox.setText("");
        }
    }

}
