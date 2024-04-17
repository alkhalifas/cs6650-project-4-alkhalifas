package server;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;



public class Server extends UnicastRemoteObject implements IProposer, IAcceptor, ILearner, IStore {

    private List<IAcceptor> acceptors;
    private List<ILearner> learners;


    public Server(long nodeId, int port, int numNodes) throws RemoteException {

    }

    public void setAcceptors(List<IAcceptor> acceptors) {
        this.acceptors = acceptors;
    }

    }

    public void setLearners(List<ILearner> learners) {
        this.learners = learners;
    }


    private boolean propose(Operation operation) throws RemoteException {

    }

    public void setRegistry(Registry registry) {

    }

    @Override
    public Object prepare(long sequenceNumber) throws RemoteException {
        return null;
    }

    @Override
    public long accept(long sequenceNumber, Object proposalValue) throws RemoteException {
        return 0;
    }

    @Override
    public void learn(long sequenceNumber, Object acceptedValue) throws RemoteException {

    }

    @Override
    public boolean executePaxos(long sequenceNumber, Object proposalValue) throws RemoteException {
        return false;
    }

    @Override
    public String put(String key, String value) throws RemoteException {
        return null;
    }

    @Override
    public String delete(String key) throws RemoteException {
        return null;
    }

    @Override
    public String get(String key) throws RemoteException {
        return null;
    }

    @Override
    public void shutdown() throws RemoteException {

    }

    private static class Operation {

    }

    private static class Promise {

    }
}
