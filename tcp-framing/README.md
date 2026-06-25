# TCP Framing Demo

This project demonstrates a simple TCP client/server exchange with length-prefixed message framing.

## What it does

- `Server` listens on port `8082`
- `Client` connects to the server and reads messages from the console
- Each message is sent as:
  - 4 bytes of message length
  - message bytes in UTF-8
- The connection closes when the user sends `bye`

## Project structure

- `src/main/java/tcp/Server.java` - TCP server
- `src/main/java/tcp/Client.java` - TCP client
- `src/main/java/tcp/Protocol.java` - framing helpers

## How it works

The client converts a text message into a byte array with a 4-byte big-endian length prefix.  
The server reads the first 4 bytes, decodes the length, then reads exactly that many bytes for the message body.

## Requirements

- Java 21
- Maven

## Run

1. Start the server:

```bash
mvn -q -DskipTests compile
java -cp target/classes tcp.Server
```

2. Start the client in a separate terminal:

```bash
java -cp target/classes tcp.Client
```

If you prefer, run both classes from your IDE instead.

---

# TCP Framing Demo

Этот проект показывает простой обмен данными между TCP-клиентом и сервером с использованием framing по длине сообщения.

## Что делает проект

- `Server` слушает порт `8082`
- `Client` подключается к серверу и читает сообщения из консоли
- Каждое сообщение отправляется как:
  - 4 байта длины сообщения
  - байты сообщения в UTF-8
- Соединение закрывается, когда пользователь отправляет `bye`

## Структура проекта

- `src/main/java/tcp/Server.java` - TCP сервер
- `src/main/java/tcp/Client.java` - TCP клиент
- `src/main/java/tcp/Protocol.java` - вспомогательные методы для framing

## Как это работает

Клиент преобразует текстовое сообщение в массив байт и добавляет 4-байтовый big-endian префикс длины.  
Сервер читает первые 4 байта, декодирует длину, а затем читает ровно столько байт, сколько нужно для тела сообщения.

## Требования

- Java 21
- Maven

## Запуск

1. Запустите сервер:

```bash
mvn -q -DskipTests compile
java -cp target/classes tcp.Server
```

2. Запустите клиент в отдельном терминале:

```bash
java -cp target/classes tcp.Client
```

При желании можно запускать оба класса из IDE.
