package server;

import java.util.HashMap;


//Pair<String, String> keyValue = new ImmutablePair("key", "value");
//Integer key = ImmutablePair.getKey();
//String value = ImmutablePair.getValue();

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
