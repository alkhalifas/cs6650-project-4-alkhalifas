package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The IStore interface has the remote methods for a key-value store over RMI for this assignment
 */
public interface IStore extends Remote {

  /**
   * Stores or updates a value in the key-value stoer
   *
   * @param key The key under which the value should be stored
   * @param value The value to be associated with the key.
   * @return A string indicating the result of the operation, like the output
   * @throws RemoteException If an RMI error occurs
   */
  String put(String key, String value) throws RemoteException;

  /**
   * Removes a value from the key-value store.
   *
   * @param key The key whose associated value is to be removed
   * @return A string indicating the result of the operation
   * @throws RemoteException If an RMI error occurs
   */
  String delete(String key) throws RemoteException;

  /**
   * Retrieves a value from the key-value store
   *
   * @param key The key whose associated value is to be retrieved.
   * @return A string containing the value associated with the key
   * @throws RemoteException If an RMI error occurs
   */
  String get(String key) throws RemoteException;

  /**
   * Shuts down the key-value store service
   * This method is intended for clean-up operations and to stop the server gracefully
   * @throws RemoteException If an RMI error occurs during the remote method call
   */
  void shutdown() throws RemoteException;
}
