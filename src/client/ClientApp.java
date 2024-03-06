package client;

import server.KeyValueInterface;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientApp {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            KeyValueInterface service = (KeyValueInterface) registry.lookup("KeyValueService");

            // Pre-populate the store
            ClientLogger.log("Pre-populating the store...");
            service.put("Name", "John Doe");
            service.put("Occupation", "Software Engineer");
            service.put("Language", "Java");
            service.put("Country", "USA");
            service.put("Project", "RMI Key-Value Store");

            // Perform operations
            ClientLogger.log("Performing operations...");
            ClientLogger.log("Name: " + service.get("Name"));
            service.put("Language", "Python");
            ClientLogger.log("Language: " + service.get("Language"));
            service.delete("Country");
            ClientLogger.log("Country key deleted.");
        } catch (Exception e) {
            ClientLogger.log("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
