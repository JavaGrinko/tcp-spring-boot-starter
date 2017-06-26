package javagrinko.spring.tcp;

import java.net.InetSocketAddress;

public interface Connection {
    InetSocketAddress getSocketAddress();
    void send(Object objectToSend);
    void addListener(Listener listener);
    void start();
    void close();

    interface Listener {
        void messageReceived(Connection connection, Object message);
        void connected(Connection connection);
        void disconnected(Connection connection);
    }
}
