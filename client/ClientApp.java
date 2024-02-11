package client;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Class for the client application that implements the calls
 */
public class ClientApp {

    /**
     * Main driver that starts the connection
     * @param args hostname, port, and protocol
     */
    public static void main(String[] args) {

        ClientLogger.log("TCPClient: Starting Client Application on " + args[2] + " - " + args[0] + ":" + args[1]);

        // Make sure enough arguments exist (address, port, protocol, ...)
        if (args.length != 3) {

            // Error handling
            ClientLogger.log("Error - Usage: java client.ClientApp <hostname> <port> <protocol> (java client.ClientApp localhost 1234 tcp)");
            System.exit(1);
        }

        // Error handling for hostname/ip
        String hostname = args[0];
        if (!isValidHostnameOrIP(hostname)) {
            ClientLogger.log("Invalid hostname/IP: " + hostname);
            System.exit(1);
        }

        // Placeholder for port to avoid error when compiling
        int port = 0;
        try {
            // Parse port
            port = Integer.parseInt(args[1]);

            // Add port range
            // https://stackoverflow.com/questions/113224/what-is-the-largest-tcp-ip-network-port-number-allowable-for-ipv4
            if (port < 0 || port > 65535) {
                throw new IllegalArgumentException("Port number must be between 0 and 65535");
            }
        } catch (IllegalArgumentException e) {
            ClientLogger.log("Invalid port number: " + args[1]);
            System.exit(1);
        }

        // Check if TCP
        String protocol = args[2].toLowerCase();
        AbstractClient client;

        // Check if TCP or UDP
        switch (protocol) {
            case "tcp":
                client = new TCPClient(hostname, port);
                break;
            case "udp":
                client = new UDPClient(hostname, port);
                break;
            default:
                ClientLogger.log("Unsupported protocol: " + protocol);
                return;
        }
        // Prepopulate Client
        prepopulateClient(client);

        // Run Operations (5 of each, plus edge cases)
        performClientOperations(client);

    }

    /**
     * Function that runs the client operations of 5 per category
     * @param client
     */
    private static void prepopulateClient(AbstractClient client) {

        ClientLogger.log("TCPClient: Populating Data Store");

        // Examples of PUT
        client.sendRequest("PUT school northeastern");
        client.sendRequest("PUT pet dog");
        client.sendRequest("PUT height 6ft");
        client.sendRequest("PUT phone 1234567890");
        client.sendRequest("PUT email somewhere@gmail.com");


    }

    /**
     * Function that runs the client operations of 5 per category
     * @param client
     */
    private static void performClientOperations(AbstractClient client) {

        // 5 of Each Operation
        ClientLogger.log("TCPClient: Running 5 of each operation");

        // Examples of PUT
        client.sendRequest("PUT firstname saleh");
        client.sendRequest("PUT lastname alkhalifa");
        client.sendRequest("PUT tvshow seinfeld");
        client.sendRequest("PUT zipcode 02148");
        client.sendRequest("PUT job datascientist");

        // Examples of GET
        client.sendRequest("GET firstname");
        client.sendRequest("GET lastname");
        client.sendRequest("GET job");
        client.sendRequest("GET datascientist");
        client.sendRequest("GET car");

        // Examples of DELETE
        client.sendRequest("DELETE firstname");
        client.sendRequest("DELETE lastname");
        client.sendRequest("DELETE tvshow");
        client.sendRequest("DELETE zipcode");
        client.sendRequest("DELETE job");

        // Edge Cases:
        ClientLogger.log("TCPClient: Running Edge Cases");

        // Examples of GET after DELETE
        client.sendRequest("GET firstname");
        client.sendRequest("GET lastname");

        // Invalid Key
        client.sendRequest("MOVE lastname");

        // Timemout
        client.sendRequest("PUT"); // Will time out and then continue to next command
        client.sendRequest("PUT firstname saleh");

    }

    /**
     * Function that checks to confirm if input is a hostname or ip address using InetAddress
     * @param hostname
     * @return
     */
    private static boolean isValidHostnameOrIP(String hostname) {
        try {
            // Check if valid hostname or ip
            InetAddress.getByName(hostname);
            return true;
            // Catch if not valid
        } catch (UnknownHostException e) {
            return false;
        }
    }

}
