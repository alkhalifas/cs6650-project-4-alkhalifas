package server;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface IStore extends Remote {

  String put(String key, String value) throws RemoteException;

  String delete(String key) throws RemoteException;

  String get(String key) throws RemoteException;

  void shutdown() throws RemoteException;
}
