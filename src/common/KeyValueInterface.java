package common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/*
Key Value Interface to extend the remote class from java, per the recommendation
for project 2
 */
public interface KeyValueInterface extends Remote {


    /**
     * PUT method for KeyValueInterface
     * @param key
     * @param value
     * @throws RemoteException
     */
    void put(String key, String value) throws RemoteException;

    /**
     * GET method for KeyValueInterface
     * @param key
     * @throws RemoteException
     */
    String get(String key) throws RemoteException;

    /**
     * DELETE method for KeyValueInterface
     * @param key
     * @throws RemoteException
     */
    void delete(String key) throws RemoteException;
}
