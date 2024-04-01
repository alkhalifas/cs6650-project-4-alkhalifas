// ClientApp.java
package client;

import common.KeyValueInterface;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;
import java.util.Scanner;

public class ClientApp {
    private static final String[] SERVER_NAMES = {"KeyValueServer1", "KeyValueServer2", "KeyValueServer3", "KeyValueServer4", "KeyValueServer5"};
    private static KeyValueInterface[] services;

    public static void main(String[] args) {
        try {
            String host = args.length > 0 ? args[0] : "localhost";
            Registry registry = LocateRegistry.getRegistry(host);

            services = new KeyValueInterface[5]; // Use KeyValueInterface instead of KeyValueService

            for (int i = 0; i < 5; i++) {
                services[i] = (KeyValueInterface) registry.lookup(SERVER_NAMES[i]); // Cast to KeyValueInterface
                ClientLogger.log("Successfully found." + i + "service");
            }

            ClientLogger.log("Successfully connected to the server.");

            // Interactive session for user commands
            interactiveSession();
        } catch (Exception e) {
            ClientLogger.log("Error during server lookup: " + e.getMessage());
            e.printStackTrace();
        }
    }

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

    private static void handleCommand(String command) {
        try {
            String[] parts = command.split(" ", 3);
            Random random = new Random();
            int randomServerIndex = random.nextInt(5);
            KeyValueInterface service = services[randomServerIndex]; // Use KeyValueInterface

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
