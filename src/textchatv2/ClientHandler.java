package textchatv2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {

    private Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        try {
            PrintWriter out
                    = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            String input = in.readLine();
            Scanner scanner = new Scanner(input);
            int algorithm = scanner.nextInt();
            int publicSize = scanner.nextInt();
            byte[] publicBytes = new byte[publicSize];
            clientSocket.getInputStream().read(publicBytes);
            BigInteger receivedPublic = new BigInteger(publicBytes);
            Connection connection = new Connection(clientSocket, algorithm, new BigInteger(publicBytes));
        } catch (IOException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
