package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The IProposer interface defines the remote method for initiating and executing the Paxos consensus algorithm.
 */
public interface IProposer extends Remote {

  /**
   * Initiates the Paxos protocol to propose a value and attempt to reach consensus among acceptors.
   *
   * @param sequenceNumber The sequence number of the proposal, ensuring proposals are processed in order.
   * @param proposalValue The value being proposed for consensus.
   * @return true if the proposal was accepted by a majority of acceptors, false otherwise.
   * @throws RemoteException If an RMI error occurs during the remote method call.
   */
  boolean executePaxos(long sequenceNumber, Object proposalValue) throws RemoteException;
}
