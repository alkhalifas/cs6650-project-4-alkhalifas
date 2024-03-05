package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/*
Key Value Interface to extend the remote class from java, per the recommendation
for project 2
 */
public interface KeyValueInterface extends Remote {
    void put(String key, String value) throws RemoteException;
    String get(String key) throws RemoteException;
    void delete(String key) throws RemoteException;
}
