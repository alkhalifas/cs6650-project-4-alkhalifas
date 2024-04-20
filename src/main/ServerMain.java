package main;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import server.IAcceptor;
import server.ILearner;
import server.Node;

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

            Node[] nodes = new Node[totalNodes];

            // Initialize each node, create its registry, and bind it to the registry
            // todo: Check docs for better way of binding?

            for (int nodeId = 0; nodeId < totalNodes; nodeId++) {
                int port = startingPort + nodeId;

                nodes[nodeId] = new Node(nodeId, port, totalNodes - 1);

                // Create a registry for each node on its specific port
                Registry registry = LocateRegistry.createRegistry(port);

                // Bind the node to its registry with a unique name
                registry.rebind("KVStore" + nodeId, nodes[nodeId]);

                // Store the registry in the node for later use
                nodes[nodeId].setRegistry(registry);
            }

            // Setup acceptors and learners for each node to communicate with other nodes
            for (int nodeId = 0; nodeId < totalNodes; nodeId++) {
                List<IAcceptor> acceptors = new ArrayList<>(totalNodes - 1);
                List<ILearner> learners = new ArrayList<>(totalNodes - 1);
                for (int otherNodeId = 0; otherNodeId < totalNodes; otherNodeId++) {
                    if (otherNodeId != nodeId) {
                        acceptors.add(nodes[otherNodeId]);
                        learners.add(nodes[otherNodeId]);
                    }
                }
                // Set the lists of acceptors and learners, excluding the node itself
                nodes[nodeId].setAcceptors(acceptors);
                nodes[nodeId].setLearners(learners);
            }
            System.out.println("> Nodes ready...");
        } catch (RemoteException e) {
            // Handle RemoteException that could occur during the RMI setup
            System.err.println("> Error: The registry could not be created due to RMI error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
