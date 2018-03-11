package javagrinko.spring.tcp;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@Component
public class TcpControllerBeanPostProcessor implements BeanPostProcessor {
    private Map<String, Class> cache = new HashMap<>();

    @Autowired
    private Server server;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        if (bean instanceof TcpHandler) {
            cache.put(beanName, beanClass);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (cache.containsKey(beanName)) {
            TcpHandler tcpHandler = (TcpHandler) bean;
            server.addListener(new Connection.Listener() {
                @Override
                public void messageReceived(Connection connection, byte[] bytes)
                        throws InvocationTargetException, IllegalAccessException {
                    tcpHandler.receiveData(connection, bytes);
                }

                @Override
                public void connected(Connection connection) throws InvocationTargetException, IllegalAccessException {
                    tcpHandler.connectEvent(connection);
                }

                @Override
                public void disconnected(Connection connection) throws InvocationTargetException, IllegalAccessException {
                    tcpHandler.disconnectEvent(connection);
                }
            });
        }
        return bean;
    }
}
