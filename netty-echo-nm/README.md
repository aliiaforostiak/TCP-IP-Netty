# Netty Echo Example

This project is a small echo client/server example based on *Netty in Action*.

It demonstrates how to build a simple Netty application with:

- `EchoServer`, which listens on a TCP port and echoes received data back to the client
- `EchoClient`, which connects to the server, sends a message, and prints the echoed response

In `EchoClient.java`, the client connects to the given host and port, sends the text `Netty rocks!`, and waits for the server response.

## How it works

1. Start the server on a port.
2. Start the client with the same host and port.
3. The client sends a message.
4. The server receives it and sends it back.
5. The client prints the echoed message.

---

# Пример Netty Echo

Этот проект представляет собой небольшой пример echo-клиента и echo-сервера на основе книги *Netty in Action*.

Он показывает, как собрать простое приложение на Netty, где:

- `EchoServer` принимает TCP-соединения и возвращает полученные данные обратно клиенту
- `EchoClient` подключается к серверу, отправляет сообщение и выводит ответ

В `EchoClient.java` клиент подключается к указанным `host` и `port`, отправляет текст `Netty rocks!` и ожидает ответ от сервера.

## Как это работает

1. Запустите сервер на нужном порту.
2. Запустите клиента с тем же `host` и `port`.
3. Клиент отправляет сообщение.
4. Сервер получает его и отправляет обратно.
5. Клиент печатает полученное echo-сообщение.
