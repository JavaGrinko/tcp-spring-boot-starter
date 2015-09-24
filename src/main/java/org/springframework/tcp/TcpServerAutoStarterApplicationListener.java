package org.springframework.tcp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class TcpServerAutoStarterApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    ServerUtils serverUtils;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        boolean autoStart = serverUtils.getAutoStart();
        if (autoStart){
            Server server = serverUtils.getServer();
            server.setPort(serverUtils.getPort());
            server.start();
        }
    }
}
