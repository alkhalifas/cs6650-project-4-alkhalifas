package common;

import common.KeyValueInterface;
import server.ServerLogger;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class KeyValueService extends UnicastRemoteObject implements KeyValueInterface {
    private ConcurrentHashMap<String, String> dataStore;
    private Set<String> preparedKeys;

    public KeyValueService() throws RemoteException {
        super();
        dataStore = new ConcurrentHashMap<>();
        preparedKeys = new HashSet<>();
    }

    @Override
    public void put(String key, String value) throws RemoteException {
        if (key == null || key.isEmpty() || value == null) {
            ServerLogger.log("Malformed PUT request: Key and value must be non-null and key cannot be empty.");
            return;
        }

        synchronized (this) {
            boolean prepareSuccess = preparedKeys.contains(key);
            if (prepareSuccess) {
                dataStore.put(key, value);
                preparedKeys.remove(key);
                ServerLogger.log("PUT operation - Key: " + key + ", Value: " + value);
            } else {
                ServerLogger.log("PUT operation aborted - Key: " + key + ", Value: " + value);
            }
        }
    }

    @Override
    public String get(String key) throws RemoteException {
        if (key == null || key.isEmpty()) {
            ServerLogger.log("Malformed GET request: Key must be non-null and cannot be empty. Key: '" + key + "'.");
            return null;
        }
        String value = dataStore.get(key);
        ServerLogger.log("GET operation - Key: " + key + ", Value: " + value);
        return value;
    }

    @Override
    public void delete(String key) throws RemoteException {
        if (key == null || key.isEmpty()) {
            ServerLogger.log("Malformed DELETE request: Key must be non-null and cannot be empty.");
            return;
        }

        synchronized (this) {
            boolean prepareSuccess = preparedKeys.contains(key);
            if (prepareSuccess) {
                dataStore.remove(key);
                preparedKeys.remove(key);
                ServerLogger.log("DELETE operation - Key: " + key);
            } else {
                ServerLogger.log("DELETE operation aborted - Key: " + key + " - Key not found in preparedKeys set");
            }
        }
    }

    private void preparePhase(String key) {
        preparedKeys.add(key);
    }
}
