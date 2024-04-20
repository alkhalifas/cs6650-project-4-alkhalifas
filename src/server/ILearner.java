package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The ILearner interface defines the remote method that must be implemented by a learner
 * in a consensus algorithm.
 */
public interface ILearner extends Remote {

  /**
   * Receives notification of an accepted value from acceptors, completing the consensus process.
   *
   * @param sequenceNumber The sequence number of the accepted proposal, ensuring the correct ordering of learned values.
   * @param acceptedValue The value that has been accepted, which is now to be learned and acted upon.
   * @throws RemoteException If an RMI error occurs during the remote method call.
   */
  void learn(long sequenceNumber, Object acceptedValue) throws RemoteException;
}
