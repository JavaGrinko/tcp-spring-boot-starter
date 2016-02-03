## Описание
**tcp-spring-boot-starter** - Spring Boot библиотека, которая позволяет быстро разворачивать TCP-сервера. Включает в себя:
* **@TcpController** - интерфейс, помечающий класс как TCP-контроллер,
* **ServerUtils** - бин, с помощью которого можно управлять сервером.

Контроллер может содержать три типа методов:
* Метод-событие получения сообщения. Должен начинаться со слова receive и иметь два аргумента: Connection и Object (или любой другой, тогда метод приема будет типизирован).
* Метод-событие нового подключения. Должен начинаться со слова connect и иметь аргумент Connection
* Метод-событие отключения клиента. Должен начинаться со слова disconnect и иметь аргумент Connection

## Примеры
```java
import javagrinko.spring.tcp.Connection;
import javagrinko.spring.tcp.TcpController;

@TcpController
public class EchoController {

    public void receiveData(Connection connection, byte[] data) {
        String s = new String(data);
        connection.send(answer.toUpperCase().getBytes());
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
javagrinko.tcp_server.port      = 20502
javagrinko.tcp_server.autoStart = true
```

## Установка
1) Необходимо добавить зависимость в build.gradle:
```
repositories {
    maven {
        url  "http://dl.bintray.com/javagrinko/maven"
    }
}

dependencies {
    compile 'javagrinko:spring-tcp-controller:0.28'
    ...
}

```

2) в файле src/main/resources/application.properties задать настройки сервера:
```
#Порт сервера
javagrinko.tcp_server.port      = 20502
#Автостарт сервера после загрузки application context
javagrinko.tcp_server.autoStart = true
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
        server.setPort(9999);
        server.start();
    }
}
```
