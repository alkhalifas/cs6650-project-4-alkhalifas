package client;

import common.KeyValueInterface;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.Random;

/**
 * Client app that uses a rmi registry to handle communication with backend.
 */
public class ClientApp {
    private static Registry registry;
    private static String[] serverNames;
    private static KeyValueInterface service;
    private static int rmiPort;

    public static void main(String[] args) {
        try {
            // Get host name, otherwise default to localhost
            String host = args.length > 0 ? args[0] : "localhost";

            // Get port name otherwise default to 1099
            int rmiPort = args.length > 1 ? Integer.parseInt(args[1]) : 1099;

            // Instantiate registry using the host and port
            registry = LocateRegistry.getRegistry(host, rmiPort);

            // Server replica names
            serverNames = new String[]{"KeyValueServer1", "KeyValueServer2", "KeyValueServer3", "KeyValueServer4", "KeyValueServer5"};

            // Lookup one of the server replicas
            service = (KeyValueInterface) registry.lookup(serverNames[0]);

            // Check if the lookup operation was successful
            if (service != null) {
                ClientLogger.log("Successfully connected to the server.");

                // Pre-populate the key-value store
                prepopulateClient();

                // Perform additional client operations
                performClientOperations();

                // Interactive session for user commands
                interactiveSession(rmiPort);
            } else {
                ClientLogger.log("Failed to connect to the server.");
            }
        } catch (Exception e) {
            ClientLogger.log("Error during server lookup: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Method that prepopulates the server with data
     * @throws Exception
     */
    private static void prepopulateClient() throws Exception {
        ClientLogger.log("ClientApp: Populating Data Store");
        ClientLogger.log("ClientApp: Running 5 of each operation:");

        // Examples of PUT for pre-population
        logAndExecutePut("school", "northeastern", serverNames[0], rmiPort);
        logAndExecutePut("pet", "dog", serverNames[0], rmiPort);
        logAndExecutePut("height", "6ft", serverNames[0], rmiPort);
        logAndExecutePut("phone", "1234567890", serverNames[0], rmiPort);
        logAndExecutePut("email", "somewhere@gmail.com", serverNames[0], rmiPort);

        logAndExecuteGet("school", serverNames[0], rmiPort);
        logAndExecuteGet("pet", serverNames[0], rmiPort);
        logAndExecuteGet("height", serverNames[0], rmiPort);
        logAndExecuteGet("email", serverNames[0], rmiPort);
        logAndExecuteGet("phone", serverNames[0], rmiPort);

        logAndExecuteDelete("pet", serverNames[0], rmiPort);
        logAndExecuteDelete("school", serverNames[0], rmiPort);
        logAndExecuteDelete("email", serverNames[0], rmiPort);
        logAndExecuteDelete("height", serverNames[0], rmiPort);
        logAndExecuteDelete("phone", serverNames[0], rmiPort);


    }

    /**
     * Method that performs many client operations to rest the robustness and error handling of the server
     * @throws Exception
     */
    private static void performClientOperations() throws Exception {
        ClientLogger.log("ClientApp: Running tests on edge cases");

        // PUT Methods to populate
        logAndExecutePut("firstname", "saleh", serverNames[0], rmiPort);
        logAndExecutePut("lastname", "alkhalifa", serverNames[0], rmiPort);
        logAndExecutePut("tvshow", "seinfeld", serverNames[0], rmiPort);
        logAndExecutePut("zipcode", "02148", serverNames[0], rmiPort);
        logAndExecutePut("job", "datascientist", serverNames[0], rmiPort);
        logAndExecutePut("home", "malden", serverNames[0], rmiPort);

        // Put methods on same key
        logAndExecutePut("firstname", "saleh", serverNames[0], rmiPort);
        logAndExecutePut("firstname", "saleh", serverNames[0], rmiPort);

        // GET Method Examples on same key
        logAndExecuteGet("firstname", serverNames[0], rmiPort);
        logAndExecuteGet("firstname", serverNames[0], rmiPort);

        // Get key that does not exist
        logAndExecuteGet("drink", serverNames[0], rmiPort); // Does not exist!!

        // Delete key then get it:
        logAndExecutePut("firstname", "saleh", serverNames[0], rmiPort);
        logAndExecuteDelete("firstname", serverNames[0], rmiPort);
        logAndExecuteGet("firstname", serverNames[0], rmiPort);

        // Delete non-existent key:
        logAndExecuteDelete("123123123", serverNames[0], rmiPort);
    }

    /**
     * Method that starts an interactive session via infinite loop until loop is killed
     */
    private static void interactiveSession(int rmiPort) {

        // Start scanner to parse input from user
        Scanner scanner = new Scanner(System.in);
        ClientLogger.log("############################");
        ClientLogger.log("Interactive session started.");
        ClientLogger.log("Type 'PUT <key> <value>', 'GET <key>', 'DELETE <key>', or 'QUIT' to exit.");
        ClientLogger.log("############################");

        while (true) {
            // Get users command and run it, move to next line
            System.out.print("> Enter Command: ");
            String input = scanner.nextLine();

            // Check if user wants to quit
            if ("quit".equalsIgnoreCase(input.trim())) {
                ClientLogger.log("Exiting interactive session.");
                break;
            }

            handleCommand(input, rmiPort);
        }
        scanner.close();
    }

    /**
     * Method that handles the different methods of PUT, GET, and DELETE,
     * sending each command to a different KVServer.
     */
    private static void handleCommand(String command, int rmiPort) {
        try {
            // Split the command to isolate each item
            String[] parts = command.split(" ", 3);
            switch (parts[0].toUpperCase()) {
                case "PUT":
                    // Check for invalid input
                    if (parts.length < 3) {
                        ClientLogger.log("Invalid PUT command. Correct usage: PUT <key> <value>");
                    } else {
                        // Randomly select a server
                        Random random = new Random();
                        String randomServerName = serverNames[random.nextInt(serverNames.length)];
                        // Lookup the selected server replica in the registry
                        service = (KeyValueInterface) registry.lookup(randomServerName);
                        logAndExecutePut(parts[1], parts[2], randomServerName, rmiPort);
                    }
                    break;
                case "GET":
                    // Check for invalid input
                    if (parts.length < 2) {
                        ClientLogger.log("Invalid GET command. Correct usage: GET <key>");
                    } else {
                        // Randomly select a server
                        Random random = new Random();
                        String randomServerName = serverNames[random.nextInt(serverNames.length)];
                        // Lookup the selected server replica in the registry
                        service = (KeyValueInterface) registry.lookup(randomServerName);
                        logAndExecuteGet(parts[1], randomServerName, rmiPort);
                    }
                    break;
                case "DELETE":
                    // Check for invalid input
                    if (parts.length < 2) {
                        ClientLogger.log("Invalid DELETE command. Correct usage: DELETE <key>");
                    } else {
                        // Randomly select a server
                        Random random = new Random();
                        String randomServerName = serverNames[random.nextInt(serverNames.length)];
                        // Lookup the selected server replica in the registry
                        service = (KeyValueInterface) registry.lookup(randomServerName);
                        logAndExecuteDelete(parts[1], randomServerName, rmiPort);
                    }
                    break;
                default:
                    ClientLogger.log("Unsupported command or Malformed data packet detected. Use PUT, GET, DELETE, followed by keys and values, or QUIT.");
                    break;
            }
        } catch (Exception e) {
            ClientLogger.log("Error handling command: " + command + " - " + e.getMessage());
        }
    }



    /**
     * Helper method to log and execute a PUT method
     * @param key
     * @param value
     * @throws Exception
     */
    private static void logAndExecutePut(String key, String value, String serverName, int port) throws Exception {
        service.put(key, value);
        ClientLogger.log("PUT - Key: " + key + ", Value: " + value + ", Server: " + serverName + ", Port: " + port);
    }

    /**
     * Helper method to log and execute a GET method
     * @param key
     * @throws Exception
     */
    private static void logAndExecuteGet(String key, String serverName, int port) throws Exception {
        String value = service.get(key);
        ClientLogger.log("GET - Key: " + key + ", Value: " + value + ", Server: " + serverName + ", Port: " + port);
//        System.out.println("Value: " + value);
    }

    /**
     * Helper method to log and execute a DELETE method
     * @param key
     * @throws Exception
     */
    private static void logAndExecuteDelete(String key, String serverName, int port) throws Exception {
        service.delete(key);
        ClientLogger.log("DELETE - Key: " + key + ", Server: " + serverName + ", Port: " + port);
    }
}
