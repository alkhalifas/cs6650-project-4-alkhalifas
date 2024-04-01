package client;

import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import participant.TranslationService;
import utils.ILogger;
import utils.Logger;

public class Client implements IClient {
    private static final String host1 = "localhost";
    private static final int port1 = 1101;
    private static final int port2 = 1102;
    private static final int port3 = 1103;
    private static final int port4 = 1104;
    private static final int port5 = 1105;
    private static final String loggerName = "ClientLogger";
    private static final String logFileName = "ClientLog.log";
    private static final String service = "TranslationService";
    private final Scanner scanner;
    private final List<String> replicaHosts;
    private final List<Integer> replicaPorts;
    private TranslationService server;
    private final ILogger logger;


    public Client() {
        this.logger = new Logger(loggerName, logFileName);
        this.scanner = new Scanner(System.in);
        this.replicaHosts = new ArrayList<>();
        this.replicaPorts = new ArrayList<>();
        this.replicaHosts.add(host1);
        this.replicaHosts.add(host1);
        this.replicaHosts.add(host1);
        this.replicaHosts.add(host1);
        this.replicaHosts.add(host1);
        this.replicaPorts.add(port1);
        this.replicaPorts.add(port2);
        this.replicaPorts.add(port3);
        this.replicaPorts.add(port4);
        this.replicaPorts.add(port5);
        System.setProperty("sun.rmi.transport.tcp.responseTimeout", "2000");
        System.setProperty("sun.rmi.transport.proxy.connectTimeout", "5000");
        this.connectToRandomReplica();
    }

    private void connectToRandomReplica() {
        try {
            Random random = new Random();
            int randomIndex = random.nextInt(this.replicaHosts.size());
            Registry registry = LocateRegistry.getRegistry(this.replicaHosts.get(randomIndex), this.replicaPorts.get(randomIndex));
            this.server = (TranslationService) registry.lookup(service);
            this.logger.log("Connected to server " + this.replicaHosts.get(randomIndex) + " at port " + this.replicaPorts.get(randomIndex));
        } catch (ConnectException ce) {
            this.logger.log("Connection to server timed out: " + ce.getMessage());
            System.err.println("Connection to server timed out: " + ce.getMessage());
            this.logger.close();
            this.scanner.close();
            System.exit(1);
        } catch (RemoteException re) {
            this.logger.log("Couldn't connect to server: registry not found");
            System.err.println("Couldn't connect to server: registry not found");
            this.logger.close();
            this.scanner.close();
            System.exit(1);
        } catch (NotBoundException nbe) {
            this.logger.log("Couldn't connect to server: TranslationService not bound");
            System.err.println("Couldn't connect to server: TranslationService not bound");
            this.logger.close();
            this.scanner.close();
            System.exit(1);
        }
    }


    @Override
    public void prePopulate() {
        try {
            this.logger.log("Pre-populating...");
            System.out.println("Pre-populating...");
            System.out.println(this.server.put("hello", "ciao"));
            System.out.println(this.server.put("goodbye", "addio"));
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
            Thread.sleep(1000);
        } catch (ConnectException ce) {
            this.logger.log("TranslationService timed out (pre-populate): " + ce.getMessage());
            System.err.println("TranslationService timed out (pre-populate): " + ce.getMessage());
        } catch (RemoteException re) {
            this.logger.log("TranslationService error (pre-populate): " + re.getMessage());
            System.err.println("TranslationService error (pre-populate): " + re.getMessage());
        } catch (InterruptedException ie) {
            this.logger.log("Pre-population error (timeout interrupted): " + ie.getMessage());
            System.err.println("Pre-population error (timeout interrupted): " + ie.getMessage());
        }
    }


    @Override
    public String getRequest() {
        System.out.print("Enter operation (PUT/GET/DELETE:key:value[only with PUT]): ");
        return this.scanner.nextLine();
    }

    private String parseRequest(String request) {
        String result;
        String[] elements = request.split(":");
        if (elements.length < 2 || elements.length > 3) {
            this.logger.log("Received malformed request: " + request);
            return "FAIL: please follow the predefined protocol PUT/GET/DELETE:key:value[with PUT only] and try again";
        } else {
            String operation;
            try {
                operation = elements[0].toUpperCase();
            } catch (Exception e) {
                this.logger.log("Parsing error: invalid operation");
                return "FAIL: could not parse the operation requested. Please follow the predefined protocol PUT/GET/DELETE:key:value[with PUT only] and try again";
            }
            String key;
            try {
                key = elements[1].toLowerCase();
            } catch (Exception e) {
                this.logger.log("Parsing error: invalid key");
                return "FAIL: could not parse the key requested. Please follow the predefined protocol PUT/GET/DELETE:key:value[with PUT only] and try again";
            }
            String value;
            try {
                switch (operation) {
                    case "PUT":
                        try {
                            value = elements[2].toLowerCase();
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
                    default:
                        this.logger.log("Received an invalid request: " + request);
                        return "Invalid request. Please follow the predefined protocol PUT/GET/DELETE:key:value[with PUT only] and try again";
                }
            } catch (ConnectException ce) {
                this.logger.log("TranslationService timed out: " + ce.getMessage());
                result = "TranslationService timed out: " + ce.getMessage();
            } catch (RemoteException re) {
                this.logger.log("TranslationService error: " + re.getMessage());
                result = "TranslationService error: " + re.getMessage();
            }
        }
        this.logger.log("Reply: " + result);
        return result;
    }


    @Override
    public void execute() {
        boolean isRunning = true;
        this.logger.log("Client is running...");
        while (isRunning) {
            String request = this.getRequest();
            if (request.equalsIgnoreCase("shutdown") || request.equalsIgnoreCase("stop")) {
                isRunning = false;
            } else {
                System.out.println(this.parseRequest(request));
            }
        }
        this.shutdown();
    }


    @Override
    public void shutdown() {
        this.logger.log("Received a request to shut down...");
        System.out.println("Client is shutting down...");
        try {
            this.server.shutdown();
            this.logger.log("TranslationService closed");
        } catch (ConnectException ce) {
            this.logger.log("TranslationService timed out (shutdown): " + ce.getMessage());
            System.err.println("TranslationService timed out (shutdown): " + ce.getMessage());
        } catch (RemoteException re) {
            this.logger.log("TranslationService error (shutdown): " + re.getMessage());
            System.err.println("TranslationService error (shutdown): " + re.getMessage());
        }
        this.scanner.close();
        this.logger.log("Client closed");
        this.logger.close();
        System.out.println("Client closed");
    }
}
