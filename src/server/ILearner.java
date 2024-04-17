package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ILearner extends Remote {

  void learn(long sequenceNumber, Object acceptedValue) throws RemoteException;
}
