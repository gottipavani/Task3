package p1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ChatClient {
    public static void main(String[] args) {
        try {
            // Connect to server
            Socket socket = new Socket("localhost", 12345);
            System.out.println("Connected to chat server");

            // Streams for communication
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            // Thread to read messages from server
            Thread readerThread = new Thread(() -> {
                try {
                    while (true) {
                        String message = in.readUTF();
                        System.out.println(">> " + message);
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from server.");
                }
            });
            readerThread.start();

            // Main thread sends messages
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String msg = userInput.readLine();
                out.writeUTF(msg);
                out.flush();
            }
        } catch (IOException e) {
            System.out.println("Client Error: " + e.getMessage());
        }
    }
}
