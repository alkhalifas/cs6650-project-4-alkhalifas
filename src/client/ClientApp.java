package client;

import common.KeyValueInterface;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

/**
 * Client app that uses a rmi registry to handle communication with backend.
 */
public class ClientApp {
    private static KeyValueInterface service;

    public static void main(String[] args) {
        try {

            // Get host name, otherwise default to local host
            String host = args.length > 0 ? args[0] : "localhost";

            // Get port name otherwise detault to 1099
            int rmiPort = args.length > 1 ? Integer.parseInt(args[1]) : 1099;

            // instantiate registry and service using the host and port
            Registry registry = LocateRegistry.getRegistry(host, rmiPort);
            service = (KeyValueInterface) registry.lookup("KeyValueService");


            // Pre-populate the key-value store
            prepopulateClient();

            // Perform additional client operations
            performClientOperations();

            // Interactive session for user commands
            interactiveSession();

        } catch (Exception e) {
            ClientLogger.log("Client exception: " + e.toString());
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
        logAndExecutePut("school", "northeastern");
        logAndExecutePut("pet", "dog");
        logAndExecutePut("height", "6ft");
        logAndExecutePut("phone", "1234567890");
        logAndExecutePut("email", "somewhere@gmail.com");

        logAndExecuteGet("school");
        logAndExecuteGet("pet");
        logAndExecuteGet("height");
        logAndExecuteGet("email");
        logAndExecuteGet("phone");

        logAndExecuteDelete("pet");
        logAndExecuteDelete("school");
        logAndExecuteDelete("email");
        logAndExecuteDelete("height");
        logAndExecuteDelete("phone");


    }

    /**
     * Method that performs many client operations to rest the robustness and error handling of the server
     * @throws Exception
     */
    private static void performClientOperations() throws Exception {
        ClientLogger.log("ClientApp: Running tests on edge cases");

        // PUT Methods to populate
        logAndExecutePut("firstname", "saleh");
        logAndExecutePut("lastname", "alkhalifa");
        logAndExecutePut("tvshow", "seinfeld");
        logAndExecutePut("zipcode", "02148");
        logAndExecutePut("job", "datascientist");
        logAndExecutePut("home", "malden");

        // Put methods on same key
        logAndExecutePut("firstname", "saleh");
        logAndExecutePut("firstname", "saleh");

        // GET Method Examples on same key
        logAndExecuteGet("firstname");
        logAndExecuteGet("firstname");

        // Get key that does not exist
        logAndExecuteGet("drink"); // Does not exist!!

        // Delete key then get it:
        logAndExecutePut("firstname", "saleh");
        logAndExecuteDelete("firstname");
        logAndExecuteGet("firstname");

        // Delete non-existent key:
        logAndExecuteDelete("123123123");

    }

    /**
     * Method that starts an interactive session via infinite loop until loop is killed
     */
    private static void interactiveSession() {

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

            handleCommand(input);
        }
        scanner.close();
    }

    /**
     * Method that handles the differnet methods of PUT, GET and DELETE
     */
    private static void handleCommand(String command) {
        try {

            // Split the command to isolate each item
            String[] parts = command.split(" ", 3);
            switch (parts[0].toUpperCase()) {
                case "PUT":
                    // Check for invalid input
                    if (parts.length < 3) {
                        ClientLogger.log("Invalid PUT command. Correct usage: PUT <key> <value>");
                    } else {
                        logAndExecutePut(parts[1], parts[2]);
                    }
                    break;
                case "GET":
                    // Check for invalid input
                    if (parts.length < 2) {
                        ClientLogger.log("Invalid GET command. Correct usage: GET <key>");
                    } else {
                        logAndExecuteGet(parts[1]);
                    }
                    break;
                case "DELETE":
                    // Check for invalid input
                    if (parts.length < 2) {
                        ClientLogger.log("Invalid DELETE command. Correct usage: DELETE <key>");
                    } else {
                        logAndExecuteDelete(parts[1]);
                    }
                    break;
                default:
                    ClientLogger.log("Unsupported command or Malformed data packet detected. Use PUT, GET, DELETE, followed by keys and valies, or QUIT.");
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
    private static void logAndExecutePut(String key, String value) throws Exception {
        service.put(key, value);
        ClientLogger.log("PUT - Key: " + key + ", Value: " + value);
    }

    /**
     * Helper method to log and execute a GET method
     * @param key
     * @throws Exception
     */
    private static void logAndExecuteGet(String key) throws Exception {
        String value = service.get(key);
        ClientLogger.log("GET - Key: " + key + ", Value: " + value);
//        System.out.println("Value: " + value);
    }

    /**
     * Helper method to log and execute a DELETE method
     * @param key
     * @throws Exception
     */
    private static void logAndExecuteDelete(String key) throws Exception {
        service.delete(key);
        ClientLogger.log("DELETE - Key: " + key);
    }
}
