package server;

import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;

/**
 * TCPHandler class
 */
public class TCPHandler implements Runnable {
    private Socket clientSocket;
    private KeyValue store;

    public TCPHandler(Socket socket, KeyValue store) {

        // Set the socket and store
        this.clientSocket = socket;
        this.store = store;
    }

    @Override
    public void run() {
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
                        response = value != null ? value : "404 - Key not found.";
                        break;

                    // DLETE
                    case "DELETE":
                        store.delete(tokens[1]);
                        response = "200 - Message deleted successfully";
                        break;

                    //Error handler to give user feedback, per specification
                    default:
                        response = "Unknown Error - Invalid key received. Please use PUT DELETE GET.";
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
