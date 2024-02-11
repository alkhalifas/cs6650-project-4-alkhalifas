package client;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 * UDP Client Class
 */
public class UDPClient extends AbstractClient {

    // Adding timeout
    private static final int TIMEOUT_MS = 5000;

    /**
     * UDP Client to send communications
     * @param hostname
     * @param port
     */
    public UDPClient(String hostname, int port) {
        super(hostname, port);
    }

    /**
     * Method to send a request via UDP client
     *
     * @param request
     */
    @Override
    public void sendRequest(String request) {

        // Connect to a DatagramSocket using the hostname and port
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
                // Receive and use Logger with response
                socket.receive(receivePacket);
                String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
                ClientLogger.log("UDPClient: " + response);

            } catch (SocketTimeoutException e) {

                //Logger with error message for SocketTimeoutException
                ClientLogger.log("UDPClient: SocketTimeoutException occurred. Server is unresponsive. Please check your request and try again.");
            }
        } catch (IOException e) {

            //Logger with error message for IOException
            ClientLogger.log("UDPClient: IOException occurred. Server is unresponsive. Connection Refused. Please check your IP and Port.");
//            e.printStackTrace();
        }
    }
}
