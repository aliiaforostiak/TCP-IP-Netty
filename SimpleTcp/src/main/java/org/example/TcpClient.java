package org.example;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TcpClient {
    public static void main(String[] args) throws IOException {
        try(Socket socket = new Socket("localhost", 8082)){
            System.out.println("Connected to server");

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            out.println("Hello from client");

            String response = in.readLine();

            System.out.println("Server says: " + response);
        }
    }
}
