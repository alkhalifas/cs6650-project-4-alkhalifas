package main;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import coordinator.Coordinator;
import server.Server;

/**
 * The main class responsible for setting up the RMI-based server-client architecture.
 */
public class ServerMain {
    // Host and port information for the coordinator
    private static final String coordinatorHost = "localhost";
    private static final int coordinatorPort = 1100;

    // Host information for servers
    private static final String serverHost = "localhost";

    // Port information for individual servers
    private static final int server1Port = 1101;
    private static final int server2Port = 1102;
    private static final int server3Port = 1103;
    private static final int server4Port = 1104;
    private static final int server5Port = 1105;

    // Service names for the coordinator and servers
    private static final String coordinatorService = "Coordinator";
    private static final String serverService = "ServerService";

    // Number of servers
    private static final int numServers = 5;

    /**
     * The main method that initializes and starts the RMI servers.
     *
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        try {
            // Create and bind the coordinator to its registry
            Registry coordinatorRegistry = LocateRegistry.createRegistry(coordinatorPort);
            Coordinator coordinator = new Coordinator();
            coordinatorRegistry.rebind(coordinatorService, coordinator);

            // Create and bind each server to its registry
            for (int i = 0; i < numServers; i++) {
                int port = 1101 + i;
                Server server = new Server(coordinatorHost, coordinatorPort, port);
                Registry serverRegistry = LocateRegistry.createRegistry(port);
                serverRegistry.rebind(serverService, server);
            }

            // Connect the coordinator to the servers
            coordinator.connectToServers(
                    Arrays.asList(serverHost, serverHost, serverHost, serverHost, serverHost),
                    Arrays.asList(server1Port, server2Port, server3Port, server4Port, server5Port)
            );

            // Print a message indicating that all servers are provisioned and ready
            System.out.println("##########################################");
            System.out.println("> All 5 servers are provisioned and ready! ");
            System.out.println("##########################################");
        } catch (RemoteException e) {
            // Handle RemoteException gracefully
            System.err.println("> Error: RMI failure in ServerMain: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
