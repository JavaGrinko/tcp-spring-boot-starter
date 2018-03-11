package javagrinko.spring.tcp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class TcpServer implements Server, Connection.Listener {
    private static Log logger = LogFactory.getLog(TcpServer.class);

    private ServerSocket serverSocket;
    private volatile boolean isStop;
    private List<Connection> connections = new CopyOnWriteArrayList<>();
    private List<Connection.Listener> listeners = new CopyOnWriteArrayList<>();

    public void setPort(Integer port) {
        try {
            serverSocket = new ServerSocket(port);
            logger.info("Server start at port " + port);
        } catch (IOException e) {
            logger.error("Port " + port + " busy.", e);
        }
    }

    @Override
    public int getConnectionsCount() {
        return connections.size();
    }

    @Override
    public void start() {
        new Thread(() -> {
            while (!isStop) {
                try {
                    Socket socket = serverSocket.accept();
                    if (socket.isConnected()) {
                        TcpConnection tcpConnection = new TcpConnection(socket);
                        tcpConnection.start();
                        tcpConnection.addListener(this);
                        connected(tcpConnection);
                    }
                } catch (IOException | IllegalAccessException | InvocationTargetException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }).start();
    }

    @Override
    public void stop() {
        isStop = true;
    }

    @Override
    public List<Connection> getConnections() {
        return connections;
    }

    @Override
    public void addListener(Connection.Listener listener) {
        listeners.add(listener);
    }

    @Override
    public void messageReceived(Connection connection, byte[] bytes)
            throws InvocationTargetException, IllegalAccessException {
        if (logger.isTraceEnabled()) {
            logger.trace("Received message from " + connection.getAddress().getCanonicalHostName());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02X ", b));
            }
            logger.trace(" <== " + sb.toString());
        }
        for (Connection.Listener listener : listeners) {
            listener.messageReceived(connection, bytes);
        }
    }

    @Override
    public void connected(Connection connection)
            throws InvocationTargetException, IllegalAccessException {
        logger.info("New connection! Ip: " + connection.getAddress().getCanonicalHostName() + ".");
        connections.add(connection);
        logger.info("Current connections count: " + connections.size());
        for (Connection.Listener listener : listeners) {
            listener.connected(connection);
        }
    }

    @Override
    public void disconnected(Connection connection)
            throws InvocationTargetException, IllegalAccessException {
        logger.info("Disconnect! Ip: " + connection.getAddress().getCanonicalHostName() + ".");
        connections.remove(connection);
        logger.info("Current connections count: " + connections.size());
        for (Connection.Listener listener : listeners) {
            listener.disconnected(connection);
        }
    }
}
