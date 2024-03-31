package client;

import common.KeyValueService;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;
import java.util.Scanner;

/**
 * Client app that uses an RMI registry to handle communication with backend.
 */
public class ClientApp {
    private static final String[] SERVER_NAMES = {"KeyValueService1", "KeyValueService2", "KeyValueService3", "KeyValueService4", "KeyValueService5"};
    private static KeyValueService[] services;

    public static void main(String[] args) {
        try {
            String host = args.length > 0 ? args[0] : "localhost";
            Registry registry = LocateRegistry.getRegistry(host);

            services = new KeyValueService[5];

            for (int i = 0; i < 5; i++) {
                services[i] = (KeyValueService) registry.lookup(SERVER_NAMES[i]);
            }

            ClientLogger.log("Successfully connected to the server.");

            // Interactive session for user commands
            interactiveSession();
        } catch (Exception e) {
            ClientLogger.log("Error during server lookup: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Method that starts an interactive session via infinite loop until loop is killed
     */
    private static void interactiveSession() {
        Scanner scanner = new Scanner(System.in);
        ClientLogger.log("Interactive session started.");
        ClientLogger.log("Type 'PUT <key> <value>', 'GET <key>', 'DELETE <key>', or 'QUIT' to exit.");

        while (true) {
            System.out.print("> Enter Command: ");
            String input = scanner.nextLine();

            if ("quit".equalsIgnoreCase(input.trim())) {
                ClientLogger.log("Exiting interactive session.");
                break;
            }

            handleCommand(input);
        }

        scanner.close();
    }

    /**
     * Method that handles the different methods of PUT, GET, and DELETE,
     * sending each command to a different KVServer.
     */
    private static void handleCommand(String command) {
        try {
            String[] parts = command.split(" ", 3);
            Random random = new Random();
            int randomServerIndex = random.nextInt(5);
            KeyValueService service = services[randomServerIndex];

            switch (parts[0].toUpperCase()) {
                case "PUT":
                    if (parts.length < 3) {
                        ClientLogger.log("Invalid PUT command. Correct usage: PUT <key> <value>");
                    } else {
                        service.put(parts[1], parts[2]);
                    }
                    break;
                case "GET":
                    if (parts.length < 2) {
                        ClientLogger.log("Invalid GET command. Correct usage: GET <key>");
                    } else {
                        String value = service.get(parts[1]);
                        ClientLogger.log("GET - Key: " + parts[1] + ", Value: " + value);
                    }
                    break;
                case "DELETE":
                    if (parts.length < 2) {
                        ClientLogger.log("Invalid DELETE command. Correct usage: DELETE <key>");
                    } else {
                        service.delete(parts[1]);
                    }
                    break;
                default:
                    ClientLogger.log("Unsupported command. Use PUT, GET, DELETE, or QUIT.");
                    break;
            }
        } catch (RemoteException e) {
            ClientLogger.log("RemoteException while handling command: " + command + " - " + e.getMessage());
        }
    }

}
