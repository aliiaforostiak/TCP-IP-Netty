import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NioClient {

    private static volatile boolean running = true;
    private static final ConcurrentLinkedQueue<String> messageQueue = new ConcurrentLinkedQueue<>();

    private static final class ClientState {
        private final StringBuilder inbound = new StringBuilder();
        private ByteBuffer outbound;
    }

    static ByteBuffer createOutboundBuffer(String message) {
        return ByteBuffer.wrap((message + "\n").getBytes(StandardCharsets.UTF_8));
    }

    static List<String> drainFramedMessages(StringBuilder inbound) {
        List<String> messages = new java.util.ArrayList<>();
        int newlineIndex;
        while ((newlineIndex = inbound.indexOf("\n")) >= 0) {
            String message = inbound.substring(0, newlineIndex);
            inbound.delete(0, newlineIndex + 1);

            if (message.endsWith("\r")) {
                message = message.substring(0, message.length() - 1);
            }

            messages.add(message);
        }
        return messages;
    }

    public static void main(String[] args) throws IOException {
        try (Selector selector = Selector.open();
             SocketChannel channel = SocketChannel.open()) {

            channel.configureBlocking(false);
            channel.connect(new InetSocketAddress("localhost", 8083));
            channel.register(selector, SelectionKey.OP_CONNECT);

            System.out.println("NIO client started");

            Thread inputThread = getInputThread(selector);
            inputThread.start();

            while (running || !messageQueue.isEmpty()) {
                selector.select(1000);

                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    try {
                        if (!key.isValid()) {
                            continue;
                        }

                        if (key.isConnectable()) {
                            handleConnect(key);
                        }

                        if (key.isWritable()) {
                            handleWrite(key);
                        }

                        if (key.isReadable()) {
                            handleRead(key);
                        }
                    } catch (IOException e) {
                        System.out.println("IO error: " + e.getMessage());
                        closeConnection(key);
                        running = false;
                    }
                }

                if (!messageQueue.isEmpty()) {
                    SelectionKey key = channel.keyFor(selector);
                    if (key != null && key.isValid() && !key.isWritable()) {
                        key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
                        selector.wakeup();
                    }
                }
            }

            System.out.println("Client stopped");
            System.exit(0);
        }
    }

    private static Thread getInputThread(Selector selector) {
        Thread inputThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (running) {
                String message = scanner.nextLine();
                messageQueue.offer(message);
                selector.wakeup();
                if ("bye".equalsIgnoreCase(message)) {
                    running = false;
                    break;
                }
            }
            scanner.close();
        });
        inputThread.setDaemon(true);
        return inputThread;
    }

    private static void handleConnect(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        if (channel.isConnectionPending()) {
            channel.finishConnect();
        }
        key.attach(new ClientState());
        System.out.println("Connected to server: " + channel.getRemoteAddress());
        key.interestOps(SelectionKey.OP_READ);
    }

    private static void handleWrite(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ClientState state = (ClientState) key.attachment();

        if (state == null) {
            key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
            return;
        }

        if (state.outbound == null) {
            String message = messageQueue.poll();
            if (message == null) {
                key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
                return;
            }
            state.outbound = createOutboundBuffer(message);
        }

        channel.write(state.outbound);

        if (!state.outbound.hasRemaining()) {
            state.outbound = null;
            key.interestOps(SelectionKey.OP_READ);
        } else {
            key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
        }
    }

    private static void handleRead(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ClientState state = (ClientState) key.attachment();
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        int bytesRead = channel.read(buffer);

        if (bytesRead == -1) {
            System.out.println("Server closed connection");
            closeConnection(key);
            running = false;
            return;
        }

        if (bytesRead > 0) {
            buffer.flip();
            state.inbound.append(StandardCharsets.UTF_8.decode(buffer));

            for (String response : drainFramedMessages(state.inbound)) {
                System.out.println();
                System.out.println("Server: " + response);
                System.out.print("You: ");
            }
        }
    }

    private static void closeConnection(SelectionKey key) {
        try {
            SocketChannel channel = (SocketChannel) key.channel();
            System.out.println("Closing connection: " + channel.getRemoteAddress());
            key.cancel();
            key.attach(null);
            channel.close();
        } catch (IOException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }
}
