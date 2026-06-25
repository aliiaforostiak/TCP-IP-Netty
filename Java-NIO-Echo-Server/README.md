# Java NIO Echo Server

## English

This is a small Java NIO learning project that demonstrates a non-blocking TCP server and client.

### What the server does

- listens on port `8083`;
- accepts client connections;
- reads messages as lines terminated by `\n`;
- responds in the form `Server received: <message>`;
- closes the connection when the client sends `bye`.

### What the client does

- connects to `localhost:8083`;
- reads user input from the console;
- sends each entered line as a separate message ending with `\n`;
- reads server responses line by line;
- stops after the user enters `bye`.

### Protocol

The project uses line-based framing:

- one message equals one line;
- each message ends with `\n`;
- both client and server accumulate incoming bytes and process only complete lines.

This is necessary because TCP is a byte stream, not a message stream.

### Tests

The project includes unit tests for the client-side framing logic:

- appending `\n` to outbound messages;
- extracting multiple lines from one incoming buffer;
- handling `\r\n`.

### Run

Build and test:

```bash
mvn test
```

Server:

```bash
mvn -q exec:java -Dexec.mainClass=NioServer
```

Client:

```bash
mvn -q exec:java -Dexec.mainClass=NioClient
```

If the `exec` plugin is not configured, you can run both classes directly from your IDE.

## Русский

Это небольшой учебный проект на Java NIO, который показывает работу неблокирующего TCP-клиента и TCP-сервера.

### Что делает сервер

- слушает порт `8083`;
- принимает подключения от клиентов;
- читает сообщения строками, где конец сообщения обозначается символом `\n`;
- отвечает в формате `Server received: <message>`;
- закрывает соединение, если клиент отправляет `bye`.

### Что делает клиент

- подключается к `localhost:8083`;
- читает ввод пользователя из консоли;
- отправляет каждую введённую строку как отдельное сообщение с завершающим `\n`;
- читает ответы сервера тоже построчно;
- завершает работу после ввода `bye`.

### Как устроен протокол

Проект использует строковый framing:

- одно сообщение = одна строка;
- сообщение заканчивается `\n`;
- сервер и клиент накапливают входящие байты и разбирают только полные строки.

Такой подход нужен, потому что TCP передаёт не сообщения, а поток байтов.

### Тесты

В проекте есть unit-тесты для клиентской логики:

- добавление `\n` к исходящему сообщению;
- разбор нескольких строк из одного входящего буфера;
- обработка `\r\n`.

### Запуск

Сборка:

```bash
mvn test
```

Сервер:

```bash
mvn -q exec:java -Dexec.mainClass=NioServer
```

Клиент:

```bash
mvn -q exec:java -Dexec.mainClass=NioClient
```

Если плагин `exec` не подключён, сервер и клиент можно запускать из IDE напрямую.

