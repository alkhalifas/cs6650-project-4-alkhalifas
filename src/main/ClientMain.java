package main;

import client.Client;

/**
 * The main entry point for the Client application.
 */
public class ClientMain {

    /**
     * The main method which starts the client. It expects two command-line arguments:
     * the hostname and the port number of the server to connect
     */
    public static void main(String[] args) {
        try {
            // Check if we have correct number of arguments are provided.
            if (args.length != 2) {
                System.err.println("> Error: Correct usage of this is: java main.ClientMain <hostname> <port>");
                System.exit(1);
            } else {
                // Parse user entry to get the port number and validate its range.
                int portNumber = Integer.parseInt(args[1]);
                if (portNumber < 1100 || portNumber > 1104) {
                    System.err.println("> Error: Invalid port number. Select from following port numbers: 1100, 1101, 1102, 1103, 1104");
                    System.exit(1);
                } else {
                    // Create and execuet a client with the given hostname and port.
                    Client client = new Client(args[0], portNumber);
                    client.execute();

                    // todo: prepopulate
                    // todo: run 5 commands
                }
            }
        } catch (NumberFormatException e) {
            // Handle cases where the port number is not a valid integer.
            System.err.println("> Error: Invalid port number. Select from following port numbers: 1100, 1101, 1102, 1103, 1104");
            System.exit(1);
        }
    }
}
