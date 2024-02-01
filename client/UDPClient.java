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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
