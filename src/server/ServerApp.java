package server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * ServerApp containing the backend logic, as well registry and KV service
 */
public class ServerApp {
    public static void main(String[] args) {

        // Set default port
        int port = 1099;

        // If port given, use that instead
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
                ServerLogger.log("Port number provided, using port: " + port);
            } catch (NumberFormatException e) {
                ServerLogger.log("Invalid port number provided, using default: " + port);
            }
        }

        try {
            // Crete a new instance of the service
            KeyValueService service = new KeyValueService();

            // Use 1099 which is default for rmi
            Registry registry = LocateRegistry.createRegistry(port);

            // Rebind registry
            registry.rebind("KeyValueService", service);

            // print for now
            ServerLogger.log("Key-Value Store Service is running on port " + port);
        } catch (Exception e) {
            ServerLogger.log("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
