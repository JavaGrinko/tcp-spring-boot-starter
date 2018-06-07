package javagrinko.spring.tcp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;

public class TcpConnection implements Connection {
    private static Log logger = LogFactory.getLog(TcpConnection.class);

    private InputStream inputStream;
    private OutputStream outputStream;
    private Socket socket;
    private List<Listener> listeners = new ArrayList<>();

    private Timer timeOutTimer;
    private TimerTask timeOutTask = new TimerTask() {
        @Override
        public void run() {
            for (Listener listener : listeners) {
                listener.timedout(TcpConnection.this);
            }
        }
    };

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

        new Thread(() -> {
            while (true) {
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
                        socket.close();

                        // Every time the connection gets closed, stop the TimeOut Timer
                        stopTimeOutTimer();

                        for (Listener listener : listeners) {
                            listener.disconnected(this);
                        }
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                    // Every time the connection gets closed, stop the TimeOut Timer
                    stopTimeOutTimer();

                    for (Listener listener : listeners) {
                        listener.disconnected(this);
                    }
                    break;
                }
            }
        }).start();
    }

    @Override
    public void close() {
        try {
            socket.close();

            // Every time the connection gets closed, stop the TimeOut Timer
            stopTimeOutTimer();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startTimeOutTimer() {
        logger.info("Start Timer");
        //timeOutTimer = new Timer();
        //timeOutTimer.schedule(timeOutTask, 10000);
    }

    @Override
    public void stopTimeOutTimer() {
        /*if (null != timeOutTimer){
            timeOutTimer.cancel();
        }*/
        logger.info("Stop Timer");
    }

    @Override
    public void restartTimeOutTimer() {
        logger.info("RE-Start Timer");
        stopTimeOutTimer();
        startTimeOutTimer();
    }
}
