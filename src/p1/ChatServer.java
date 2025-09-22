package p1;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class ChatServer {
	
    // List to keep track of all client output streams
    private static Set<ObjectOutputStream> clientWriters = new HashSet<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(12345);
            System.out.println("Server started. Waiting for clients...");

            while (true) {
                // Accept a new client
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket);

                // Create a new thread for this client
                ClientHandler handler = new ClientHandler(socket);
                handler.start();
            }
        } catch (IOException e) {
            System.out.println("Server Error: " + e.getMessage());
        }
    }

    // Broadcast message to all clients
    public static synchronized void broadcast(String message) {
        for (ObjectOutputStream writer : clientWriters) {
            try {
                writer.writeUTF(message);
                writer.flush();
            } catch (IOException e) {
                System.out.println("Broadcast error: " + e.getMessage());
            }
        }
    }

    // Inner class to handle each client
    static class ClientHandler extends Thread {
        private Socket socket;
        private ObjectInputStream in;
        private ObjectOutputStream out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());

                synchronized (clientWriters) {
                    clientWriters.add(out);
                }

                String message;
                while ((message = in.readUTF()) != null) {
                    System.out.println("Received: " + message);
                    ChatServer.broadcast(message);
                }
            } catch (IOException e) {
                System.out.println("Client disconnected: " + socket);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                synchronized (clientWriters) {
                    clientWriters.remove(out);
                }
            }
        }
    }
}
