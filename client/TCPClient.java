package client;

import java.net.Socket;
import java.net.SocketTimeoutException;
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
        try (Socket socket = new Socket(hostname, port)) {

            // Set timeout using setSoTimeout()
            socket.setSoTimeout(TIMEOUT_MS);

            // Set PrintWriter for getting the output response from the server
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Set BufferedReader for handling the input data
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Make call with 'out...'
            out.println(request);

            try {
                // Logger with response
                String response = in.readLine();
                ClientLogger.log("TCPClient: " + response);
            } catch (SocketTimeoutException e) {

                //Logger with error message
                ClientLogger.log("TCPClient: SocketTimeoutException occurred. Server is unresponsive. Please check your request and try again.");
            }

        } catch (IOException e) {
            ClientLogger.log("TCPClient: IOException occurred. Server is unresponsive. Connection Refused. Please check your IP and Port.");
//            e.printStackTrace();
        }
    }
}
