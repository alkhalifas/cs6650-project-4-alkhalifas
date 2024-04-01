package client;

public interface IClient {
    String getRequest();
    void prePopulate();
    void execute();
    void shutdown();
}
