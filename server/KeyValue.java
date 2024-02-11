package server;

import java.util.HashMap;

/**
 * Key Value hashmap that acts as a data store for TCP and UDP communications
 */
public class KeyValue {
    private HashMap<String, String> dataStore;

    /**
     * Instantiate a keyvalue hashmap datastore
     */
    public KeyValue() {
        dataStore = new HashMap<>();
    }

    /**
     * PUT method
     * @param key
     * @param value
     */
    public synchronized void put(String key, String value) {
        dataStore.put(key, value);
    }

    /**
     * GET Method
     * @param key
     * @return
     */
    public synchronized String get(String key) {
        return dataStore.get(key);
    }

    /**
     * DELETE Method
     * @param key
     */
    public synchronized void delete(String key) {
        dataStore.remove(key);
    }
}
