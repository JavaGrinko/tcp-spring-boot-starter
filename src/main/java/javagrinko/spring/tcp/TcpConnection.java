package javagrinko.spring.tcp;

import javagrinko.spring.starter.TcpServerProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;

public class TcpConnection implements Connection {
    private static Log logger = LogFactory.getLog(TcpConnection.class);

    @Autowired
    private TcpServerProperties properties;

    private InputStream inputStream;
    private OutputStream outputStream;
    private Socket socket;
    private List<Listener> listeners = new ArrayList<>();

    private Timer timeOutTimer;
    private Thread connectionThread;

    public TcpConnection(Socket socket) {
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
    public void send(Object objectToSend) {
        if (objectToSend instanceof byte[]) {
            byte[] data = (byte[]) objectToSend;
            try {
                outputStream.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    @Override
    public void start() {
        // Every time the connection start, set the TimeOut Timer
        startTimeOutTimer();

        connectionThread = new Thread(() -> {
            while (!connectionThread.isInterrupted()) {
                byte buf[] = new byte[64 * 1024];
                try {
                    int count = inputStream.read(buf);
                    if (count > 0) {
                        byte[] bytes = Arrays.copyOf(buf, count);

                        // Every time the connection is kept alive, re-start the TimeOut Timer
                        restartTimeOutTimer();

                        for (Listener listener : listeners) {
                            listener.messageReceived(this, bytes);
                        }
                    } else {
                        // Don't want to close the socket after data is read
                        /*socket.close();
                        logger.info("Receibi nada....");
                        // Every time the connection gets closed, stop the TimeOut Timer
                        stopTimeOutTimer();

                        for (Listener listener : listeners) {
                            listener.disconnected(this);
                        }
                        break;*/
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                    logger.debug("Exception in ConnecionThread.");

                    // BREAK just quit from the loop
                    break;
                }
            }

            // Whenever the thread stops
            //  - Client disconnects
            //  - Server closes the connection (for example in TimeOut)
            // Then it will notify the disconnection
            logger.debug("ConnectionThread is dead!");

            // Every time the connection gets closed, stop the TimeOut Timer
            stopTimeOutTimer();

            // Notify about the disconnection (closed socket)
            for (Listener listener : listeners) {
                listener.disconnected(this);
            }
        });

        connectionThread.start();
    }

    @Override
    public void close() {
        logger.info("Closing connection...");
        try {
            // Need to interrupt the thread and also close the socket else it won't work.
            // Then the connectioThread will stop and there it will notify the event
            connectionThread.interrupt();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void startTimeOutTimer() {
        long timeout = properties.getTimeout();

        logger.debug("Start Timer");
        TimerTask timeOutTask = new TimerTask() {
            @Override
            public void run() {
                for (Listener listener : listeners) {
                    listener.timedout(TcpConnection.this);
                }
            }
        };
        timeOutTimer = new Timer();
        timeOutTimer.schedule(timeOutTask, timeout);
    }

    @Override
    public synchronized void stopTimeOutTimer() {
        if (null != timeOutTimer){
            logger.debug("Timer Cancelled");
            timeOutTimer.cancel();
            timeOutTimer = null;
        }
    }

    @Override
    public synchronized void restartTimeOutTimer() {
        logger.debug("Timer Restarted");
        stopTimeOutTimer();
        startTimeOutTimer();
    }
}
