package node;

import java.rmi.Remote;

public interface IAcceptor extends Remote {
  Object prepare(long sequenceNumber) throws RemoteException;
  long accept(long sequenceNumber, Object proposalValue) throws RemoteException;
}
