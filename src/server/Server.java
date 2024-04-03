package server;

import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import coordinator.ICoordinator;
import utils.ILogger;
import utils.Logger;

/**
 * Represents a server in the two-phase commit protocol that communicates via Remote Method Invocation (RMI).
 */
public class Server extends UnicastRemoteObject implements IServer, ServerService {
  // Constants
  private static final String coordinatorService = "Coordinator";
  private static final int randomNumBound = 100;
  private static final int abortNumber = 23;
  // Attributes
  private final int port;
  private final Registry registry;
  private boolean canCommit;
  private final ICoordinator coordinator;
  private String state = "INITIAL";
  private final Map<String, String> dictionary;
  private final ILogger logger;

  /**
   * Instantiates a new server.
   *
   * @param coordinatorHost the hostname of the coordinator
   * @param coordinatorPort the port number of the coordinator
   * @param serverPort the port number of this server
   * @throws RemoteException if an RMI-related error occurs
   */
  public Server(String coordinatorHost, int coordinatorPort, int serverPort) throws RemoteException {
    // Initialize attributes
    this.port = serverPort;
    String loggerName = "Server" + this.port + "Logger";
    String logFileName = "Server" + this.port + "Log.log";
    Map<String, String> store = new HashMap<>(); // instantiate the key-value store
    this.dictionary = Collections.synchronizedMap(store); // make the key-value store thread-safe
    this.logger = new Logger(loggerName, logFileName);

    // Set timeout mechanism
    System.setProperty("sun.rmi.transport.tcp.responseTimeout", "2000");
    System.setProperty("sun.rmi.transport.proxy.connectTimeout", "5000");

    try {
      // Connect to the coordinator
      this.registry = LocateRegistry.getRegistry(coordinatorHost, coordinatorPort);
      this.logger.log("> Established connection to the remote object Registry on the host " + coordinatorHost + " and port " + coordinatorPort + " created");
      this.coordinator = (ICoordinator) registry.lookup(coordinatorService);
      this.logger.log("> Established Connection bound to Coordinator in the remote object Registry");
    } catch (ConnectException ce) { // connection times out
      this.logger.log("> Established Connection to coordinator " + coordinatorHost + " at port " + coordinatorPort + " timed out: " + ce.getMessage());
      throw new RemoteException("Established Connection to coordinator " + coordinatorHost + " at port " + coordinatorPort + " timed out", ce);
    } catch (Exception e) { // general error
      this.logger.log("> Unable to establish connection to coordinator " + coordinatorHost + " at port " + coordinatorPort + ": " + e.getMessage());
      throw new RemoteException("Unable to establish connection to coordinator " + coordinatorHost + " at port " + coordinatorPort, e);
    }
  }

//   Simulates an abort scenario by generating a random number between 0 and 100 and comparing it against the "abort number".
//  private void simulateAbort() {
//    Random random = new Random();
//    int randomNumber = random.nextInt(randomNumBound);
//    this.logger.log("The random number is: " + randomNumber);
//    this.canCommit = randomNumber != abortNumber;
//  }

  /**
   * Performs the first phase of the two-phase commit protocol.
   *
   * @param operation the type of operation (PUT/DELETE)
   * @param key the key associated with the operation
   * @return true if the server votes to commit, false if it votes to abort
   * @throws RemoteException if an RMI-related error occurs during the operation
   */
  @Override
  public boolean isReady(String operation, String key) throws RemoteException {
    if (operation.equals("PUT")) {
      if (this.dictionary.containsKey(key)) { // if the key is already in the store
        this.canCommit = false; // abort
        this.logger.log("> Error: the put operation for \"" + key + "\" already exists");
      } else {
        this.canCommit = true;
//        this.simulateAbort(); // 1/101 chances to abort
      }
    } else if (operation.equals("DELETE")) {
      if (this.dictionary.containsKey(key)) {
        this.canCommit = true;
//        this.simulateAbort(); // 1/101 chances to abort
      } else { // if the key doesn't exist
        this.canCommit = false; // abort
        this.logger.log("> Error: " + "\"" + key + "\" does not exist");
      }
    }
    if (this.canCommit) {
      this.logger.log("ready");
      this.state = "READY";
    } else {
      this.logger.log("abort");
      this.state = "ABORT";
    }
    return this.canCommit;
  }

  /**
   * Performs the second phase of the two-phase commit protocol for inserting a key-value pair.
   *
   * @param key the key associated with the operation
   * @param value the value associated with the key
   * @throws RemoteException if an RMI-related error occurs during the operation
   */
  @Override
  public void commit(String key, String value) throws RemoteException { // overloaded method that performs the put operation
    this.dictionary.put(key, value);
    this.logger.log("> Added the key \"" + key + "\" associated with \"" + value + "\"");
    this.state = "INITIAL";
  }

  /**
   * Performs the second phase of the two-phase commit protocol for deleting a key-value pair.
   *
   * @param key the key associated with the operation
   * @throws RemoteException if an RMI-related error occurs during the operation
   */
  @Override
  public void commit(String key) throws RemoteException { // overloaded method that performs the delete operation
    this.dictionary.remove(key);
    this.logger.log("> Deleted the key-value pair associated with \"" + key + "\"");
    this.state = "INITIAL";
  }

  /**
   * Saves a key-value pair in a hashmap.
   *
   * @param key the word to be translated
   * @param value the translation
   * @return the outcome of the operation
   * @throws RemoteException if an RMI-related error occurs during the operation
   */
  @Override
  public String put(String key, String value) throws RemoteException {
    try {
      if (!this.coordinator.prepareTransaction("PUT", key, value)) {
        return "> Error: the operation for \"" + key + "\" already exists or the transaction aborted";
      } else {
        return "> Successfully completed the PUT operation.";
      }
    } catch (ConnectException ce) {
      this.logger.log("> Error: Communication with the coordinator timed out during the two-phase protocol (put): " + ce.getMessage());
      return "> Error: the connection timed out. Please try again";
    } catch (RemoteException re) {
      this.logger.log("> Error: RMI failure occurred during the two-phase protocol (put): " + re.getMessage());
      return "> Error: RMI failure. Please try again";
    }
  }

  /**
   * Retrieves the value of a key.
   *
   * @param key the word to be translated
   * @return the translation
   * @

  throws RemoteException if an RMI-related error occurs during the operation
   */
  @Override
  public String get(String key) throws RemoteException {
    String translation;
    if (this.dictionary.containsKey(key)) {
      translation = this.dictionary.get(key);
      this.logger.log("> Returned the value \"" + translation + "\" associated with \"" + key + "\"");
    } else { // if the key doesn't exist
      translation = "> Error: Unknown operation for " + "\"" + key + "\"" + " yet";
      this.logger.log("> Error: no value is associated with \"" + key + "\"");
    }
    return translation;
  }

  /**
   * Removes a key-value pair.
   *
   * @param key the word to be deleted
   * @return the outcome of the operation
   * @throws RemoteException if an RMI-related error occurs during the operation
   */
  @Override
  public String delete(String key) throws RemoteException {
    try {
      if (!this.coordinator.prepareTransaction("DELETE", key)) {
        return "> Error: " + "\"" + key + "\" does not exist or the transaction aborted";
      } else {
        return "> Successfully completed the DELETE operation.";
      }
    } catch (ConnectException ce) {
      this.logger.log("> Communication with the coordinator timed out during the two-phase protocol (delete): " + ce.getMessage());
      return "> Error: the connection timed out. Please try again";
    } catch (RemoteException re) {
      this.logger.log("> RMI failure occurred during the two-phase protocol (delete): " + re.getMessage());
      return "> Error: RMI failure. Please try again";
    }
  }

  /**
   * Retrieves the current state of this server.
   *
   * @return the state of this server
   * @throws RemoteException if an RMI-related error occurs during the operation
   */
  @Override
  public String getState() throws RemoteException {
    return this.state;
  }

  /**
   * Retrieves the port number where this server is running.
   *
   * @return the port number of this server
   * @throws RemoteException if an RMI-related error occurs during the operation
   */
  @Override
  public int getPort() throws RemoteException {
    return this.port;
  }

  /**
   * Returns the size of the key-value store of this server.
   *
   * @return the size of the key-value store of this server
   * @throws RemoteException if an RMI-related error occurs during the operation
   */
  @Override
  public int getMapSize() throws RemoteException {
    return this.dictionary.size();
  }

  /**
   * Communicates to the coordinator to stop all the servers.
   *
   * @throws RemoteException if an RMI-related error occurs during the operation
   */
  @Override
  public void shutdown() throws RemoteException {
    try {
      this.coordinator.shutdown(this.registry);
    } catch (ConnectException ce) {
      this.logger.log("> Communication with the coordinator timed out during the shutdown process: " + ce.getMessage());
    } catch (RemoteException e) {
      this.logger.log("> Error: RMI failure during the shutdown process: " + e.getMessage());
    }
  }

  /**
   * Stops this server.
   *
   * @param registry this server's registry
   * @throws RemoteException if an RMI-related error occurs during the operation
   */
  @Override
  public void shutdown(Registry registry) throws RemoteException {
    this.logger.log("> Received a request to shut down...");
    System.out.println(this + " is shutting down...");
    try {
      registry.unbind("ServerService"); // unbind the remote object from the custom name
      this.logger.log(this + " unbound in registry");
    } catch (NotBoundException e) {
      this.logger.log("> Unbind error: " + e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
    UnicastRemoteObject.unexportObject(this, true); // unexport the remote object
    this.logger.log(this + " unexported");
    this.logger.log(this + " closed");
    this.logger.close();
    System.out.println(this + " closed");
  }

  @Override
  public String toString() {
    return "> Server at port " + this.port;
  }
}
