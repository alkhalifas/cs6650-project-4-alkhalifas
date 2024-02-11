package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.io.IOException;

/**
 * UDP Handler Class for the server that implements Runnable to have thread
 */
public class UDPHandler implements Runnable {
    private DatagramPacket packet;
    private KeyValue store;

    /**
     * UDPHandler that connects to store and socket
     *
     * @param socket
     * @param store
     */
    public UDPHandler(DatagramPacket packet, KeyValue store) {

        // Set the socket and store
        this.packet = packet;
        this.store = store;
    }

    /**
     * Method that starts the UDP server
     */
    @Override
    public void run() {

        // Get sender address and port
        String senderAddress = packet.getAddress().getHostAddress();
        int senderPort = packet.getPort();

        // Start try except block
        try {
            String request = new String(packet.getData(), 0, packet.getLength()).trim();
            ServerLogger.log("Received request from " + senderAddress + ":" + senderPort + " - " + request);

            String[] tokens = request.split(" ");
            String response = "";

            // Check for malformed requets
            if (tokens.length < 2 || tokens.length > 3) {
                ServerLogger.log("UDPHandler: Received malformed request of length " + tokens.length + " from " + senderAddress + ":" + senderPort);
                response = "400 - Bad Request. Expected format: COMMAND KEY [VALUE]";

                // Convert the response string to byte array
                byte[] responseData = response.getBytes();

                // Prepare the response packet
                DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length, packet.getAddress(), packet.getPort());

                // Create a DatagramSocket to send the response
                try (DatagramSocket responseSocket = new DatagramSocket()) {
                    responseSocket.send(responsePacket); // Send the response
                } catch (IOException e) {
                    ServerLogger.log("UDPHandler: Error sending response to " + senderAddress + ":" + senderPort + " - " + e.getMessage());
                }
                return; // Exit the method or handle accordingly
            }

            // Conditional switch to get the specific command
            switch (tokens[0].toUpperCase()) {
                case "PUT":
                    if (tokens.length < 3) throw new IllegalArgumentException("PUT request missing arguments");
                    store.put(tokens[1], tokens[2]);
                    response = "200 - Message received and saved successfully";
                    break;
                case "GET":
                    String value = store.get(tokens[1]);
                    response = value != null ? "200 - " + value : "404 - Key not found";
                    break;
                case "DELETE":
                    store.delete(tokens[1]);
                    response = "200 - Message deleted successfully";
                    break;
                default:
                    throw new IllegalArgumentException("Unknown command");
            }

            ServerLogger.log("Response to " + senderAddress + ":" + senderPort + " - " + response);

            byte[] responseData = response.getBytes();
            DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length, packet.getAddress(), packet.getPort());
            DatagramSocket responseSocket = new DatagramSocket();
            responseSocket.send(responsePacket);
            responseSocket.close();
        } catch (IllegalArgumentException e) {
            ServerLogger.log("Received malformed request of length " + packet.getLength() + " from " + senderAddress + ":" + senderPort + " - Error: " + e.getMessage());
        } catch (IOException e) {
            ServerLogger.log("IOException occurred while responding to " + senderAddress + ":" + senderPort + " - Error: " + e.getMessage());
        }
    }
}
