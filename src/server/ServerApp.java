package server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * ServerApp containing the backend logic, as well registry and KV service
 */
public class ServerApp {
    public static void main(String[] args) {

        try {
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

            // Create and bind instances of KeyValueService on multiple servers to adhere to reqs of prj3
            // Prj3 new change
            List<KeyValueService> services = new ArrayList<>();

            // Iterate over the services to spin them up
            for (int i = 0; i < 5; i++) {
                // Create a new instance of the service
                KeyValueService service = new KeyValueService();

                // Use the specified port or default (1099)
                Registry registry = LocateRegistry.createRegistry(port + i);

                // Rebind registry
                registry.rebind("KeyValueService", service);

                // add it to the service
                services.add(service);

                // Log to the logger
                ServerLogger.log("Key-Value Store Service is running on port " + (port + i));
            }
        } catch (Exception e) {
            ServerLogger.log("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
