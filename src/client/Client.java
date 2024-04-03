package client;

import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import server.ServerService;
import utils.ILogger;
import utils.Logger;

/**
 * The type Client represents a client that communicates via Remote Method Invocation (RMI).
 */
public class Client implements IClient {
    // Constants for the default host and ports of the servers
    private static final String host1 = "localhost";
    private static final int port1 = 1101;
    private static final int port2 = 1102;
    private static final int port3 = 1103;
    private static final int port4 = 1104;
    private static final int port5 = 1105;
    // Logger configuration
    private static final String loggerName = "ClientLogger";
    private static final String logFileName = "ClientLog.log";
    // Service name
    private static final String service = "ServerService";
    // Scanner for user input
    private final Scanner scanner;
    // Lists to hold server hosts and ports
    private final List<String> replicaHosts;
    private final List<Integer> replicaPorts;
    // Reference to the ServerService server
    private ServerService server;
    // Logger instance
    private final ILogger logger;

    /**
     * Instantiates a new Client.
     */
    public Client() {
        this.logger = new Logger(loggerName, logFileName); // instantiate a logging system that already is thread-safe
        this.scanner = new Scanner(System.in); // instantiate an object to get user input
        this.replicaHosts = new ArrayList<>();
        this.replicaPorts = new ArrayList<>();
        // Adding server hosts and ports to the lists
        this.replicaHosts.add(host1); // server 1
        this.replicaHosts.add(host1); // server 2
        this.replicaHosts.add(host1); // server 3
        this.replicaHosts.add(host1); // server 4
        this.replicaHosts.add(host1); // server 5
        this.replicaPorts.add(port1); // server 1's port number
        this.replicaPorts.add(port2); // server 2's port number
        this.replicaPorts.add(port3); // server 3's port number
        this.replicaPorts.add(port4); // server 4's port number
        this.replicaPorts.add(port5); // server 5's port number
        // Timeout mechanism
        System.setProperty("sun.rmi.transport.tcp.responseTimeout", "2000");
        System.setProperty("sun.rmi.transport.proxy.connectTimeout", "5000");
        this.connectToRandomReplica(); // connect to a random server
    }

    // Connects to a random server
    private void connectToRandomReplica() {
        try {
            Random random = new Random();
            int randomIndex = random.nextInt(this.replicaHosts.size());
            Registry registry = LocateRegistry.getRegistry(this.replicaHosts.get(randomIndex), this.replicaPorts.get(randomIndex)); // connect to a random server
            this.server = (ServerService) registry.lookup(service); // look up the registry for the remote object
            this.logger.log("> Successfully connected to server " + this.replicaHosts.get(randomIndex) + " at port " + this.replicaPorts.get(randomIndex));
        } catch (ConnectException ce) { // connection times out
            this.logger.log("> Error. Connection to server timed out: " + ce.getMessage());
            System.err.println("> Error. Connection to server timed out: " + ce.getMessage());
            this.logger.close();
            this.scanner.close();
            System.exit(1);
        } catch (RemoteException re) { // registry not found
            this.logger.log("> Error. Cannot connect to server: registry not found");
            System.err.println("> Error. Cannot connect to server: registry not found");
            this.logger.close();
            this.scanner.close();
            System.exit(1);
        } catch (NotBoundException nbe) { // service not bound in registry
            this.logger.log("> Error. Cannot connect to server: ServerService not bound");
            System.err.println("> Error. Cannot connect to server: ServerService not bound");
            this.logger.close();
            this.scanner.close();
            System.exit(1);
        }
    }

    /**
     * Pre-populates the key-value store.
     */
    @Override
    public void prePopulate() {
        try {
            this.logger.log("Pre-populating...");
            System.out.println("Pre-populating...");
            // Pre-populating key-value pairs
            System.out.println(this.server.put("name", "saleh"));
            System.out.println(this.server.put("color", "blue"));
            System.out.println(this.server.put("thank you", "grazie"));
            System.out.println(this.server.put("please", "per favore"));
            System.out.println(this.server.put("yes", "sì"));
            System.out.println(this.server.put("no", "no"));
            System.out.println(this.server.put("water", "acqua"));
            System.out.println(this.server.put("food", "cibo"));
            System.out.println(this.server.put("friend", "amico"));
            System.out.println(this.server.put("love", "amore"));
            this.logger.log("Pre-population completed");
            System.out.println("Pre-population completed");
            Thread.sleep(1000); // wait a second before user interaction


        } catch (ConnectException ce) { // connection times out
            this.logger.log("ServerService timed out (pre-populate): " + ce.getMessage());
            System.err.println("ServerService timed out (pre-populate): " + ce.getMessage());
        } catch (RemoteException re) { // RMI failure
            this.logger.log("ServerService error (pre-populate): " + re.getMessage());
            System.err.println("ServerService error (pre-populate): " + re.getMessage());
        } catch (InterruptedException ie) { // thread is prematurely resumed
            this.logger.log("Pre-population error (timeout interrupted): " + ie.getMessage());
            System.err.println("Pre-population error (timeout interrupted): " + ie.getMessage());
        }
    }

    /**
     * Gets the user request.
     *
     * @return the user request
     */
    @Override
    public String getRequest() {
        System.out.print("Enter operation (PUT/GET/DELETE:key:value[only with PUT]): ");
        return this.scanner.nextLine();
    }

    // Parses the user request and executes the appropriate operation
    private String parseRequest(String request) {
        String result;
        String[] elements = request.split(":");
        if (elements.length < 2 || elements.length > 3) { // the protocol is not followed
            this.logger.log("Received malformed request: " + request);
            return "FAIL: please follow the predefined protocol PUT/GET/DELETE:key:value[with PUT only] and try again";
        } else {
            String operation;
            try {
                operation = elements[0].toUpperCase(); // PUT/GET/DELETE
            } catch (Exception e) {
                this.logger.log("Parsing error: invalid operation");
                return "FAIL: could not parse the operation requested. Please follow the predefined protocol PUT/GET/DELETE:key:value[with PUT only] and try again";
            }
            String key;
            try {
                key = elements[1].toLowerCase(); // word to be translated
            } catch (Exception e) {
                this.logger.log("Parsing error: invalid key");
                return "FAIL: could not parse the key requested. Please follow the predefined protocol PUT/GET/DELETE:key:value[with PUT only] and try again";
            }
            String value;
            try {
                switch (operation) {
                    case "PUT":
                        try {
                            value = elements[2].toLowerCase(); // word to translate
                            this.logger.log("Received a request to save " + "\"" + key + "\"" + " mapped to " + "\"" + value + "\"");
                        } catch (Exception e) {
                            this.logger.log("Parsing error: invalid value");
                            return "FAIL: could not parse the value requested. Please follow the predefined protocol PUT/GET/DELETE:key:value[with PUT only] and try again";
                        }
                        result = this.server.put(key, value);
                        break;
                    case "GET":
                        result = this.server.get(key);
                        if (result.startsWith("FAIL:")) {
                            this.logger.log("Received a request to retrieve the value mapped to a nonexistent key: \"" + key + "\"");
                        } else {
                            this.logger.log("Received a request to retrieve the value mapped to \"" + key + "\"");
                        }
                        break;
                    case "DELETE":
                        result = this.server.delete(key);
                        if (result.startsWith("FAIL:")) {
                            this.logger.log("Received a request to delete a nonexistent key-value pair associated with the key: \"" + key + "\"");
                        } else {
                            this.logger.log("Received a request to delete the key-value pair associated with the key: \"" + key + "\"");
                        }
                        break;
                    default: // invalid request
                        this.logger.log("Received an invalid request: " + request);
                        return "Invalid request. Please follow the predefined protocol PUT/GET/DELETE:key:value[with PUT only] and try again";
                }
            } catch (ConnectException ce) { // connection times out
                this.logger.log("ServerService timed out: " + ce.getMessage());
                result = "ServerService timed out: " + ce.getMessage();
            } catch (RemoteException re) { // RMI failure
                this.logger.log("ServerService error: " + re.getMessage());
                result = "ServerService error: " + re.getMessage();
            }
        }
        this.logger.log("Reply: " + result);
        return result;
    }

    /**
     * Starts the client.
     */
    @Override
    public void execute() {
        boolean isRunning = true;
        this.logger.log("Client is running...");
        while (isRunning) { // keep getting user input
            String request = this.getRequest(); // get the user request
            if (request.equalsIgnoreCase("shutdown") || request.equalsIgnoreCase("stop")) { // if the user wants to quit
                isRunning = false; // prepare the shutdown process
            } else {
                System.out.println(this.parseRequest(request)); // process the request and output the result
            }
        }
        this.shutdown(); // shut down the servers and the client
    }

    /**
     * Stops this client and the servers.
     */
    @Override
    public void shutdown() {
        this.logger.log("Received a request to shut down...");
        System.out.println("Client is shutting down...");
        try {
            this.server.shutdown(); // shut down the server
            this.logger.log("ServerService closed");
        } catch (ConnectException ce) { // connection times out
            this.logger.log("ServerService timed out (shutdown): " + ce.getMessage());
            System.err.println("ServerService timed out (shutdown): " + ce.getMessage());
        } catch (RemoteException re) { // RMI failure
            this.logger.log("ServerService error (shutdown): " + re.getMessage());
            System.err.println("ServerService error (shutdown): " + re.getMessage());
        }
        this.scanner.close();
        this.logger.log("Client closed");
        this.logger.close();
        System.out.println("Client closed");
    }
}

