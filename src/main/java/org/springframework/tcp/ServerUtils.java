package org.springframework.tcp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ServerUtils {
    private Server server;

    @Autowired
    Environment environment;

    private Integer port;

    private Boolean autoStart;

    public Server getServer() {
        return server;
    }

    @PostConstruct
    public void setUp(){
        port = environment.getProperty("tcp.server.port", Integer.class, 1234);
        autoStart = environment.getProperty("tcp.server.autostart", Boolean.class, false);
    }

    protected void setServer(Server server) {
        this.server = server;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Boolean getAutoStart() {
        return autoStart;
    }

    public void setAutoStart(Boolean autoStart) {
        this.autoStart = autoStart;
    }
}
