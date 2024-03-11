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
    public void put(String key, String value) {
        dataStore.put(key, value);
        ServerLogger.log("PUT operation - Key: " + key + ", Value: " + value);
    }

    /**
     * GET method for the data store
     * @param key
     */
    @Override
    public String get(String key) {
        String value = dataStore.get(key);
        ServerLogger.log("GET operation - Key: " + key + ", Value: " + value);
        return value;
    }

    /**
     * DELETE method for the data store
     * @param key
     */
    @Override
    public void delete(String key) {
        dataStore.remove(key);
        ServerLogger.log("DELETE operation - Key: " + key);
    }
}