package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IProposer extends Remote {
  boolean executePaxos(long sequenceNumber, Object proposalValue) throws RemoteException;
}
