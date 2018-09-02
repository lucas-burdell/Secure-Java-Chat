package textchatv2;

import java.io.IOException;
import java.util.Random;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MainApplication extends Application {

    private static MainApplication application;

    public static MainApplication getApplication() {
        return application;
    }

    public static void setApplication(MainApplication aApplication) {
        application = aApplication;
    }

    public void startChatGui(Connection connection) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> {
                startChatGui(connection);
            });
        } else {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                Pane p = fxmlLoader.load(MainApplication.class.getResourceAsStream("Chat.fxml"));
                ChatController controller = (ChatController) fxmlLoader.getController();
                controller.setConnection(connection,
                        SecuritySuite.getSecuritySolution(
                                connection.getAlgorithm(),
                                connection.getSharedSecret().toByteArray()));
                Stage stage = new Stage();
                stage.setScene(new Scene(p));
                stage.setTitle(connection.getClientConnection().getInetAddress().toString());
                controller.setStage(stage);
                stage.show();
            } catch (IOException ioe) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Problems");
                alert.setContentText("A problem occured opening a chat window for "
                        + connection.getClientConnection().getInetAddress());
                alert.show();
                ioe.printStackTrace(System.err);
            }
        }
    }

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
    }
}
