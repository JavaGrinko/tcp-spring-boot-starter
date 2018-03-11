## Description
**tcp-spring-boot-starter** - is convenient dependency descriptor that you can include in your application to get started using Spring and TCP-servers.
## Usage
```java
import javagrinko.spring.tcp.Connection;
import javagrinko.spring.tcp.TcpController;

@TcpController
public class EchoController implements TcpHandler {

    @Override
    public void receiveData(Connection connection, byte[] data) {
        String s = new String(data);
        connection.send(s.toUpperCase().getBytes());
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
```
**@TcpController** - indicates that an annotated class is a TCP-Controller.

## Configuration

| Setting key        | Type           | Default value  |
| ------------- |:-------------:| -----:|
| tcp.server.port      | Integer | 1234 |
| tcp.server.autoStart      | Boolean      |   true |

**application.properties**:

```
tcp.server.port      = 20502
tcp.server.auto-start = true
```

## Установка
1) Необходимо добавить зависимость в build.gradle:
```
repositories {
    maven {
        url "http://jcenter.bintray.com"
    }
}

dependencies {
    compile 'javagrinko:tcp-spring-boot-starter:1.12'
    ...
}

```

2) в файле src/main/resources/application.properties задать настройки сервера:
```
#Порт сервера
javagrinko.tcp-server.port      = 20502
#Автостарт сервера после загрузки application context
javagrinko.tcp-server.auto-start = true
```

Если автостарт не задан или установлено значение **false**, то необходимо вручную запустить сервер из кода:
```java
@Component
public class Starter {
    @Autowired
    ServerUtils serverUtils;

    @PostConstruct
    void setUp(){
        Server server = serverUtils.getServer();
        server.setPort(20502);
        server.start();
    }
}
```
