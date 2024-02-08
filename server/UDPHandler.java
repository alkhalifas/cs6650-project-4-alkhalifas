package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.io.IOException;

/**
 * UDP Handler Class placeholder
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
        try {

            // Get the request, tokens command
            String request = new String(packet.getData(), 0, packet.getLength());
            String[] tokens = request.split(" ");
            String command = tokens[0];

            // Response
            String response = "";

            // Conditional for each command
            switch (command.toUpperCase()) {

                //PUT
                case "PUT":
                    store.put(tokens[1], tokens[2]);
                    response = "200 - Message received and saved successfully";
                    break;

                // GET
                case "GET":
                    String value = store.get(tokens[1]);
                    response = value != null ? value : "404 - Key not found.";
                    break;

                // DELETTE
                case "DELETE":
                    store.delete(tokens[1]);
                    response = "200 - Message deleted successfully";
                    break;
                default:
                    response = "Unknown Error - Invalid key received. Please use PUT DELETE GET.";
            }

            String senderAddress = packet.getAddress().getHostAddress();
            int senderPort = packet.getPort();

            ServerLogger.log("UDP Server: Incoming Request from " + senderAddress + ":" + senderPort + " - " + response);


            // response handling
            byte[] responseData = response.getBytes();
            DatagramSocket socket = new DatagramSocket();
            DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length, packet.getAddress(), packet.getPort());
            socket.send(responsePacket);
            socket.close();

            // Response handling
//            byte[] responseData = response.getBytes();
//            DatagramSocket socket = new DatagramSocket();
//            DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length, packet.getAddress(), packet.getPort());
//            socket.send(responsePacket);
//            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
