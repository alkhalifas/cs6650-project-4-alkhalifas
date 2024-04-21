//package coordinator;
//
//import java.rmi.Remote;
//import java.rmi.RemoteException;
//import java.rmi.registry.Registry;
//import java.util.List;
//
///**
// * The interface for the Coordinator, defining methods to manage transactions and server connections.
// */
//public interface ICoordinator extends Remote {
//
//    /**
//     * Connects to the servers specified by their host addresses and ports.
//     *
//     * @param serverHosts the list of server host addresses
//     * @param serverPorts the list of server ports
//     * @throws RemoteException if an RMI-related error occurs during the connection process
//     */
//    void connectToServers(List<String> serverHosts, List<Integer> serverPorts) throws RemoteException;
//
//    /**
//     * Prepares and coordinates a transaction for inserting a key-value pair.
//     *
//     * @param operation the type of operation (PUT)
//     * @param key the key associated with the operation
//     * @param value the value associated with the key
//     * @return true if the transaction was successfully committed, false otherwise
//     * @throws RemoteException if an RMI-related error occurs during transaction processing
//     */
//    boolean prepareTransaction(String operation, String key, String value) throws RemoteException;
//
//    /**
//     * Prepares and coordinates a transaction for deleting a key-value pair.
//     *
//     * @param operation the type of operation (DELETE)
//     * @param key the key associated with the operation
//     * @return true if the transaction was successfully committed, false otherwise
//     * @throws RemoteException if an RMI-related error occurs during transaction processing
//     */
//    boolean prepareTransaction(String operation, String key) throws RemoteException;
//
//    /**
//     * Initiates the shutdown process for the coordinator and connected servers.
//     *
//     * @param registry the RMI registry where the coordinator is bound
//     * @throws RemoteException if an RMI-related error occurs during shutdown
//     */
//    void shutdown(Registry registry) throws RemoteException;
//}
