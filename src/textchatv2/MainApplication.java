package textchatv2;

import java.io.IOException;
import java.util.Scanner;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 *
 * @author lucas.burdell
 */
public class MainApplication extends Application {
    
    private static MainApplication application;

    /**
     * @return the application
     */
    public static MainApplication getApplication() {
        return application;
    }

    /**
     * @param aApplication the application to set
     */
    public static void setApplication(MainApplication aApplication) {
        application = aApplication;
    }
    

    public void startChatGui(Connection connection) {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            Pane p = fxmlLoader.load(MainApplication.class.getResourceAsStream("Chat.fxml"));
            ChatController fooController = (ChatController) fxmlLoader.getController();
            Stage stage = new Stage();
            stage.setScene(new Scene(p));
            stage.setTitle(connection.getClientConnection().getInetAddress().toString());
            stage.show();
            //throw new IOException();
        } catch (IOException ioe) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Problems");
            alert.setContentText("A problem occured opening a chat window for "
                    + connection.getClientConnection().getInetAddress());
            alert.show();
            ioe.printStackTrace();
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        Connection.startServer(12345);
        Application.launch(args);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        setApplication(this);
        FXMLLoader fxmlLoader = new FXMLLoader();
        Pane p = fxmlLoader.load(ChatController.class.getResourceAsStream("ConnectView.fxml"));
        Scene primaryScene = new Scene(p);
        primaryStage.setScene(primaryScene);
        primaryStage.setTitle("Connections");
        primaryStage.show();
        
        //ChatController fooController = (ChatController) fxmlLoader.getController();

        /*
        FXMLLoader fxmlLoader = new FXMLLoader();
        Pane p = fxmlLoader.load(TextChat.class.getResource("Chat.fxml").openStream());
        ChatController fooController = (ChatController) fxmlLoader.getController();
         */
        /*
        Scanner scanner = new Scanner(System.in);
        System.out.println("connecting?");
        if (scanner.nextBoolean()) {
            scanner.nextLine();
            System.out.println("IP? ");
            Connection kaaan = Connection.connectTo(0, scanner.nextLine());
            startChatGui(kaaan);
            System.out.println("CONNECTED");
        } else {
            Connection.startServer(12345);
        }
        */
    }

}
