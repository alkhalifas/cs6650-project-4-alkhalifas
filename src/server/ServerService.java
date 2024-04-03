package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The interface ServerService contains methods that all types of translation servers communicating via Remote Method Invocation (RMI) should support.
 */
public interface ServerService extends Remote {
  /**
   * Saves a key-value pair in a hashmap.
   *
   * @param key   the word to be translated
   * @param value the translation
   * @return the outcome of the operation
   * @throws RemoteException the RMI failure
   */
  String put(String key, String value) throws  RemoteException;

  /**
   * Retrieves the value of a key.
   *
   * @param key the word to be translated
   * @return the translation
   * @throws RemoteException the RMI failure
   */
  String get(String key) throws RemoteException;

  /**
   * Removes a key-value pair.
   *
   * @param key the word to be deleted
   * @return the outcome of the operation
   * @throws RemoteException the RMI failure
   */
  String delete(String key) throws RemoteException;

  /**
   * Returns the size of the key-value store of this server.
   *
   * @return the size of the key-value store of this server
   * @throws RemoteException the RMI failure
   */
  int getMapSize() throws RemoteException;

  /**
   * Communicates to the coordinator to stop all the servers.
   *
   * @throws RemoteException the RMI failure
   */
  void shutdown() throws RemoteException;
}
