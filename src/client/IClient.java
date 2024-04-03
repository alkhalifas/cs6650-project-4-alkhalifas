package client;

/**
 * Interface defining the behavior expected from a client.
 */
public interface IClient {

    /**
     * Retrieves a user request.
     *
     * @return the user request as a string
     */
    String getRequest();

    /**
     * Pre-populates necessary data in KV store.
     */
    void prePopulate();

    /**
     * Initiates the execution of client operations.
     * This method typically includes the main logic of the client.
     */
    void execute();

    /**
     * Shuts down the client, performing necessary cleanup tasks.
     */
    void shutdown();
}
