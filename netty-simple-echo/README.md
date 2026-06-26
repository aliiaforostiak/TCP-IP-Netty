# netty-simple-echo

Simple echo server and client example built with Netty.

## What is inside

- `netty.server.NettyEchoServer` starts a server on port `8083`.
- `netty.server.ServerHandler` receives a message and echoes it back to the client.
- `netty.client.NettyClient` connects to the server and sends the string `Netty rocks!`.
- `netty.client.ClientHandler` reads the server response and prints it to the console.

## Requirements

- Java 21
- Maven

## Build

```bash
mvn clean package
```

## Run

Start the server first:

```bash
mvn -Dexec.mainClass=netty.server.NettyEchoServer exec:java
```

Then start the client:

```bash
mvn -Dexec.mainClass=netty.client.NettyClient exec:java
```

If `exec-maven-plugin` is not configured, you can run the classes directly from your IDE.

## Important `ByteBuf` note

This project intentionally emphasizes manual buffer release in handlers that extend `ChannelInboundHandlerAdapter`.

In `ClientHandler.channelRead(...)`, the incoming message arrives as a `ByteBuf`, and it must be released after reading:

```java
finally {
    buffer.release();
}
```

This matters because `ChannelInboundHandlerAdapter` does not automatically release inbound messages. Forgetting this can lead to memory leaks. The same pattern is used on the server side as well.

## Structure

```text
src/main/java/netty/client
src/main/java/netty/server
```

---

Простой пример echo-сервера и клиента на Netty.

## Что внутри

- `netty.server.NettyEchoServer` поднимает сервер на `8083`.
- `netty.server.ServerHandler` принимает сообщение и отправляет его обратно клиенту.
- `netty.client.NettyClient` подключается к серверу и отправляет строку `Netty rocks!`.
- `netty.client.ClientHandler` читает ответ сервера и выводит его в консоль.

## Требования

- Java 21
- Maven

## Сборка

```bash
mvn clean package
```

## Запуск

Сначала сервер:

```bash
mvn -Dexec.mainClass=netty.server.NettyEchoServer exec:java
```

Потом клиент:

```bash
mvn -Dexec.mainClass=netty.client.NettyClient exec:java
```

Если плагин `exec-maven-plugin` не подключен, можно запускать классы из IDE напрямую.

## Важный момент про `ByteBuf`

В этом проекте специально сделан акцент на ручном освобождении буфера в обработчиках, которые наследуются от `ChannelInboundHandlerAdapter`.

В `ClientHandler.channelRead(...)` сообщение приходит как `ByteBuf`, и после чтения его нужно обязательно освободить:

```java
finally {
    buffer.release();
}
```

Это критично, потому что `ChannelInboundHandlerAdapter` сам не занимается автоматическим release входящих сообщений. Если забыть это сделать, можно получить утечки памяти. Такой же подход используется и на стороне сервера.

## Структура

```text
src/main/java/netty/client
src/main/java/netty/server
```
