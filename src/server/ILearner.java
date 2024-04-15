package server;

import java.rmi.Remote;

public interface ILearner extends Remote {

  void learn(long sequenceNumber, Object acceptedValue) throws RemoteException;
}
