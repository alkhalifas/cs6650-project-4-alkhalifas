package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.DatagramPacket;

/**
 * ServerApp that handles the server as a whole
 */
public class ServerApp {
    public static void main(String[] args) throws IOException {

        // Check for insufficient or missing arguments
        if (args.length != 2) {
            ServerLogger.log("Error - Usage: java server.ServerApp <tcp-port> <udp-port>");
            System.exit(1);
        }

        // Get the ports
        int tcpPort = Integer.parseInt(args[0]);
        int udpPort = Integer.parseInt(args[1]);

        // Start serverSocket
        // https://docs.oracle.com/javase/8/docs/api/java/net/ServerSocket.html
        ServerSocket tcpServerSocket = new ServerSocket(tcpPort);
        DatagramSocket udpServerSocket = new DatagramSocket(udpPort);
        ServerLogger.log("TCP Server is running on port " + tcpPort);
        ServerLogger.log("UDP Server is running on port " + udpPort);

        // Instantiate the KV database / datastore
        KeyValue store = new KeyValue();

        // Loop to keep online
//        while (true) {
//            // Enable connection
//            // https://docs.oracle.com/javase/8/docs/api/java/net/ServerSocket.html
//            Socket clientSocket = serverSocket.accept();
//            new Thread(new TCPHandler(clientSocket, store)).start();
//        }


        // thread to handle TCP connections since we need both online
        new Thread(() -> {
            try {
                // Separate while true mechanim similar to previous one created
                while (true) {
                    Socket clientSocket = tcpServerSocket.accept();
                    new Thread(new TCPHandler(clientSocket, store)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        // Handle UDP requests
        while (true) {
            // UDP connection for datagram packet
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            udpServerSocket.receive(packet);
            // Separate thread for this one
            new Thread(new UDPHandler(packet, store)).start();
        }
    }
}
