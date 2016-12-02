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

/**
 *
 * @author lucas.burdell
 */
public class ClientHandler implements Runnable {

    private Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {

        try (
                PrintWriter out
                = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));) {
            System.out.println("Connection received from " + clientSocket.getInetAddress());
            String input = in.readLine();
            System.out.println("input: " + input);
            Scanner scanner = new Scanner(input);

            // get algorithm
            int algorithm = scanner.nextInt();
            System.out.println("Algorithm: " + algorithm);
            // get G
            //int prime = scanner.nextInt();
            // get P
            //int generator = scanner.nextInt();
            // get magic number
            int publicSize = scanner.nextInt();
            byte[] publicBytes = new byte[publicSize];
            clientSocket.getInputStream().read(publicBytes);
            BigInteger receivedPublic = new BigInteger(publicBytes);
            //System.out.println("received public: " + receivedPublic);
            
            System.out.println("received public size: " + publicSize);

            Connection connection = new Connection(clientSocket, algorithm, new BigInteger(publicBytes));

            System.out.println("Secret: " + connection.getSharedSecret());

        } catch (IOException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
