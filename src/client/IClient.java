package client;

/**
 * Defines client operations in a distributed system to manage and interact with a key-value store.
 */
public interface IClient {

    /**
     * Initializes the key-value store with predefined data entries for readiness and testing.
     */
    void prePopulate();

    /**
     * Captures and returns a user input representing a request for an operation on the key-value store.
     */
    String getRequest();

    /**
     * Manages the execution lifecycle of the client, processing requests until a shutdown command is received.
     */
    void execute();

    /**
     * Cleans up resources and terminates the client application gracefully.
     */
    void shutdown();
}
