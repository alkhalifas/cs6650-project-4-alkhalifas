package server;

import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;

/**
 * TCPHandler class for server that implenents Runnable to have threading capability
 */
public class TCPHandler implements Runnable {
    private Socket clientSocket;
    private KeyValue store;

    /**
     * TCPHandler that connects to store and socket
     *
     * @param socket
     * @param store
     */
    public TCPHandler(Socket socket, KeyValue store) {

        // Set the socket and store
        this.clientSocket = socket;
        this.store = store;
    }

    /**
     * Method that starts the TCP server
     */
    @Override
    public void run() {

        // Start try except block
        try {
            // Input output //todo: add logger here later
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            String inputLine;

            // Conditional to wrap around if NULL
            while ((inputLine = in.readLine()) != null) {

                // Get toknes, command and response
                String[] tokens = inputLine.split(" ");
                String command = tokens[0];
                String response = "";

                // Check for malformed requests
                if (tokens.length < 2 || tokens.length > 3) {
                    // Log malformed request
                    ServerLogger.log("TCPHandler: Received malformed request of length " + tokens.length + " from " + clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort());
                    response = "400 - Bad Request. Expected format: COMMAND KEY [VALUE]";
                    out.println(response);
                    continue;
                }

                // Conditional switch to get the specific command
                switch (command.toUpperCase()) {

                    // PUT
                    case "PUT":
                        store.put(tokens[1], tokens[2]);
                        response = "200 - Message received and saved successfully";
                        break;

                    // GET
                    case "GET":
                        String value = store.get(tokens[1]);
                        response = value != null ? "200 - " + value : "404 - Key not found";
                        break;

                    // DLETE
                    case "DELETE":
                        store.delete(tokens[1]);
                        response = "200 - Message deleted successfully";
                        break;

                    //Error handler to give user feedback, per specification
                    default:
                        response = "400 - Bad Request. Invalid key received. Please use PUT DELETE GET.";
                }

                // Get the client address using getInetAddress
                String clientAddress = clientSocket.getInetAddress().getHostAddress();

                // Get the client port
                int clientPort = clientSocket.getPort();

                // print the response
                ServerLogger.log("TCPHandler: Incoming Request from " + clientAddress + ":" + clientPort + ", response: " + response);

                // Send back response
                out.println(response);
            }
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
