package server;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import utils.ILogger;
import utils.Logger;


/**
 * Backend server that implements the PAXOS algorithm in conjunction with the KV Store
 */
public class Server extends UnicastRemoteObject implements IProposer, IAcceptor, ILearner, IStore {

    // Set up random failure mode variables:
    private static final double RANDOM_FAILURE_PROBABILITY = 0.10;
    private final Random randomGenerator = new Random();

    // Vars:
    private static final AtomicLong sequenceNumberGenerator = new AtomicLong(0);
    private Registry registry;
    private final ConcurrentHashMap<String, String> kvStore;
    private final long serverId;
    private final int numServers;
    private long maxObservedSequenceNumber;
    private long lastAcceptedSequenceNumber;
    private Object lastAcceptedValue;

    // Set logger, accepter and learner
    private final ILogger logger;
    private List<IAcceptor> acceptors;
    private List<ILearner> learners;


    /**
     * Executes the Paxos consensus algorithm for a given proposal.
     *
     * @param sequenceNumber The unique sequence number for the current proposal.
     * @param proposalValue The value being proposed, which might be accepted by the majority of acceptors.
     * @return true if the proposal was accepted by a majority; false otherwise.
     * The algorithm will proceed in three parts:
     * 1. Propose/Prepare: Collect promises from a majority of acceptors
     * 2. Accept: If a majority promised, send accept requests with the proposed or a previously accepted value
     * 3. Learning: If a majority accepts, notify all learners of the accepted value
     */
    @Override
    public synchronized boolean runExecutePaxosAlgorithm(long sequenceNumber, Object proposalValue) throws RemoteException {
        int majorityThreshold = this.numServers / 2;  // Calculate the majority threshold
        int promisesCount = 0;  // Count of promises received

        // To store previously accepted values
        Map<Long, Object> alreadyAcceptedValues = new HashMap<>(this.numServers);

        // Gather promises from all acceptors
        for (IAcceptor acceptor : this.acceptors) {
            try {
                Promise promise = (Promise) acceptor.prepare(sequenceNumber);
                if (promise.value != null) {
                    // Store accepted value
                    alreadyAcceptedValues.put(promise.sequenceNumber, promise.value);
                }
                promisesCount += promise.vote;  // Sum up the promises
            } catch (RemoteException e) {
                this.logger.log("> " +acceptor + " failed during the propose phase");
            }
        }

        // Choose the proposal value based on acceptor responses
        if (!alreadyAcceptedValues.isEmpty()) {
            this.lastAcceptedValue = alreadyAcceptedValues.get(Collections.max(alreadyAcceptedValues.keySet()));
        } else {
            this.lastAcceptedValue = proposalValue;
        }

        // Check if a majority of acceptors promised
        if (promisesCount > majorityThreshold) {
            // count of acceptances set to zero
            int acceptsCount = 0;
            // send accept requests
            for (IAcceptor acceptor : this.acceptors) {
                try {
                    // Count each acceptance
                    if (acceptor.accept(sequenceNumber, this.lastAcceptedValue) <= sequenceNumber) {
                        acceptsCount += 1;
                    }
                } catch (RemoteException e) {
                    this.logger.log("> " + acceptor + " failed during the accept phase");
                }
            }

            // Check if a majority accepted the value
            if (acceptsCount > majorityThreshold) {
                // Notify all learners
                for (ILearner learner : this.learners) {
                    try {
                        // broadcast and share the accepted value
                        learner.learn(sequenceNumber, this.lastAcceptedValue);
                    } catch (RemoteException e) {
                        this.logger.log("> " +learner + " failed during the learn phase");
                    }
                }
                this.logger.log("> The accepted value " + this.lastAcceptedValue.toString() + " has been saved");
                this.runModificationOperation((Operation) this.lastAcceptedValue);
                return true;
            } else {
                this.logger.log("> The accepted value " + this.lastAcceptedValue.toString() + " has been rejected");
                return false;
            }
        } else {
            this.logger.log("> Proposal aborted: couldn't extract a majority of promises");
            return false;
        }
    }


    /**
     * Generates a globally unique sequence number for each operation proposed by this server.
     */
    private long generateSequenceNumber() {
        long currTime = System.currentTimeMillis();
        long sequenceNumber = (currTime << 16) | (this.serverId & 0xFFFF);
        return sequenceNumber + sequenceNumberGenerator.getAndIncrement();
    }

    /**
     * Constructs a new server with the specified parameters
     */
    public Server(long serverId, int port, int numServers) throws RemoteException {
        this.kvStore = new ConcurrentHashMap<>();
        this.serverId = serverId;
        this.numServers = numServers;
        this.maxObservedSequenceNumber = -Long.MAX_VALUE;
        this.lastAcceptedSequenceNumber = -Long.MAX_VALUE;
        String loggerName = "Server" + serverId + "Logger";
        String logFileName = "Server" + serverId + "Log.log";
        this.logger = new Logger(loggerName, logFileName);
        this.logger.log(this + " is online and ready at port " + port);
    }

    /**
     * Set the list of acceptors
     *
     * @param acceptors A list of Server that act as acceptors in the Paxos protocol.
     */
    public void setServerAcceptors(List<IAcceptor> acceptors) {
        this.acceptors = acceptors;
    }

    /**
     * Sets the list of learners
     *
     * @param learners A list of servers that act as learners in the Paxos protocol.
     */
    public void setServerLearners(List<ILearner> learners) {
        this.learners = learners;
    }

    /**
     * Attempts to put a key-value pair in the distributed key-value store.
     * This method proposes a new entry via the Paxos protocol to ensure consistency
     */
    @Override
    public synchronized String put(String key, String value) throws RemoteException {
        if (this.kvStore.containsKey(key)) {
            this.logger.log("> Error: the entry for \"" + key + "\" already exists");
            return "> Error: the entry for \"" + key + "\" already exists";
        } else {
            if (this.propose(new Operation("PUT", key, value))) {
                return "> SUCCESS";
            } else {
                return "> Error: execution of Paxos failed - please try again.";
            }
        }
    }

    /**
     * Retrieves the value associated with a given key from the distributed key-value store.
     * If the key does not exist, it logs an error and returns null.
     */
    @Override
    public synchronized String get(String key) throws RemoteException {
        if (this.kvStore.containsKey(key)) {
            this.logger.log("> Returned the value \"" + key + "\" associated with \"" + key + "\"");
        } else {
            this.logger.log("> Error: no value is associated with \"" + key + "\"");
        }
        return this.kvStore.get(key);
    }

    /**
     * Deletes a key-value pair from the distributed key-value store.
     */
    @Override
    public synchronized String delete(String key) throws RemoteException {
        if (this.kvStore.containsKey(key)) {
            if (this.propose(new Operation("DELETE", key))) {
                this.logger.log("> Value proposed promised to be accepted by the majority of servers");
                return "SUCCESS";
            } else {
                return "> Error: Execution of Paxos failed - please try again.";
            }
        } else {
            this.logger.log("> Error: " + "\"" + key + "\" does not exist");
            return "> Error: " + "\"" + key + "\" does not exist";
        }
    }


    /**
     * Applies a specified operation to the key-value store. The operation can either
     * be a PUT or DELETE or GET
     */
    private void runModificationOperation(Operation operation) {
        if (operation == null) return;

        switch (operation.type) {
            // todo: conditionally add GET to align with the others
            case "PUT":
                // Check if the key already exists
                if (this.kvStore.containsKey(operation.key)) {
                    // Log failure
                    this.logger.log("> Error: the execution for \"" + operation.key + "\" already exists");
                } else {
                    // If the key does not exist, add the key-value pair
                    this.kvStore.put(operation.key, operation.value);
                    // Log success message.
                    this.logger.log("> Added the key \"" + operation.key + "\" associated with \"" + operation.value + "\"");
                }
                break;
            case "DELETE":
                // Check if the key exists in the key-value store.
                if (this.kvStore.containsKey(operation.key)) {
                    // Remove the key-value pair from the store if it exists.
                    this.kvStore.remove(operation.key);
                    // Log success message.
                    this.logger.log("> Deleted the key-value pair associated with \"" + operation.key + "\"");
                } else {
                    // Log failure message if the key does not exist.
                    this.logger.log("> Error: the key \"" + operation.key + "\" does not exist");
                }
                break;
            default:
                // Log and throw an error if the operation type is unknown.
                this.logger.log("> Error: Unknown operation type: " + operation.type);
                throw new IllegalArgumentException("> Error: Unknown operation type: " + operation.type);
        }
    }


    /**
     * Proposes an operation to be executed using the Paxos consensus algorithm.
     */
    private boolean propose(Operation operation) throws RemoteException {
        // Generate a new unique sequence number
        this.maxObservedSequenceNumber = generateSequenceNumber();
        this.logger.log("> A sequence number has been generated: " + this.maxObservedSequenceNumber);

        // Execute the Paxos algorithm
        return runExecutePaxosAlgorithm(this.maxObservedSequenceNumber, operation);
    }


    /**
     * Handles the "prepare" phase of the Paxos consensus protocol.
     * This method checks if the proposed sequence number is acceptable and makes a promise
     */
    @Override
    public synchronized Object prepare(long sequenceNumber) throws RemoteException {

        // Simulate a failure scenario using the RANDOM_FAILURE_PROBABILITY we specified above
        double random = this.randomGenerator.nextDouble();

        // Condition for probability being true
        if (random < RANDOM_FAILURE_PROBABILITY) {
            this.logger.log(this + " failed (simulated) (" + random + " < " + RANDOM_FAILURE_PROBABILITY + ")");
            throw new RemoteException(this + " failed (simulated)");
        }

        // Check if the proposed sequence number is the highest observed.
        if (sequenceNumber > this.maxObservedSequenceNumber) {
            this.maxObservedSequenceNumber = sequenceNumber;
            if (this.lastAcceptedValue != null) {
                this.logger.log("> Already accepted " + this.lastAcceptedValue + " associated with the sequence number " + this.lastAcceptedSequenceNumber);
                return new Promise(1, this.lastAcceptedSequenceNumber, this.lastAcceptedValue);
            } else {
                this.logger.log("> Sent promise message (" + sequenceNumber + " > " + this.maxObservedSequenceNumber + ")");
                return new Promise(1);
            }
        } else {
            this.logger.log("> Did not send promise message (" + sequenceNumber + " <= " + this.maxObservedSequenceNumber + ")");
            return new Promise(0);
        }
    }


    /**
     * Accepts or rejects a proposed value based on the Paxos consensus protocol.
     */
    @Override
    public synchronized long accept(long sequenceNumber, Object proposalValue) throws RemoteException {
        // Implement Paxos accept logic here
        double random = this.randomGenerator.nextDouble();
        if (random < RANDOM_FAILURE_PROBABILITY) { // simulate server failure
            this.logger.log(this + " failed (" + random + " < " + RANDOM_FAILURE_PROBABILITY + ")");
            throw new RemoteException(this + " failed");
        } else {
            if (sequenceNumber >= this.maxObservedSequenceNumber) {
                this.logger.log(proposalValue + " associated with sequence number " + sequenceNumber + " accepted");
                this.lastAcceptedValue = proposalValue;
                this.maxObservedSequenceNumber = sequenceNumber;
                this.lastAcceptedSequenceNumber = sequenceNumber;
            } else {
                this.logger.log(proposalValue + " associated with sequence number " + sequenceNumber + " rejected");
            }
        }
        return this.maxObservedSequenceNumber;
    }

    /**
     * Implements the learning phase of the Paxos protocol, where the accepted value is learned and executed.
     */
    @Override
    public synchronized void learn(long sequenceNumber, Object acceptedValue) throws RemoteException {
        // Implement Paxos learn logic here
        this.runModificationOperation((Operation) acceptedValue);
        this.logger.log(acceptedValue.toString() + " learned and executed");
        this.lastAcceptedValue = null;
        this.maxObservedSequenceNumber = sequenceNumber;
        this.lastAcceptedSequenceNumber = -Long.MAX_VALUE;
    }

    /**
     * Returns a string representation of this server.
     */
    @Override
    public String toString() {
        return "Server{" + "serverId=" + serverId + '}';
    }

    /**
     * Setter for regristry
     */
    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    /**
     * Shuts down this server instance, unbinding it from the RMI registry and unexporting it from RMI runtime.
     */
    @Override
    public void shutdown() throws RemoteException {
        this.logger.log("> Received a request to shut down...");
        System.out.println(this + " is shutting down...");
        try {
            this.registry.unbind("KVStore" + this.serverId); // unbind the remote object from the custom name
            this.logger.log(this + " unbound in registry");
        } catch (NotBoundException e) {
            this.logger.log("Unbind error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        UnicastRemoteObject.unexportObject(this, true); // unexport the remote object
        this.logger.log(this + " unexported");
        this.logger.log(this + " closed");
        this.logger.close();
        System.out.println(this + " closed");
    }

    /**
     * Represents an operation to be performed on a key-value store
     */
    private static class Operation {
        final String type;
        final String key;
        final String value;

        /**
         * Constructs a complete operation with a type, key, and value
         */
        Operation(String type, String key, String value) {
            this.type = type;
            this.key = key;
            this.value = value;
        }

        /**
         * Constructs an operation with only a type and key
         */
        Operation(String type, String key) {
            this(type, key, null);
        }

        @Override
        public String toString() {
            return "Operation{" +
                    "type='" + type + '\'' +
                    ", key='" + key + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }


    /**
     * Represents a Promise as part of the Paxos consensus protocol
     */
    private static class Promise {
        final int vote;
        final Long sequenceNumber;
        final Object value;

        /**
         * Constructs a Promise with a specific vote, sequence number, and value.
         */
        Promise(int vote, Long sequenceNumber, Object value) {
            this.vote = vote;
            this.sequenceNumber = sequenceNumber;
            this.value = value;
        }

        /**
         * Constructs a Promise with only a vote.
         */
        Promise(int vote) {
            this(vote, null, null);
        }
    }

}
