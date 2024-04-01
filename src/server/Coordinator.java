package coordinator;

import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import participant.IParticipant;
import utils.ILogger;
import utils.Logger;


public class Coordinator extends UnicastRemoteObject implements ICoordinator {
    private static final String loggerName = "CoordinatorLogger";
    private static final String logFileName = "CoordinatorLog.log";
    private static final int numParticipants = 5;
    private static final String service = "TranslationService";
    private final List<IParticipant> participants;
    private final List<Registry> participantRegistries;
    private String state = "INITIAL";
    private final ILogger logger;


    public Coordinator() throws RemoteException {
        super();
        this.logger = new Logger(loggerName, logFileName);
        this.participants = new ArrayList<>(numParticipants);
        this.participantRegistries = new ArrayList<>(numParticipants);

        // Setting the time outs per the reqs
        System.setProperty("sun.rmi.transport.tcp.responseTimeout", "2000");
        System.setProperty("sun.rmi.transport.proxy.connectTimeout", "4000");
    }


    @Override
    public void connectToParticipants(List<String> participantHosts, List<Integer> participantPorts) throws RemoteException {
        for (int i = 0; i < participantHosts.size(); i++) {
            try {
                Registry registry = LocateRegistry.getRegistry(participantHosts.get(i), participantPorts.get(i));
                this.logger.log("Stub to the remote object Registry on the host " + participantHosts.get(i) + " and port " + participantPorts.get(i) + " created");
                IParticipant participant = (IParticipant) registry.lookup(service);
                this.logger.log("Stub bound to " + service + " in the remote object Registry");
                this.participants.add(participant);
                this.participantRegistries.add(registry);
                this.logger.log("Participant " + participantHosts.get(i) + " at port " + participantPorts.get(i) + " added to the list");
            } catch (ConnectException ce) {
                this.logger.log("Connection to participant " + participantHosts.get(i) + " at port " + participantPorts.get(i) + " timed out: " + ce.getMessage());
            } catch (Exception e) {
                this.logger.log("Unable to connect to participant " + participantHosts.get(i) + " at port " + participantPorts.get(i) + ": " + e.getMessage());
                throw new RemoteException("Unable to connect to participant " + participantHosts.get(i) + " at port " + participantPorts.get(i), e);
            }
        }
    }

    private boolean phaseOne(String operation, String key) throws RemoteException {
        this.logger.log("begin_commit");
        for (IParticipant participant : this.participants) {
            try {
                if (!participant.isReady(operation, key)) {
                    this.logger.log("abort: participant at port " + participant.getPort() + " aborted the transaction");
                    this.state = "ABORT";
                    this.logger.log("end_of_transaction");
                    return false;
                }
            } catch (ConnectException e) {
                this.logger.log("Communication with the participant at port " + participant.getPort() + " timed out (phase 1): " + e.getMessage());
                return false;
            }
        }
        return true;
    }

    private void phaseTwo(String key, String value) throws RemoteException {
        this.state = "COMMIT";
        this.logger.log("commit");
        for (IParticipant participant : this.participants) {
            try {
                participant.commit(key, value);
            } catch (ConnectException e) {
                this.logger.log("Communication with the participant at port " + participant.getPort() + " timed out (phase 2): " + e.getMessage());
            }
        }
    }

    private void phaseTwo(String key) throws RemoteException {
        this.state = "COMMIT";
        this.logger.log("commit");
        for (IParticipant participant : this.participants) {
            try {
                participant.commit(key);
            } catch (ConnectException e) {
                this.logger.log("Communication with the participant at port " + participant.getPort() + " timed out (phase 2): " + e.getMessage()); // should have a retry mechanism
            }
        }
    }

    private boolean getAck() throws RemoteException {
        for (IParticipant participant : this.participants) {
            try {
                if (participant.getState().equals("INITIAL")) {
                    this.logger.log("Participant at port " + participant.getPort() + " committed");
                } else {
                    this.logger.log("Participant at port " + participant.getPort() + " did not commit");
                    this.state = "INITIAL";
                    this.logger.log("end_of_transaction");
                    return false;
                }
            } catch (ConnectException e) {
                this.logger.log("Communication with the participant at port " + participant.getPort() + " timed out (ack): " + e.getMessage());
            }
        }
        return true;
    }


    @Override
    public synchronized boolean prepareTransaction(String operation, String key, String value) throws RemoteException {
        this.logger.log("Received a request to save " + "\"" + key + "\"" + " mapped to " + "\"" + value + "\"");
        // 1st phase
        if (!this.phaseOne(operation, key)) {
            return false;
        }
        this.state = "WAIT";
        // 2nd phase
        this.phaseTwo(key, value);
        if (!this.getAck()) {
            return false;
        }
        this.state = "INITIAL";
        this.logger.log("end_of_transaction");
        return true;
    }


    @Override
    public synchronized boolean prepareTransaction(String operation, String key) throws RemoteException {
        this.logger.log("Received a request to delete the key-value pair associated with the key: \"" + key + "\"");
        // 1st phase
        if (!this.phaseOne(operation, key)) {
            return false;
        }
        this.state = "WAIT";
        // 2nd phase
        this.phaseTwo(key);
        if (!this.getAck()) {
            return false;
        }
        this.state = "INITIAL";
        this.logger.log("end_of_transaction");
        return true;
    }


    @Override
    public void shutdown(Registry registry) throws RemoteException {
        this.logger.log("Received a request to shut down...");
        System.out.println("Participants are shutting down...");
        for (int i = 0; i < this.participants.size(); i++) {
            try {
                this.participants.get(i).shutdown(this.participantRegistries.get(i));
            } catch (ConnectException ce) {
                this.logger.log("Communication with the participant at port " + this.participants.get(i).getPort() + " timed out while attempting to shut it down: " + ce.getMessage()); // should have a retry mechanism
            }
        }
        this.logger.log("Participants closed");
        System.out.println("Participants closed");
        try {
            registry.unbind("Coordinator");
            this.logger.log("Coordinator unbound in registry");
        } catch (NotBoundException nbe) {
            this.logger.log("Unbind error: " + nbe.getMessage());
            nbe.printStackTrace();
            System.exit(1);
        }
        UnicastRemoteObject.unexportObject(this, true);
        this.logger.log("Coordinator unexported");
        this.logger.log("Coordinator closed");
        this.logger.close();
        System.out.println("Coordinator closed");
    }
}
