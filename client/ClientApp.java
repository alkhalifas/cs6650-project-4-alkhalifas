package client;

/**
 * Class for the client application that implements the calls
 */
public class ClientApp {

    /**
     * Main driver that starts the connection
     * CS5010 //Todo search later
     * @param args hostname, port, and protocol (111111 3000 tcp
     */
    public static void main(String[] args) {

        ClientLogger.log("Alert - Starting Client Application");

        // Make sure enough arguments exist (address, port, protocol
        if (args.length != 3) {

            // Error handling
            ClientLogger.log("Error - Usage: java client.ClientApp <hostname> <port> <protocol> (java client.ClientApp localhost 1234 tcp)");
            System.exit(1);
        }

        // todo:
        // handle error of hostname
        // account of garbage vals "adasdasd"
        // account for host and IP address
        // try catch to find exceptions
        // e is generic exception

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
            // Logger -> write to file .txt
            // https://docs.oracle.com/javase%2F7%2Fdocs%2Fapi%2F%2F/java/util/logging/Logger.html
            ClientLogger.log("Error - Unsupported protocol: " + protocol);
            return;
        }

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

        // Examples of GET
        client.sendRequest("GET firstname");
        client.sendRequest("GET lastname");

    }
}
