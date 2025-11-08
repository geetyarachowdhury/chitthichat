package chitthi.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * @class ChatClient
 * @main
 * A console-based client application for connecting to the multithreaded chat server.
 * Implements a two-thread model for non-blocking communication: one thread for sending 
 * user input and one thread for continuously listening for server messages.
 */
public class ChatClient {
    private static final String SERVER_IP = "127.0.0.1"; // Loopback address for local testing
    private static final int SERVER_PORT = 8888;         // Must match the port the ChatServer is listening on
    
    public static void main(String[] args) {
        try {
            // STEP 1: Connect to the Server
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            System.out.println("Connected to the server");
            
            // Initialize I/O streams for the socket
            // Stream to receive messages from the server
            BufferedReader serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // Stream to send messages to the server (auto-flush set to true)
            PrintWriter serverOutput = new PrintWriter(socket.getOutputStream(), true);

            Scanner sc = new Scanner(System.in);
            String username;

            // STEP 2: User Registration
            System.out.print("Enter your username: ");
            username = sc.nextLine();

            // The very first line sent to the server is used for user registration
            serverOutput.println(username);

            // STEP 3: Create and Start Listener Thread
            // This background thread runs concurrently to continuously receive messages
            Thread listenerThread = new Thread(() -> {
                try {
                    String message;
                    // Loop until the server closes the connection (readLine() returns null)
                    while((message = serverInput.readLine()) != null) {
                        System.out.println(message);
                    }
                } catch (Exception e) {
                    // Prints message if the connection is closed unexpectedly or by the server
                    System.out.println("Disconnected from server.");
                }
            });
            listenerThread.start();

            // Display commands to the user
            System.out.println("\n--- Chat Commands ---");
            System.out.println("Write you message below");
            System.out.println("To exit from the chat: /exit\n"); // NOTE: Client Handler checks for "/quit"

            // STEP 4: Sender Loop (Main Thread)
            String userInput;
            while (true) {
                userInput = sc.nextLine();
                // Send user input (message or command) to the server
                serverOutput.println(userInput);
                
                // Check locally if the user wants to quit
                if (userInput.equalsIgnoreCase("/exit")) {
                    break;
                }
            }

            // STEP 5: Cleanup Resources
            listenerThread.interrupt(); // Stops the background thread
            sc.close();                 // Closes the scanner
            socket.close();             // Closes the socket connection
            
        } catch (IOException e) {
            // Handle connection failure (e.g., server not running or connection refused)
            System.err.println("Could not connect to server: " + e.getMessage());
        }
    }
}