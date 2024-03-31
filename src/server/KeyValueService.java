package server;

import common.KeyValueInterface;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.HashSet;
import java.util.Set;

/*
 Key Val Service to extend UnicastRemoteObject that uses hashmap with logging
 */
public class KeyValueService extends UnicastRemoteObject implements KeyValueInterface {
    private ConcurrentHashMap<String, String> dataStore;
    private Set<String> preparedKeys;

    /**
     * Public of KeyValueService where datastore is instantiated
     * @throws RemoteException
     */
    public KeyValueService() throws RemoteException {
        super();
        dataStore = new ConcurrentHashMap<>();
        preparedKeys = new HashSet<>();
    }

    /**
     * PUT method for the data store
     * @param key
     * @param value
     */
    @Override
    public synchronized void put(String key, String value) throws RemoteException {
        if (key == null || key.isEmpty() || value == null) {
            String errorMessage = "Malformed PUT request: Key and value must be non-null and key cannot be empty.";
            throw new RemoteException(errorMessage);
        }

        boolean prepareSuccess = preparePhase(key, value);

        if (prepareSuccess) {
            commitPhase(key, value);
            ServerLogger.log("PUT operation - Key: " + key + ", Value: " + value);
        } else {
            ServerLogger.log("PUT operation aborted - Key: " + key + ", Value: " + value);
        }
    }

    /**
     * GET method for the data store
     * @param key
     */
    @Override
    public synchronized String get(String key) throws RemoteException {
        if (key == null || key.isEmpty()) {
            String errorMessage = "Malformed GET request: Key must be non-null and cannot be empty. Key: '" + key + "'.";
            ServerLogger.log(errorMessage);
            throw new RemoteException(errorMessage);
        }
        String value = dataStore.get(key);
        ServerLogger.log("GET operation - Key: " + key + ", Value: " + value);
        return value;
    }

    /**
     * DELETE method for the data store
     * @param key
     */
    @Override
    public synchronized void delete(String key) throws RemoteException {
        if (key == null || key.isEmpty()) {
            String errorMessage = "Malformed DELETE request: Key must be non-null and cannot be empty.";
            throw new RemoteException(errorMessage);
        }

        boolean prepareSuccess = preparePhase(key, null);

        if (prepareSuccess) {
            commitPhase(key, null);
            ServerLogger.log("DELETE operation - Key: " + key);
        } else {
            ServerLogger.log("DELETE operation aborted - Key: " + key + " - Key not found in preparedKeys set");
        }
    }

    private synchronized boolean preparePhase(String key, String value) {
        if (preparedKeys.contains(key)) {
            return true;
        } else {
            ServerLogger.log("Key '" + key + "' not found in preparedKeys set");
            return false;
        }
    }

    private synchronized void commitPhase(String key, String value) {
        if (preparedKeys.contains(key)) {
            dataStore.put(key, value);
            ServerLogger.log("COMMIT phase successful for key: " + key);
        } else {
            ServerLogger.log("COMMIT phase aborted for key: " + key + " - Key not found in preparedKeys set");
        }
        preparedKeys.remove(key);
    }


}