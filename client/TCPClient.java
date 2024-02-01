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

    /**
     * TCP Client to send communications
     * @param hostname
     * @param port
     */
    public TCPClient(String hostname, int port) {
        super(hostname, port);
    }

    /**
     * Method to send a request via TCP client
     * @param request
     */
    @Override
    public void sendRequest(String request) {

        // Connect to a socket using the hostname and port
        try (Socket socket = new Socket(hostname, port);

             // Set PrintWriter for getting the output response from the server
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

             // Set BufferedReader for handling the input data
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
//            BufferedReader in = new BufferedReader(new InputStreamReader(socket))) {

            // Make call
            out.println(request);
            String response = in.readLine();

            // Print the output (response)
            System.out.println("Response: " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
