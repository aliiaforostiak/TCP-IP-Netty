package org.example.tcp;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class MultiClientServer {

    public static void main(String[] args) throws IOException {

        try (ServerSocket serverSocket = new ServerSocket(8082)) {

            System.out.println("Server started on 8082");

            while (true) {

                Socket clientSocket = serverSocket.accept();
                System.out.println("Client " + clientSocket.getRemoteSocketAddress() + " connected");
                Thread clientThread = new Thread(() -> handleClient(clientSocket));
                clientThread.start();
            }
        }
    }

    private static void handleClient(Socket clientSocket) {

        try (Socket socket = clientSocket;
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String message;
            while ((message = in.readLine()) != null && !"bye".equalsIgnoreCase(message)) {
                System.out.println("Message form " + socket.getRemoteSocketAddress() + ": " + message);
                out.println("Server received: " + message);
            }

            System.out.println("Client " + socket.getRemoteSocketAddress() + " disconnected");
        } catch (IOException e) {
            System.out.println("Client error: " + e.getMessage());
        }

    }

}
