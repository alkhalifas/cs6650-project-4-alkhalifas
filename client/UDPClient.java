package client;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;

public class UDPClient extends AbstractClient {
    public UDPClient(String hostname, int port) {
        super(hostname, port);
    }

    @Override
    public void sendRequest(String request) {
        try (DatagramSocket socket = new DatagramSocket()) {

            // Method to send data
            byte[] sendData = request.getBytes();

            // Set InetAddress per requirements
            InetAddress address = InetAddress.getByName(hostname);

            // Create the dDatagramPacket
            // https://docs.oracle.com/javase/8/docs/api/java/net/DatagramPacket.html
            DatagramPacket packet = new DatagramPacket(sendData, sendData.length, address, port);
            socket.send(packet);

            // Prepare buffer
            byte[] buffer = new byte[1024];

            // Get response packet
            DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(responsePacket);
            String response = new String(responsePacket.getData(), 0, responsePacket.getLength());

            // print response
            // todo: add to logger later
            System.out.println("Response: " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
