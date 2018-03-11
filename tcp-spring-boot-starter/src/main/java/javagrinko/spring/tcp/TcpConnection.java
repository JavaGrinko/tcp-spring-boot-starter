package javagrinko.spring.tcp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TcpConnection implements Connection {
    private static Log logger = LogFactory.getLog(TcpConnection.class);
    private InputStream inputStream;
    private OutputStream outputStream;
    private Socket socket;
    private List<Listener> listeners = new CopyOnWriteArrayList<>();

    TcpConnection(Socket socket) {
        this.socket = socket;
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public InetAddress getAddress() {
        return socket.getInetAddress();
    }

    @Override
    public void send(byte[] bytes) throws IOException {
        outputStream.write(bytes);
        logger.trace("Sent message");
        if (logger.isTraceEnabled()) {
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02X ", b));
            }
            logger.trace(" ==> " + sb.toString());
        }
    }

    @Override
    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    @Override
    public void start() {
        new Thread(() -> {
            while (true) {
                byte buf[] = new byte[64 * 1024];
                try {
                    int count = inputStream.read(buf);
                    if (count > 0) {
                        byte[] bytes = Arrays.copyOf(buf, count);
                        for (Listener listener : listeners) {
                            listener.messageReceived(this, bytes);
                        }
                    } else {
                        socket.close();
                        disconnectAll();
                        break;
                    }
                } catch (IOException | IllegalAccessException | InvocationTargetException e) {
                    logger.error(e.getMessage(), e);
                    disconnectAll();
                    break;
                }
            }
        }).start();
    }

    private void disconnectAll() {
        for (Listener listener : listeners) {
            try {
                listener.disconnected(this);
            } catch (InvocationTargetException | IllegalAccessException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
