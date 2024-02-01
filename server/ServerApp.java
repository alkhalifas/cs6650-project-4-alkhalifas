package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;

/**
 * ServerApp that handles the server as a whole
 */
public class ServerApp {
    public static void main(String[] args) throws IOException {

        // Check for insufficient or missing arguments
        if (args.length != 2) {
            System.out.println("Usage: java server.ServerApp <tcp-port> <udp-port>");
            System.exit(1);
        }

        // Get the ports
        int tcpPort = Integer.parseInt(args[0]);
        int udpPort = Integer.parseInt(args[1]);

        // Start serverSocket
        // https://docs.oracle.com/javase/8/docs/api/java/net/ServerSocket.html
        ServerSocket serverSocket = new ServerSocket(tcpPort);
        System.out.println("TCP Server is running on port " + tcpPort);

        // Instantiate the KV database / datastore
        KeyValue store = new KeyValue();

        // Loop to keep online
        while (true) {
            // Enable connection
            // https://docs.oracle.com/javase/8/docs/api/java/net/ServerSocket.html
            Socket clientSocket = serverSocket.accept();
            new Thread(new TCPHandler(clientSocket, store)).start();
        }
    }
}
