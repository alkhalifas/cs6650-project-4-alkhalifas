package client;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.net.SocketTimeoutException;

public class UDPClient extends AbstractClient {

    // Adding timeout
    private static final int TIMEOUT_MS = 5000;

    public UDPClient(String hostname, int port) {
        super(hostname, port);
    }

    @Override
    public void sendRequest(String request) {
        try (DatagramSocket socket = new DatagramSocket()) {

            // Setting the timeout
            socket.setSoTimeout(TIMEOUT_MS);

            // Method to send data
            byte[] sendData = request.getBytes();

            // Set InetAddress per requirements
            InetAddress address = InetAddress.getByName(hostname);

            // Create the dDatagramPacket
            // https://docs.oracle.com/javase/8/docs/api/java/net/DatagramPacket.html
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
            socket.send(sendPacket);

            // Prepare receiveData, buffer
            byte[] receiveData = new byte[1024];

            // Get receive packet
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            try {
                socket.receive(receivePacket);
                String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("Response: " + response);
            } catch (SocketTimeoutException e) {
                System.out.println("Timeout occurred: Server is unresponsive. Please try again.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
