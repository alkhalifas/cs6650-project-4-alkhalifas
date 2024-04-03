package main;

import client.Client;

/**
 * The main class responsible for initiating and running the client application.
 */
public class ClientMain {

    /**
     * The main method that initializes the client and executes its functionality.
     *
     * @param args Command-line arguments (not used in this application)
     */
    public static void main(String[] args) {
        // Create a new instance of the Client
        Client client = new Client();

        // Pre-populate any necessary data or configurations
        client.prePopulate();

        // Execute the client functionality
        client.execute();
    }
}
