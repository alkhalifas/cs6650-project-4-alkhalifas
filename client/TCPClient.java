package client;

import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;

/**
 * TCP Client class
 */
public class TCPClient extends AbstractClient {
    // Timeout requirement
    private static final int TIMEOUT_MS = 5000;

    /**
     * TCP Client to send communications
     *
     * @param hostname
     * @param port
     */
    public TCPClient(String hostname, int port) {
        super(hostname, port);
    }

    /**
     * Method to send a request via TCP client
     *
     * @param request
     */
    @Override
    public void sendRequest(String request) {

        // Connect to a socket using the hostname and port
        try (Socket socket = new Socket(hostname, port);

             // Set timeout using setSoTimeout()
             socket.setSoTimeout(TIMEOUT_MS);

             // Set PrintWriter for getting the output response from the server
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

             // Set BufferedReader for handling the input data
             // https://docs.oracle.com/javase/8/docs/api/java/io/InputStreamReader.html
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
//            BufferedReader in = new BufferedReader(new InputStreamReader(socket))) {

            // Make call
            out.println(request);

            try {
                String response = in.readLine();
                System.out.println("Response: " + response);
            } catch (SocketTimeoutException e) {
                System.out.println("Timeout occurred: Server is unresponsive. Plese try again.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
