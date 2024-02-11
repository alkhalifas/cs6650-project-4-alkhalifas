package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.io.IOException;

/**
 * UDP Handler Class for the server
 */
public class UDPHandler implements Runnable {
    private DatagramPacket packet;
    private KeyValue store;

    public UDPHandler(DatagramPacket packet, KeyValue store) {
        this.packet = packet;
        this.store = store;
    }

    @Override
    public void run() {
        String senderAddress = packet.getAddress().getHostAddress();
        int senderPort = packet.getPort();
        try {
            String request = new String(packet.getData(), 0, packet.getLength()).trim();
            ServerLogger.log("Received request from " + senderAddress + ":" + senderPort + " - " + request);

            String[] tokens = request.split(" ");
            String response = "";

            if (tokens.length < 2) {
                throw new IllegalArgumentException("Incomplete request");
            }

            switch (tokens[0].toUpperCase()) {
                case "PUT":
                    if (tokens.length < 3) throw new IllegalArgumentException("PUT request missing arguments");
                    store.put(tokens[1], tokens[2]);
                    response = "200 - Message received and saved successfully";
                    break;
                case "GET":
                    String value = store.get(tokens[1]);
                    response = value != null ? value : "404 - Key not found";
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
