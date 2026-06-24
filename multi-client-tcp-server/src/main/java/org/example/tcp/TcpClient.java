package org.example.tcp;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class TcpClient {
    public static void main(String[] args) throws IOException {
        try (Socket socket = new Socket("localhost", 8082);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            System.out.println("Connected to server");

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.print("You: ");
                String message = scanner.nextLine();

                out.println(message);

                String response = in.readLine();
                System.out.println(response);

                if ("bye".equalsIgnoreCase(message)) {
                    break;
                }
            }
        }
    }
}
