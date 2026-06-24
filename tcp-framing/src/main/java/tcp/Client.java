package tcp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws IOException {
        try (Socket socket = new Socket("localhost", 8082);
             OutputStream out = socket.getOutputStream()) {

            Scanner scanner = new Scanner(System.in);
            System.out.println("Connected to server: " + socket.getRemoteSocketAddress());

            while (true) {

                System.out.print("You: ");
                String message = scanner.nextLine();

                byte[] packet = Protocol.encodeLength(message);
                out.write(packet);
                out.flush();

                if ("bye".equalsIgnoreCase(message)) {
                    break;
                }
            }
        }
    }
}
