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
   */
  void learn(long sequenceNumber, Object acceptedValue) throws RemoteException;
}
