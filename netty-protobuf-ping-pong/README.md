# netty-protobuf-ping-pong

## English

This project is a small Netty + Protocol Buffers gateway example.

It contains:
- a TCP server built with Netty
- a TCP client for testing requests
- protobuf-based request and response messages
- Redis-backed session storage for login requests

### `GatewayServerHandler`

`GatewayServerHandler` processes incoming `GatewayProto.Request` messages and writes back a `GatewayProto.Response`.

Supported request types:
- `login` - creates a new session in Redis for the provided username and returns the generated session id
- `ping` - responds with `PONG: <text>`
- any other payload - returns `Unsupported request type`

The handler also logs each received request and closes the channel on exceptions.

## Русский

Это небольшой пример gateway-сервиса на Netty и Protocol Buffers.

В проекте есть:
- TCP-сервер на Netty
- TCP-клиент для проверки запросов
- protobuf-сообщения для запросов и ответов
- хранение сессий в Redis для login-запросов

### `GatewayServerHandler`

`GatewayServerHandler` принимает входящие сообщения `GatewayProto.Request` и отправляет обратно `GatewayProto.Response`.

Поддерживаемые типы запросов:
- `login` - создаёт новую сессию в Redis для переданного имени пользователя и возвращает `session id`
- `ping` - отвечает `PONG: <text>`
- любой другой payload - возвращает `Unsupported request type`

Обработчик также логирует каждый полученный запрос и закрывает канал при ошибках.
