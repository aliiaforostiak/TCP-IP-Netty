package tcp;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Server {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8082)) {
            System.out.println("Server started on port: " + serverSocket.getLocalPort());

            try (Socket clientSocket = serverSocket.accept();
                 InputStream in = clientSocket.getInputStream()) {

                System.out.println("Client connected");

                while (true) {

                    byte[] lengthBytes = in.readNBytes(4);
                    if (lengthBytes.length != 4) {
                        throw new EOFException();
                    }

                    int length = Protocol.decodeLength(lengthBytes);
                    byte[] messageBytes = in.readNBytes(length);

                    String message = new String(messageBytes, StandardCharsets.UTF_8);
                    System.out.println("Received length bytes: " + Arrays.toString(lengthBytes));
                    System.out.println("Received message bytes: " + Arrays.toString(messageBytes));
                    System.out.println("Bytes as a message: " + message);

                    if ("bye".equalsIgnoreCase(message)) {
                        break;
                    }

                }
            }
        } catch (IOException e) {
            System.out.println("Got exception: " + e.getMessage());
        }
    }

}
