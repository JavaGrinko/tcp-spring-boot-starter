package ru.javainside.tcpserver;

import javagrinko.spring.tcp.Connection;
import javagrinko.spring.tcp.TcpController;
import javagrinko.spring.tcp.TcpHandler;

import java.io.IOException;

@TcpController
public class EchoController implements TcpHandler {

    @Override
    public void receiveData(Connection connection, byte[] data) {
        String s = new String(data);
        try {
            connection.send(s.toUpperCase().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connectEvent(Connection connection) {
        System.out.println("New connection " + connection.getAddress().getCanonicalHostName());
    }

    @Override
    public void disconnectEvent(Connection connection) {
        System.out.println("Disconnect " + connection.getAddress().getCanonicalHostName());
    }
}
