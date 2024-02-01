package client;

/**
 * Class for the client application that implements the calls
 */
public class ClientApp {

    /**
     * Main driver that starts the connection
     * @param args hostname, port, and protocol (111111 3000 tcp
     */
    public static void main(String[] args) {

        // Make sure enough arguments exist (address, port, protocol
        if (args.length != 3) {

            // Error handling
            System.out.println("Usage: java client.ClientApp <hostname> <port> <protocol>");
            System.exit(1);
        }

        // Set the arguments as variables
        String hostname = args[0];
        int port = Integer.parseInt(args[1]);
        String protocol = args[2];

        // Create an abstract client for TCP
        AbstractClient client;

        // Check if TCP
        if ("tcp".equalsIgnoreCase(protocol)) {
            client = new TCPClient(hostname, port);
        } else if ("udp".equalsIgnoreCase(protocol)) {

            // Check if UDP
            client = new UDPClient(hostname, port);
        } else {

            // Client-side error handlign for bad protocol
            System.out.println("Unsupported protocol: " + protocol);
            return;
        }

        // Examples of PUT
        client.sendRequest("PUT key1 value1");
        client.sendRequest("PUT movie alladin");
        client.sendRequest("PUT show seinfeld");
        client.sendRequest("PUT zipcode 02148");

        // Examples of GET
        client.sendRequest("GET movie");
        client.sendRequest("GET show");
        client.sendRequest("GET zipcode");

        // Examples of DELETE
        client.sendRequest("DELETE key1");
        client.sendRequest("DELETE movie");
    }
}
