package server;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;

/*
 Key Val Service to extend UnicastRemoteObject that uses hashmap with logging
 */
public class KeyValueService extends UnicastRemoteObject implements KeyValueInterface {
    private ConcurrentHashMap<String, String> dataStore;

    /**
     * Public of KeyValueService where datastore is instantiated
     * @throws RemoteException
     */
    public KeyValueService() throws RemoteException {
        super();
        dataStore = new ConcurrentHashMap<>();
    }

    /**
     * PUT method for the data store
     * @param key
     * @param value
     */
    @Override
    public void put(String key, String value) throws RemoteException {
        if (key == null || key.isEmpty() || value == null) {
            String errorMessage = "Malformed PUT request: Key and value must be non-null, key cannot be empty. Key: '" + key + "', Value: '" + value + "'.";
            ServerLogger.log(errorMessage);
            throw new RemoteException(errorMessage);
        }
        dataStore.put(key, value);
        ServerLogger.log("PUT operation - Key: " + key + ", Value: " + value);
    }

    /**
     * GET method for the data store
     * @param key
     */
    @Override
    public String get(String key) throws RemoteException {
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
    public void delete(String key) throws RemoteException {
        if (key == null || key.isEmpty()) {
            String errorMessage = "Malformed DELETE request: Key must be non-null and cannot be empty. Key: '" + key + "'.";
            ServerLogger.log(errorMessage);
            throw new RemoteException(errorMessage);
        }
        dataStore.remove(key);
        ServerLogger.log("DELETE operation - Key: " + key);
    }
}