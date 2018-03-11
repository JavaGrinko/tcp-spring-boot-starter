package javagrinko.spring.tcp;

public interface TcpHandler {
    void receiveData(Connection connection, byte[] data);
    void connectEvent(Connection connection);
    void disconnectEvent(Connection connection);
}