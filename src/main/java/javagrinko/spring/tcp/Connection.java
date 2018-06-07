package javagrinko.spring.tcp;

import java.net.InetAddress;

public interface Connection {
    InetAddress getAddress();
    void send(Object objectToSend);
    void addListener(Listener listener);
    void start();
    void close();

    void startTimeOutTimer();
    void stopTimeOutTimer();
    void restartTimeOutTimer();

    interface Listener {
        void messageReceived(Connection connection, Object message);
        void connected(Connection connection);
        void disconnected(Connection connection);
        void timedout(Connection connection);
    }
}
