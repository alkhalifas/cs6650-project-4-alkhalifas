package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IAcceptor extends Remote {
  Object prepare(long sequenceNumber) throws RemoteException;
  long accept(long sequenceNumber, Object proposalValue) throws RemoteException;
}
