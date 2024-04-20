package client;

import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;
import server.IStore;
import utils.ILogger;
import utils.Logger;

/**
 * The Client class manages communication with a distributed key-value store
 * over RMI. It handles setup, command processing, and shutdown procedures.
 */
public class Client implements IClient {

    // Base port number to calculate other port numbers dynamically
    private static final int BASE_PORT = 1100;

    // Client logger for logging events
    private final ILogger logger;
    private final Scanner scanner;
    private IStore server;

    /**
     * Constructs a Client object.
     *
     * @param host The hostname of the server.
     * @param port The port number where the server is accessible.
     */
    public Client(String host, int port) {
        // Setting timeout properties for the RMI transport layer per specs in assignment
        System.setProperty("sun.rmi.transport.tcp.responseTimeout", "2000");
        System.setProperty("sun.rmi.transport.proxy.connectTimeout", "5000");

        this.logger = new Logger("> Client Log", "ClientLog.log");
        this.scanner = new Scanner(System.in);

        // Prepare the RMI URL based on the host and port provided so that its dynamically made
        String str_port = Integer.toString(port);
        String url = "rmi://" + host + ":" + port + "/KVStore" + str_port.charAt(str_port.length() - 1);
        try {
            this.server = (IStore) Naming.lookup(url);
        } catch (NotBoundException nbe) {
            // Handling cases where the server is not bound in the RMI registry
            handleInitializationError("> Error: Error connecting to node at port " + port + ": " + url, nbe);
        } catch (MalformedURLException mue) {
            // Handling malformed URL exceptions
            handleInitializationError(url + " is not formatted correctly", mue);
        } catch (ConnectException ce) {
            // Handling connection timeout exceptions
            handleInitializationError("> Error: The server connection timed out", ce);
        } catch (RemoteException re) {
            // Handling generic RMI remote exceptions
            handleInitializationError("> Error: Not able to connect to server: registry not found", re);
        }
    }

    /**
     * Handles initialization errors by logging and exiting the program.
     *
     * @param message The error message to log and display.
     * @param e The exception associated with the error.
     */
    private void handleInitializationError(String message, Exception e) {
        this.logger.log("> Error: " + message + " \n" + e.getMessage());
        System.err.println("> Error: " + message + " \n" + e.getMessage());
        this.logger.close();
        this.scanner.close();
        System.exit(1);
    }

    /**
     * Pre-populates the key-value store with some default data.
     */
    @Override
    public void prePopulate() {
        try {
            this.logger.log("> Pre-populating database started");
            System.out.println("> Pre-populating database started");
            // Example data being put into the store
            System.out.println(this.server.put("name", "saleh"));
            System.out.println(this.server.put("car", "nissan"));
            System.out.println(this.server.put("city", "malden"));
            System.out.println(this.server.put("device", "iphone"));
            System.out.println(this.server.put("laptop", "macbook"));
            System.out.println(this.server.put("zipcode", "02148"));
            System.out.println(this.server.put("state", "massachusetts"));
            this.logger.log("> Pre-populating database done");
            System.out.println("> Pre-populating database done");
            Thread.sleep(1000);
        } catch (ConnectException ce) {
            handleConnectionError("> Error: Connection timed out in pre-populate", ce);
        } catch (RemoteException re) {
            handleConnectionError("> Error: Connection error in pre-populate", re);
        } catch (InterruptedException ie) {
            handleConnectionError("> Error: Pre-population error from timeout interrupted", ie);
        }
    }

    /**
     * Handles connection errors by logging and displaying an error message.
     *
     * @param message The error message to log and display.
     * @param e The exception associated with the error.
     */
    private void handleConnectionError(String message, Exception e) {
        this.logger.log("> Error: " + message + ": " + e.getMessage());
        System.err.println("> Error: " + message + ": " + e.getMessage());
    }

    /**
     * Retrieves a request from the user through the command line interface.
     *
     * @return The command entered by the user.
     */
    @Override
    public String getRequest() {
        System.out.print("> Enter operation (PUT/GET/DELETE:key:value[only with PUT]): ");
        return this.scanner.nextLine();
    }

    /**
     * Parses and executes the user request.
     *
     * @param request The request string to parse and execute.
     * @return The result of the request execution.
     */
    private String parseRequest(String request) {
        String[] elements = request.split(":");
        if (elements.length < 2 || elements.length > 3) {
            return logAndReturnError("> Error: Received malformed request: " + request, "Check to make sure you follow the predefined protocol PUT/GET/DELETE:key:value[with PUT only] and try again");
        }
        String operation;
        String key;
        String value;
        try {
            operation = elements[0].toUpperCase();
            key = elements[1].toLowerCase();
            switch (operation) {
                case "PUT":
                    if (elements.length < 3) {
                        return logAndReturnError("Parsing error because of invalid value", "Error parsing the value requested. Please follow the predefined protocol PUT/GET/DELETE:key:value[with PUT only] and try again");
                    }
                    value = elements[2].toLowerCase();
                    this.logger.log("> Received a request to save " + "\"" + key + "\"" + " mapped to " + "\"" + value + "\"");
                    return this.server.put(key, value);
                case "GET":
                    return handleGetOperation(key);
                case "DELETE":
                    this.logger.log("> Received a request to delete the key-value pair associated with the key: \"" + key + "\"");
                    return this.server.delete(key);
                default:
                    return logAndReturnError("Received an invalid request: " + request, "Invalid request, must follow predefined protocol PUT/GET/DELETE:key:value[with PUT only] and try again");
            }
        } catch (Exception e) {
            return handleExceptionDuringRequest("Server error during request processing", e);
        }
    }

    /**
     * Logs an error and returns a formatted error message.
     *
     * @param logMessage The message to log.
     * @param errorMessage The error message to return.
     * @return The formatted error message.
     */
    private String logAndReturnError(String logMessage, String errorMessage) {
        this.logger.log("> Error: " + logMessage);
        return "> Error: " + errorMessage;
    }

    /**
     * Handles the 'GET' operation.
     *
     * @param key The key for which to retrieve the value.
     * @return The value associated with the key, or an error message.
     */
    private String handleGetOperation(String key) {
        try {
            String result = this.server.get(key);
            if (result == null) {
                this.logger.log("> Error: Server received a nonexistent key: \"" + key + "\"");
                return "> Error: Error running the mapping for " + "\"" + key + "\"";
            }
            this.logger.log("> Received request to retrieve the value mapped to \"" + key + "\"");
            return result;
        } catch (ConnectException ce) {
            return handleExceptionDuringRequest("Server timed out", ce);
        } catch (RemoteException re) {
            return handleExceptionDuringRequest("RMI error", re);
        }
    }

    /**
     * Handles exceptions that occur during request processing by logging and formatting an error message.
     *
     * @param message The error message to display.
     * @param e The exception that occurred.
     * @return A formatted error message based on the exception.
     */
    private String handleExceptionDuringRequest(String message, Exception e) {
        this.logger.log("> Error: " + message + ": " + e.getMessage());
        return "> Error: " + message + ": " + e.getMessage();
    }

    /**
     * Main execution loop for handling user commands.
     */
    @Override
    public void execute() {
        boolean isRunning = true;
        this.logger.log("> Client is running...");
        while (isRunning) {
            String request = this.getRequest();
            if ("shutdown".equalsIgnoreCase(request) || "stop".equalsIgnoreCase(request)) {
                isRunning = false;
            } else if ("pp".equalsIgnoreCase(request)) {
                this.prePopulate();
            } else {
                System.out.println(this.parseRequest(request));
            }
        }
        this.shutdown();
    }

    /**
     * Shuts down the client and all connections.
     */
    @Override
    public void shutdown() {
        this.logger.log("> Received a request to shut down...");
        System.out.println("> Client is shutting down...");
        try {
            // Attempting to shut down each node
            for (int nodeId = 0; nodeId < 5; nodeId++) {
                int port = BASE_PORT + nodeId;
                String str_port = Integer.toString(port);
                String url = "rmi://localhost:" + port + "/KVStore" + str_port.charAt(str_port.length() - 1);
                IStore node = (IStore) Naming.lookup(url);
                node.shutdown();
            }
        } catch (Exception e) {
            handleExceptionDuringShutdown(e);
        }
        this.logger.log("> Nodes closed");
        this.scanner.close();
        this.logger.close();
        System.out.println("> Client closed");
    }

    /**
     * Handles exceptions that occur during the shutdown process.
     *
     * @param e The exception that occurred.
     */
    private void handleExceptionDuringShutdown(Exception e) {
        this.logger.log("> Error during shutdown: " + e.getMessage());
        System.err.println("> Error during shutdown: " + e.getMessage());
    }
}
