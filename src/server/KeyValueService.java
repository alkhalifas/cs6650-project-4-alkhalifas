package server;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;

/*
 Key Val Service to extend UnicastRemoteObject that uses hashmap with logging
 */
public class KeyValueService extends UnicastRemoteObject implements KeyValueInterface {
    private ConcurrentHashMap<String, String> dataStore;

    public KeyValueService() throws RemoteException {
        super();
        dataStore = new ConcurrentHashMap<>();
    }

    @Override
    public void put(String key, String value) {
        dataStore.put(key, value);
        ServerLogger.log("PUT operation - Key: " + key + ", Value: " + value);
    }

    @Override
    public String get(String key) {
        String value = dataStore.get(key);
        ServerLogger.log("GET operation - Key: " + key + ", Value: " + value);
        return value;
    }

    @Override
    public void delete(String key) {
        dataStore.remove(key);
        ServerLogger.log("DELETE operation - Key: " + key);
    }
}