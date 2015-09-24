## Описание
**Spring TCP Server** - это плагин для Spring, который позволяет быстро сделать TCP-сервер. Включает в себя:
* **@TcpController** - интерфейс, помечающий класс как TCP-контроллер,
* **ServerUtils** - бин, с помощью которого можно управлять сервером.

Контроллер может содержать три типа методов:
* Метод-событие получения сообщения. Должен начинаться со слова receive и иметь два аргумента: Connection и Object (или любой другой, тогда метод приема будет типизирован).
* Метод-событие нового подключения. Должен начинаться со слова connect и иметь аргумент Connection
* Метод-событие отключения клиента. Должен начинаться со слова disconnect и иметь аргумент Connection

## Примеры
```java
import org.springframework.tcp.Connection;
import org.springframework.tcp.TcpController;

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
tcp.server.port=18502
tcp.server.autostart=true
```
**ApplicationContext:**
```java
@Configuration
@EnableIntegration
@ComponentScan({"org.springframework.tcp", //!!! Для активации плагина необходимо добавить эту строку
                "ru.vympel"})
@EnableAutoConfiguration
public class Main {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Main.class);
    }
}
```

## Установка
1) Необходимо подключить к Spring Boot проекту библиотеку **springtcp-X.X.jar

2) В конфигурацию добавить сканирование пакета из библиотеки:
```java
@ComponentScan({"org.springframework.tcp","ВАШ_ПАКЕТ"}
```
3) в файле src/main/resources/application.properties задать настройки сервера:
```
#Порт сервера
tcp.server.port=18502
#Автостарт сервера после загрузки application context
tcp.server.autostart=true
```

Если автостарт не задан или установлено значение **false**, то необходимо вручную запустить сервер из кода:
```java
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