package server;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;

/*
 Key Val Service to extend UnicastRemoteObject that uses hashmap
 */
public class KeyValueService extends UnicastRemoteObject implements KeyValueInterface {
    private ConcurrentHashMap<String, String> dataStore;

    public KeyValueService() throws RemoteException {
        super();

        // double check in docs for best practice around the use of DS here
        dataStore = new ConcurrentHashMap<>();
    }

    @Override
    public void put(String key, String value) {
        dataStore.put(key, value);
    }

    @Override
    public String get(String key) {
        return dataStore.get(key);
    }

    @Override
    public void delete(String key) {
        dataStore.remove(key);
    }
}
