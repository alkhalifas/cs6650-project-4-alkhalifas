package coordinator;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.List;

public interface ICoordinator extends Remote {
    void connectToParticipants(List<String> participantHosts, List<Integer> participantPorts) throws RemoteException;
    boolean prepareTransaction(String operation, String key, String value) throws RemoteException;
    boolean prepareTransaction(String operation, String key) throws RemoteException;
    void shutdown(Registry registry) throws RemoteException;
}
