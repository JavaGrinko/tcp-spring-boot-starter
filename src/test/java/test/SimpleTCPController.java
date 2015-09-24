package test;

import org.springframework.tcp.Connection;
import org.springframework.tcp.TcpController;

@TcpController
public class SimpleTCPController {

    public void receiveTest(Connection connection, byte[] request) {
        System.out.println("connection = " + connection);
        connection.send(new String("Спасибки!\n").getBytes());
    }

    public void connectSuperPuper(Connection connection) {
        System.out.println("К нам подключился " + connection.getAddress().getCanonicalHostName() + "!");
    }

    public void disconnectEvent(Connection connection) {
        System.out.println("Отключился " + connection.getAddress().getCanonicalHostName() + ":-(");
    }
}
