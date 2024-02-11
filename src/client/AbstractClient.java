package client;

/**
 * AbstractClient class for the client application
 */
public abstract class AbstractClient {
    protected String hostname;
    protected int port;

    /**
     * AbstractClient client
     * @param hostname
     * @param port
     */
    public AbstractClient(String hostname, int port) {

        // Set hostname and port per requirements
        this.hostname = hostname;
        this.port = port;
    }

    /**
     * Method to send a request
     * @param request
     */
    public abstract void sendRequest(String request);
}
