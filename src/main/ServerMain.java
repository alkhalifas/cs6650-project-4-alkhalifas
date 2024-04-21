package main;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import server.IAcceptor;
import server.ILearner;
import server.Server;

/**
 * ServerMain class initializes a set of distributed nodes and configures them
 * as part of a PAXOS.
 */
public class ServerMain {

    /**
     * The main method initializes and starts server
     */
    public static void main(String[] args) {
        try {
            // Assign nodes and ports
            int totalNodes = 5;
            int startingPort = 1100;

            Server[] nodes = new Server[totalNodes];

            // Initialize each node, create its registry, and bind it to the registry
            // todo: Check docs for better way of binding?

            for (int serverId = 0; serverId < totalNodes; serverId++) {
                int port = startingPort + serverId;

                nodes[serverId] = new Server(serverId, port, totalNodes - 1);

                // Create a registry for each node on its specific port
                Registry registry = LocateRegistry.createRegistry(port);

                // Bind the node to its registry with a unique name
                registry.rebind("KVStore" + serverId, nodes[serverId]);

                // Store the registry in the node for later use
                nodes[serverId].setRegistry(registry);
            }

            // Setup acceptors and learners for each node to communicate with other nodes
            for (int serverId = 0; serverId < totalNodes; serverId++) {
                List<IAcceptor> acceptors = new ArrayList<>(totalNodes - 1);
                List<ILearner> learners = new ArrayList<>(totalNodes - 1);
                for (int otherserverId = 0; otherserverId < totalNodes; otherserverId++) {
                    if (otherserverId != serverId) {
                        acceptors.add(nodes[otherserverId]);
                        learners.add(nodes[otherserverId]);
                    }
                }
                // Set the lists of acceptors and learners, excluding the node itself
                nodes[serverId].setServerAcceptors(acceptors);
                nodes[serverId].setServerLearners(learners);
            }
            System.out.println("> Server is online and all nodes are ready...");
        } catch (RemoteException e) {
            // Handle RemoteException that could occur during the RMI setup
            System.err.println("> Error: The registry could not be created due to RMI error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
