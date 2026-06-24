import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public class NioServer {

    private static final class ConnectionState {
        private final StringBuilder inbound = new StringBuilder();
        private final Deque<ByteBuffer> outbound = new ArrayDeque<>();
    }

    public static void main(String[] args) throws IOException {
        try (Selector selector = Selector.open();
             ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {

            serverSocketChannel.bind(new InetSocketAddress(8083));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("NIO server started on port: " + serverSocketChannel.getLocalAddress());

            while (true) {
                selector.select();

                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    try {
                        if (!key.isValid()) {
                            System.out.println("Invalid key detected");
                            continue;
                        }

                        if (key.isAcceptable()) {
                            handleAccept(selector, serverSocketChannel);
                        }

                        if (key.isReadable()) {
                            handleRead(key);
                        }

                        if (key.isWritable()) {
                            handleWrite(key);
                        }
                    } catch (IOException e) {
                        System.out.println("Error: " + e.getMessage());
                        closeKey(key);
                    }
                }
            }
        }
    }

    private static void handleAccept(Selector selector, ServerSocketChannel serverSocketChannel) throws IOException {
        SocketChannel clientChannel = serverSocketChannel.accept();
        if (clientChannel == null) {
            return;
        }
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ, new ConnectionState());
        System.out.println("Client connected: " + clientChannel.getRemoteAddress());
    }

    private static void handleRead(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ConnectionState state = (ConnectionState) key.attachment();
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        int bytesRead = clientChannel.read(buffer);

        if (bytesRead == -1) {
            System.out.println("Client disconnected: " + clientChannel.getRemoteAddress());
            closeKey(key);
            return;
        }

        if (bytesRead == 0) {
            return;
        }

        buffer.flip();
        state.inbound.append(StandardCharsets.UTF_8.decode(buffer));

        int newlineIndex;
        while ((newlineIndex = state.inbound.indexOf("\n")) >= 0) {
            String message = state.inbound.substring(0, newlineIndex);
            state.inbound.delete(0, newlineIndex + 1);

            if (message.endsWith("\r")) {
                message = message.substring(0, message.length() - 1);
            }

            System.out.println("Received from " + clientChannel.getRemoteAddress() + ": " + message);

            if ("bye".equalsIgnoreCase(message)) {
                System.out.println("Client said bye, closing connection...");
                closeKey(key);
                return;
            }

            String response = "Server received: " + message + "\n";
            state.outbound.add(ByteBuffer.wrap(response.getBytes(StandardCharsets.UTF_8)));
            key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
        }
    }

    private static void handleWrite(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ConnectionState state = (ConnectionState) key.attachment();

        if (state == null || state.outbound.isEmpty()) {
            key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
            return;
        }

        while (!state.outbound.isEmpty()) {
            ByteBuffer buffer = state.outbound.peek();
            int written = clientChannel.write(buffer);
            System.out.println("Written " + written + " bytes to " + clientChannel.getRemoteAddress());

            if (buffer.hasRemaining()) {
                key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
                return;
            }

            state.outbound.remove();
        }

        key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
    }

    private static void closeKey(SelectionKey key) {
        try {
            key.attach(null);
            key.cancel();
            key.channel().close();
        } catch (IOException ignored) {
        }
    }
}
