package common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface for the Key-Value Service.
 */
public interface KeyValueInterface extends Remote {

    /**
     * Method to put a key-value pair into the key-value store.
     * @param key The key.
     * @param value The value.
     * @throws RemoteException If a remote error occurs.
     */
    void put(String key, String value) throws RemoteException;

    /**
     * Method to retrieve the value associated with the given key from the key-value store.
     * @param key The key.
     * @return The value associated with the key, or null if the key does not exist.
     * @throws RemoteException If a remote error occurs.
     */
    String get(String key) throws RemoteException;

    /**
     * Method to delete the key-value pair associated with the given key from the key-value store.
     * @param key The key.
     * @throws RemoteException If a remote error occurs.
     */
    void delete(String key) throws RemoteException;
}
