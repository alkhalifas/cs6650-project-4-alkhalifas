package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The IAcceptor interface defines the remote methods that must be implemented by an acceptor
 */
public interface IAcceptor extends Remote {
  /**
   * Prepares the acceptor for a proposed change and decides whether to accept or reject
   *
   * @param sequenceNumber The sequence number of the proposal, used to ensure proposals are processed in order.
   * @return The currently promised sequence number or the proposal value, depending on the algorithm implementation.
   * @throws RemoteException If an RMI error occurs during the remote method call.
   */
  Object prepare(long sequenceNumber) throws RemoteException;

  /**
   * Accepts the proposal if the given sequence number matches the acceptor's current state or promise.
   *
   * @param sequenceNumber The sequence number of the proposal which must match the promised number.
   * @param proposalValue The value proposed by the proposer, to be accepted if conditions are met.
   * @return The sequence number associated with the accepted value, confirming the acceptance.
   * @throws RemoteException If an RMI error occurs during the remote method call.
   */
  long accept(long sequenceNumber, Object proposalValue) throws RemoteException;
}
