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
                    response = "OK";
                    break;

                // GET
                case "GET":
                    String value = store.get(tokens[1]);
                    response = value != null ? value : "NOT FOUND";
                    break;

                // DELETTE
                case "DELETE":
                    store.delete(tokens[1]);
                    response = "OK";
                    break;
                default:
                    response = "ERROR Please make me into a logger and dont forget";
            }

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
