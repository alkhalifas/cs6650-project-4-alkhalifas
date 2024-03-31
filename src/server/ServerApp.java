// ServerApp.java
package server;
import common.KeyValueService;
import java.util.concurrent.ConcurrentHashMap;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class ServerApp {
    public static void main(String[] args) {
        try {
            int port = 1099;

            if (args.length > 0) {
                try {
                    port = Integer.parseInt(args[0]);
                    ServerLogger.log("Port number provided, using port: " + port);
                } catch (NumberFormatException e) {
                    ServerLogger.log("Invalid port number provided, using default: " + port);
                }
            }

            // Create and bind instances of KeyValueService on separate registries for each replica
            for (int i = 1; i <= 5; i++) {
                Registry registry = LocateRegistry.createRegistry(port + i); // Adjust port for each replica
                KeyValueService service = new KeyValueService();
                String serverName = "KeyValueServer" + i;
                registry.rebind(serverName, service);
                ServerLogger.log("Key-Value Store Service " + serverName + " is running on port " + (port + i));
            }
        } catch (Exception e) {
            ServerLogger.log("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
