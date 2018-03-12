#### [Просмотр русской версии этого файла здесь](./README.md)

## Description
**tcp-spring-boot-starter** - Spring Boot library that allows you to quickly deploy a TCP server. Includes:
* **@TcpController** - Annotation marking a class as a TCP controller,
* **ServerUtils** - bean, with which you can manage the server.

The controller can contain three types of methods:
* The event-receiving method of the message. Must start with the word `receive` and have two arguments: `Connection` and `Object` (or any other, then the receive method will be typed).
* New connection method-event. Must start with connect and have the `Connection` argument
* Client-disconnect method-event. Must start with the word `disconnect` and have the `Connection` argument

## Examples
```java
import javagrinko.spring.tcp.Connection;
import javagrinko.spring.tcp.TcpController;

@TcpController
public class EchoController {

    public void receiveData(Connection connection, byte[] data) {
        String s = new String(data);
        connection.send(s.toUpperCase().getBytes());
    }

    public void connect(Connection connection) {
        System.out.println("New connection " + connection.getAddress().getCanonicalHostName());
    }

    public void disconnect(Connection connection) {
        System.out.println("Disconnect " + connection.getAddress().getCanonicalHostName());
    }
}
```
**application.properties**:
```
javagrinko.tcp-server.port      = 20502
javagrinko.tcp-server.auto-start = true
```

## Installation
1) You need to add the dependency to your build.gradle:
```
repositories {
    maven {
        url "http://jcenter.bintray.com"
    }
}

dependencies {
    compile 'javagrinko:tcp-spring-boot-starter:1.10'
    ...
}

```

2) In the file `src/main/resources/application.properties` set the server settings:
```
#Server Port
javagrinko.tcp-server.port      = 20502
#Autostart server after loading application context
javagrinko.tcp-server.auto-start = true
```

If `autostart` is not set or set to **false**, you must manually start the server from the code:
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
