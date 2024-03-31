package server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
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

            // Create and bind instances of KeyValueService on a single registry with different names
            Registry registry = LocateRegistry.createRegistry(port);

            // Bind each server replica with a unique name
            for (int i = 1; i <= 5; i++) {
                KeyValueService service = new KeyValueService();
                String serverName = "KeyValueServer" + i;
                registry.rebind(serverName, service);
                ServerLogger.log("Key-Value Store Service " + serverName + " is running on port " + port);
            }
        } catch (Exception e) {
            ServerLogger.log("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
