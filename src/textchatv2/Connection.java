package textchatv2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

/**
 *
 * @author lucas.burdell
 */
public class Connection {

    private static ServerSocket server;
    private static final Random RANDOM = new Random();
    public static final BigInteger PRIME = new BigInteger("58471251915205049503966067708399220941");
    public static final BigInteger GENERATOR = new BigInteger("161757641980761671241642708495003159553");

    private final ServerSocket serverConnection = Connection.server;
    private Socket clientConnection;
    private int algorithm;
    private BigInteger prime = Connection.PRIME;
    private BigInteger generator = Connection.GENERATOR;
    private BigInteger privateNumber;
    private BigInteger sharedSecret;
    private BigInteger publicNumber;

    // runnable to call when data received after initial setup (i.e. messages)
    private Runnable receiverCallback;

    // boolean set to true when keys successfuly exchanged
    private boolean connectionEstablished = false;

    //from http://stackoverflow.com/questions/4582277/biginteger-powbiginteger
    public final BigInteger repeatedSquaring(BigInteger base, BigInteger exponent, BigInteger modulus) {
        BigInteger result = BigInteger.ONE;
        base = base.mod(modulus);
        while (exponent.signum() > 0) {
            if (exponent.testBit(0)) {
                result = result.multiply(base).mod(modulus);
            }
            base = base.multiply(base).mod(modulus);
            exponent = exponent.shiftRight(1);
        }
        return result;
    }

    //constructor for creating a connection for connecting to a person
    public Connection(Socket client, int algorithm) {
        clientConnection = client;
        this.algorithm = algorithm;
        System.out.println("prime: " + prime);
        System.out.println("generator: " + generator);
        //generator = p;
        privateNumber = BigInteger.probablePrime(64, RANDOM);
        System.out.println("staring repeated squaring");
        publicNumber = repeatedSquaring(prime, privateNumber, generator);
        this.startTransfer();
    }

    //constructor for receiving a connection from another person
    public Connection(Socket client, int algorithm, BigInteger magic) {
        this.clientConnection = client;
        this.algorithm = algorithm;
        this.privateNumber = BigInteger.probablePrime(64, RANDOM);

        System.out.println("Received public: " + magic);
        //BigInteger otherPublic = BigInteger.valueOf(magic);
        this.sharedSecret = repeatedSquaring(magic, privateNumber, generator);
        System.out.println("secret: " + this.sharedSecret);
        //System.out.println("secret powered: " + otherPublic.pow(privateNumber.intValueExact()).toString());
        this.publicNumber = repeatedSquaring(prime, privateNumber, generator);
        System.out.println("My public: " + publicNumber);
        try {
            PrintWriter writer = new PrintWriter(clientConnection.getOutputStream());
            byte[] publicArray = publicNumber.toByteArray();
            writer.println(publicArray.length);
            writer.flush();
            clientConnection.getOutputStream().write(publicArray);
            clientConnection.getOutputStream().flush();
            //writer.println("" + publicNumber.toByteArray());
            //writer.flush();
            System.out.println("" + publicNumber + " sent");
        } catch (IOException ioe) {
            System.out.println("DH exchanged failed. TODO: panic");
        }
        this.setConnectionEstablished(true);
        MainApplication.getApplication().startChatGui(this);
    }

    //start the key transfer to the server
    private boolean startTransfer() {
        // try-with-resources can't catch implicit IOException on close() method
        // of stream, so you need the catch clause
        try (PrintWriter writer = new PrintWriter(clientConnection.getOutputStream())) {
            byte[] publicBytes = publicNumber.toByteArray();
            writer.println(algorithm + " " + publicBytes.length);
            writer.flush();

            BufferedWriter byteWriter = new BufferedWriter(new OutputStreamWriter(clientConnection.getOutputStream()));

            clientConnection.getOutputStream().write(publicBytes);
            clientConnection.getOutputStream().flush();

            System.out.println("sent: " + algorithm + " " + this.getPublicNumber());

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientConnection.getInputStream()));
            String sizeString = in.readLine();
            int publicSize = Integer.parseInt(sizeString);

            //int magic = Integer.parseInt(in.readLine());
            System.out.println("awaiting " + publicSize + " bytes");
            publicBytes = new byte[publicSize];
            clientConnection.getInputStream().read(publicBytes);
            BigInteger receivedPublic = new BigInteger(publicBytes);
            System.out.println("received public: " + receivedPublic);

            //output.sharedSecret = otherPublic.pow(output.privateNumber.intValueExact()).mod(output.generator);
            this.setSharedSecret(repeatedSquaring(receivedPublic, privateNumber, generator));
            System.out.println("secret: " + this.getSharedSecret());
            //this.setSharedSecret((int) (Math.pow(receivedPublic, this.getPrivateNumber()) % this.getGenerator()));
            this.setConnectionEstablished(true);
            return true;
        } catch (IOException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Connection closed/refused");
            return false;
        } finally {
            return false;
        }

    }

    //Parse an IP string into 
    public static Connection connectTo(int algorithm, String address) throws IOException {
        Pattern pattern = Pattern.compile(":");
        String[] result = pattern.split(address);
        String ip = result[0];
        String port = null;
        if (result.length == 1) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("USING DEFAULT PORT");
            alert.setContentText("Using default port (12345). Is this okay?");
            Optional<ButtonType> alertResult = alert.showAndWait();
            if (alertResult.isPresent() && alertResult.get() == ButtonType.OK) {
                port = "12345"; // default port
            }
        } else {
            port = result[1];
        }

        Connection newConnection = null;
        Socket socket = new Socket(ip, Integer.parseInt(port));
        newConnection = new Connection(socket, algorithm);
        return newConnection;
    }

    // 58243
    // 59359
    //RC4:0 TEA:1
    // algorithm A
    // prime modulus G
    // generator P
    // g % p
    // A G P
    // start the server that listens for connections from other applications
    public static void startServer(int port) {

        try {
            // try binding to 50 ports before quitting
            server = new ServerSocket(port);
        } catch (IOException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Server error: Port " + port + " already in use!");
        }

        Thread serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!server.isClosed()) {
                    try {
                        Socket clientSocket = server.accept();

                        Thread thread = new Thread(new ClientHandler(clientSocket));
                        thread.setDaemon(true);
                        thread.start();
                    } catch (IOException ioe) {
                        System.out.println("IOException in server (server closed?)");
                        ioe.printStackTrace();
                    }
                }
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();

    }

    /**
     * @return the serverConnection
     */
    protected ServerSocket getServerConnection() {
        return serverConnection;
    }

    /**
     * @return the clientConnection
     */
    protected Socket getClientConnection() {
        return clientConnection;
    }

    /**
     * @param clientConnection the clientConnection to set
     */
    protected void setClientConnection(Socket clientConnection) {
        this.clientConnection = clientConnection;
    }

    /**
     * @return the algorithm
     */
    protected int getAlgorithm() {
        return algorithm;
    }

    /**
     * @param algorithm the algorithm to set
     */
    protected void setAlgorithm(int algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * @return the prime
     */
    protected BigInteger getPrime() {
        return prime;
    }

    /**
     * @return the generator
     */
    protected BigInteger getGenerator() {
        return generator;
    }

    /**
     * @return the privateNumber
     */
    protected BigInteger getPrivateNumber() {
        return privateNumber;
    }

    /**
     * @param privateNumber the privateNumber to set
     */
    protected void setPrivateNumber(BigInteger privateNumber) {
        this.privateNumber = privateNumber;
    }

    /**
     * @return the sharedSecret
     */
    protected BigInteger getSharedSecret() {
        return sharedSecret;
    }

    /**
     * @param sharedSecret the sharedSecret to set
     */
    protected void setSharedSecret(BigInteger sharedSecret) {
        this.sharedSecret = sharedSecret;
    }

    /**
     * @return the publicNumber
     */
    protected BigInteger getPublicNumber() {
        return publicNumber;
    }

    /**
     * @param publicNumber the publicNumber to set
     */
    protected void setPublicNumber(BigInteger publicNumber) {
        this.publicNumber = publicNumber;
    }

    /**
     * @return the connectionEstablished
     */
    public boolean isConnectionEstablished() {
        return connectionEstablished;
    }

    /**
     * @param connectionEstablished the connectionEstablished to set
     */
    private void setConnectionEstablished(boolean connectionEstablished) {
        this.connectionEstablished = connectionEstablished;
    }

    /**
     * @return the receiverCallback
     */
    public Runnable getReceiverCallback() {
        return receiverCallback;
    }

    /**
     * @param receiverCallback the receiverCallback to set
     */
    public void setReceiverCallback(Runnable receiverCallback) {
        if (receiverCallback == null) {
            this.receiverCallback = receiverCallback;
            Thread listener = new Thread(receiverCallback);
            listener.setDaemon(true);
            listener.start();
        }
    }
}
