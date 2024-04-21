//package coordinator;
//
//import java.rmi.ConnectException;
//import java.rmi.NotBoundException;
//import java.rmi.server.UnicastRemoteObject;
//import java.rmi.RemoteException;
//import java.rmi.registry.LocateRegistry;
//import java.rmi.registry.Registry;
//import java.util.ArrayList;
//import java.util.List;
//
//import utils.ILogger;
//import utils.Logger;
//
///**
// * Represents a coordinator that communicates via Remote Method Invocation (RMI).
// */
//public class Coordinator extends UnicastRemoteObject implements ICoordinator {
//    private static final String loggerName = "CoordinatorLogger";
//    private static final String logFileName = "CoordinatorLog.log";
//    private static final int numServers = 5;
//    private static final String service = "ServerService";
//    private final List<IServer> servers;
//    private final List<Registry> serverRegistries;
//    private String state = "INITIAL"; // not used in this simplified version
//    private final ILogger logger;
//
//    /**
//     * Instantiates a new Coordinator.
//     *
//     * @throws RemoteException if an RMI-related error occurs during the instantiation
//     */
//    public Coordinator() throws RemoteException {
//        super();
//        this.logger = new Logger(loggerName, logFileName);
//        this.servers = new ArrayList<>(numServers);
//        this.serverRegistries = new ArrayList<>(numServers);
//        // Timeout mechanism
//        System.setProperty("sun.rmi.transport.tcp.responseTimeout", "2000");
//        System.setProperty("sun.rmi.transport.proxy.connectTimeout", "5000");
//    }
//
//    /**
//     * Connects to the servers.
//     *
//     * @param serverHosts the hostnames of the servers
//     * @param serverPorts the port numbers of the servers
//     * @throws RemoteException if an RMI-related error occurs during the connection
//     */
//    @Override
//    public void connectToServers(List<String> serverHosts, List<Integer> serverPorts) throws RemoteException {
//        for (int i = 0; i < serverHosts.size(); i++) {
//            try {
//                Registry registry = LocateRegistry.getRegistry(serverHosts.get(i), serverPorts.get(i));
//                this.logger.log("> Connection to the remote object Registry on the host " + serverHosts.get(i) + " and port " + serverPorts.get(i) + " created");
//                IServer server = (IServer) registry.lookup(service);
//                this.logger.log("> Connection bound to " + service + " in the remote object Registry");
//                this.servers.add(server); // add server
//                this.serverRegistries.add(registry); // add servers' registries for shutdown purposes
//                this.logger.log("> Server " + serverHosts.get(i) + " at port " + serverPorts.get(i) + " added to the list");
//            } catch (ConnectException ce) { // connection times out
//                this.logger.log("> Connection to server " + serverHosts.get(i) + " at port " + serverPorts.get(i) + " timed out: " + ce.getMessage());
//            } catch (Exception e) { // general error
//                this.logger.log("> Unable to connect to server " + serverHosts.get(i) + " at port " + serverPorts.get(i) + ": " + e.getMessage());
//                throw new RemoteException("Unable to connect to server " + serverHosts.get(i) + " at port " + serverPorts.get(i), e);
//            }
//        }
//    }
//
//    // First phase of the two-phase commit protocol.
//    private boolean phaseOne(String operation, String key) throws RemoteException {
//        this.logger.log("begin_commit");
//        for (IServer server : this.servers) {
//            try {
//                if (!server.isReady(operation, key)) { // if only one server aborts
//                    this.logger.log("abort: server at port " + server.getPort() + " aborted the transaction");
//                    this.state = "ABORT";
//                    this.logger.log("end_of_transaction");
//                    return false; // none of the servers commit
//                }
//            } catch (ConnectException e) { // connection times out
//                this.logger.log("Communication with the server at port " + server.getPort() + " timed out (phase 1): " + e.getMessage());
//                return false; // should have a retry mechanism
//            }
//        }
//        return true;
//    }
//
//    // Overloaded second phase of the two-phase commit protocol (PUT).
//    private void phaseTwo(String key, String value) throws RemoteException {
//        this.state = "COMMIT";
//        this.logger.log("commit");
//        for (IServer server : this.servers) {
//            try {
//                server.commit(key, value); // every server commits
//            } catch (ConnectException e) { // connection times out
//                this.logger.log("Communication with the server at port " + server.getPort() + " timed out (phase 2): " + e.getMessage()); // should have a retry mechanism
//            }
//        }
//    }
//
//    // Overloaded second phase of the two-phase commit protocol (DELETE).
//    private void phaseTwo(String key) throws RemoteException {
//        this.state = "COMMIT";
//        this.logger.log("commit");
//        for (IServer server : this.servers) {
//            try {
//                server.commit(key); // every server commits
//            } catch (ConnectException e) { // connection times out
//                this.logger.log("Communication with the server at port " + server.getPort() + " timed out (phase 2): " + e.getMessage()); // should have a retry mechanism
//            }
//        }
//    }
//
//    // Check to ensure servers have actually committed.
//    private boolean getAck() throws RemoteException {
//        for (IServer server : this.servers) {
//            try {
//                if (server.getState().equals("INITIAL")) { // the server's state is back to initial after passing through the commit state
//                    this.logger.log("server at port " + server.getPort() + " committed");
//                } else {
//                    this.logger.log("server at port " + server.getPort() + " did not commit");
//                    this.state = "INITIAL";
//                    this.logger.log("end_of_transaction");
//                    return false; // should have a mechanism to roll back commits from every server
//                }
//            } catch (ConnectException e) { // connection times out
//                this.logger.log("Communication with the server at port " + server.getPort() + " timed out (ack): " + e.getMessage()); // should have a retry mechanism
//            }
//        }
//        return true;
//    }
//
//    /**
//     * Initiates the two-phase protocol for putting a key-value pair.
//     *
//     * @param operation the operation
//     * @param key       the key
//     * @param value     the value
//     * @return true if the transaction is successful, otherwise false
//     * @throws RemoteException if an RMI-related error occurs during the operation
//     */
//    @Override
//    public synchronized boolean prepareTransaction(String operation, String key, String value) throws RemoteException {
//        this.logger.log("Received a request to save " + "\"" + key + "\"" + " mapped to " + "\"" + value + "\"");
//        // 1st phase
//        if (!this.phaseOne(operation, key)) { // a server aborts
//            return false;
//        }
//        this.state = "WAIT";
//        // 2nd phase
//        this.phaseTwo(key, value);
//        if (!this.getAck()) { // a server didn't commit
//            return false;
//        }
//        this.state = "INITIAL";
//        this.logger.log("end_of_transaction");
//        return true;
//    }
//
//    /**
//     * Initiates the two-phase protocol for deleting a key-value pair.
//     *
//     * @param operation the operation
//     * @param key       the key
//     * @return true if the transaction is successful, otherwise false
//     * @throws RemoteException if an RMI-related error occurs during the operation
//     */
//    @Override
//    public synchronized boolean prepareTransaction(String operation, String key) throws RemoteException {
//        this.logger.log("Received a request to delete the key-value pair associated with the key: \"" + key + "\"");
//        // 1st phase
//        if (!this.phaseOne(operation, key)) { // a server aborts
//            return false;
//        }
//        this.state = "WAIT";
//        // 2nd phase
//        this.phaseTwo(key);
//        if (!this.getAck()) { // a server didn't commit
//            return false;
//        }
//        this.state = "INITIAL";
//        this.logger.log("end_of_transaction");
//        return true;
//    }
//
//    /**
//     * Stops the server and this coordinator.
//     *
//     * @param registry the coordinator's registry
//     * @throws RemoteException if an RMI-related error occurs during the operation
//     */
//    @Override
//    public void shutdown(Registry registry) throws RemoteException {
//        this.logger.log("Received a request to shut down...");
//        System.out.println("servers are shutting down...");
//        for (int i = 0; i < this.servers.size(); i++) { // shut down all the servers
//            try {
//                this.servers.get(i).shutdown(this.serverRegistries.get(i));
//            } catch (ConnectException ce) { // connection times out
//                this.logger.log("Communication with the server at port " + this.servers.get(i).getPort() + " timed out while attempting to shut it down: " + ce.getMessage()); // should have a retry mechanism
//            }
//        }
//        this.logger.log("servers closed");
//        System.out.println("servers closed");
//        try {
//            registry.unbind("Coordinator"); // unbind this remote object from the custom name
//            this.logger.log("Coordinator unbound in registry");
//        } catch (NotBoundException nbe) { // custom name wasn't bound
//            this.logger.log("Unbind error: " + nbe.getMessage());
//            nbe.printStackTrace();
//            System.exit(1);
//        }
//        UnicastRemoteObject.unexportObject(this, true); // unexport this remote object
//        this.logger.log("Coordinator unexported");
//        this.logger.log("Coordinator closed");
//        this.logger.close();
//        System.out.println("Coordinator closed");
//    }
//}
