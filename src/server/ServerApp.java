package server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * ServerApp containing the backend logic, as well registry and KV service
 */
public class ServerApp {
    public static void main(String[] args) {
        try {
            // Crete a new instance of the service
            KeyValueService service = new KeyValueService();

            // Use 1099 which is default for rmi
            Registry registry = LocateRegistry.createRegistry(1099);

            // Rebind registry
            registry.rebind("KeyValueService", service);

            // print for now
            ServerLogger.log("Key-Value Store Service is running");
        } catch (Exception e) {
            ServerLogger.log("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
