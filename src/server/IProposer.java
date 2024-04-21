package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The IProposer interface defines the remote method for initiating and executing the Paxos consensus algorithm.
 */
public interface IProposer extends Remote {

  /**
   * Initiates the Paxos protocol to propose a value and attempt to reach consensus among acceptors.
   */
  boolean runExecutePaxosAlgorithm(long sequenceNumber, Object proposalValue) throws RemoteException;
}
