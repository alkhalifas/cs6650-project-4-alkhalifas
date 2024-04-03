package server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

/**
 * The interface for servers in the two-phase commit protocol for translation servers using Remote Method Invocation (RMI).
 */
public interface IServer extends Remote {

  /**
   * Performs the first phase of the two-phase commit protocol.
   *
   * @param operation the type of operation (PUT/DELETE)
   * @param key       the key associated with the operation
   * @return true if the server votes to commit, false if it votes to abort
   * @throws RemoteException if an RMI-related error occurs during the operation
   */
  boolean isReady(String operation, String key) throws RemoteException;

  /**
   * Performs the second phase of the two-phase commit protocol for inserting a key-value pair.
   *
   * @param key   the key associated with the operation
   * @param value the value associated with the key
   * @throws RemoteException if an RMI-related error occurs during the operation
   */
  void commit(String key, String value) throws RemoteException;

  /**
   * Performs the second phase of the two-phase commit protocol for deleting a key-value pair.
   *
   * @param key the key associated with the operation
   * @throws RemoteException if an RMI-related error occurs during the operation
   */
  void commit(String key) throws RemoteException;

  /**
   * Retrieves the current state of this server.
   *
   * @return the state of this server
   * @throws RemoteException if an RMI-related error occurs during the operation
   */
  String getState() throws RemoteException;

  /**
   * Retrieves the port number where this server is running.
   *
   * @return the port number of this server
   * @throws RemoteException if an RMI-related error occurs during the operation
   */
  int getPort() throws RemoteException;

  /**
   * Shuts down this server.
   *
   * @param registry the RMI registry associated with this server
   * @throws RemoteException if an RMI-related error occurs during the operation
   */
  void shutdown(Registry registry) throws RemoteException;
}
