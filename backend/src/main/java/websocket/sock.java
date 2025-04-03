package websocket;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class sock {
    private static final int PORT = 8081;
    private static final ConcurrentHashMap<String, Socket> orderConnections = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server fl port : " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    private static void handleClient(Socket clientSocket) {
        String orderId = null;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            orderId = in.readLine();
            if (orderId == null || orderId.trim().isEmpty()) {
                System.err.println("Invalid orderId received. Disconnecting client.");
                return;
            }
            orderConnections.put(orderId, clientSocket);
            System.out.println("Client registered for order: " + orderId);

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received for order " + orderId + ": " + inputLine);
            }
        } catch (IOException e) {
            System.err.println("Client disconnected: " + e.getMessage());
        } finally {
            if (orderId != null) {
                orderConnections.remove(orderId);
                System.out.println("Removed connection for order: " + orderId);
            }
        }
    }

}
