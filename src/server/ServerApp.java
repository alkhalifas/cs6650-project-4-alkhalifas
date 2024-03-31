// ServerApp.java
package server;

import common.KeyValueService;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerApp {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(1099); // Create a single registry

            for (int i = 1; i <= 5; i++) {
                KeyValueService service = new KeyValueService();
                String serverName = "KeyValueServer" + i;
                registry.rebind(serverName, service);
                ServerLogger.log("Key-Value Store Service " + serverName + " is running.");
            }
        } catch (RemoteException e) {
            ServerLogger.log("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
