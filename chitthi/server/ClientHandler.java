package chitthi.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

/**
 * @class ClientHandler
 * @extends Thread
 * * Manages the dedicated, concurrent connection for a single client on the server.
 * This class handles all I/O, user registration, and message parsing for one client.
 */
public class ClientHandler extends Thread {
    // --- Connection/IO Fields ---
    private Socket clientSocket;
    private PrintWriter send;     // Output stream to send data TO the client
    private BufferedReader receive; // Input stream to receive data FROM the client
    private String username;      // The unique identifier for this client

    // --- Shared Data Structure ---
    // Reference to the global, thread-safe map managed by the ChatServer.
    // Used for registering this client and looking up recipients for PMs.
    private Map<String, ClientHandler> activeClients;

    /**
     * Constructor for the ClientHandler. Initializes streams and stores references.
     * * @param socket The specific Socket connection established with the client.
     * @param activeclients The global map of active users/handlers.
     */
    public ClientHandler(Socket socket, Map<String, ClientHandler> activeclients) {
        this.clientSocket = socket;
        this.activeClients = activeclients;

        try {
            // Initialize streams: PrintWriter for writing (auto-flush set to true)
            this.send = new PrintWriter(socket.getOutputStream(), true);
            // Initialize streams: BufferedReader for reading text lines
            this.receive = new BufferedReader(new InputStreamReader(socket.getInputStream())); 
        } catch(IOException io) {
            System.err.println("Error initializing streams for new client: " + io.getMessage());
            // Attempt to close the socket if stream initialization fails
            try {
                this.clientSocket.close();
            } catch (IOException ioException) {
                System.err.println("Error closing socket for new client: " + ioException.getMessage());
            }
        }
    }

    /**
     * Gets the unique username of this connected client.
     * * @return The client's username (String).
     */
    public String getUsername() {
        return username;
    }

    /**
     * The main execution loop for the thread. 
     * Handles client registration, message parsing, and routing.
     */
    @Override
    public void run() {
        try {
            // STEP 1: Registration - The first line read must be the username
            this.username = receive.readLine();

            if(this.username == null || this.username.trim().isEmpty()) {
                return; // Stop processing if no valid username is provided
            }

            // STEP 2: Add this handler instance to the server's global active list.
            // This allows other handlers to find and message this client.
            activeClients.put(this.username, this);
            System.out.println("Server is registered by: " + this.username);
            
            // Note: Add ChatServer.broadcastMessage("SERVER", username + " joined...") here if needed.

            String message;
            
            // STEP 3: Main Message Processing Loop
            while ((message = receive.readLine()) != null) {
                if(message.equals("/exit")) {
                    break; // Exit loop to initiate cleanup
                } else {
                    // PRIVATE MESSAGE PROTOCOL: [recipient] [content]
                    String[] parts = message.split(" ", 2);
                    if (parts.length >= 2) {
                        String recipient = parts[0];
                        String conent = parts[1];
                        
                        // Delegate the routing responsibility to the ChatServer
                        ChatServer.sendPrivateMessage(this.username, recipient, conent);
                    } else {
                        sendMessage("ERROR: Private message format is [recipient] [message]");
                    }
                }
            }

        } catch (IOException e) {
            // This exception often occurs when the client closes the connection unexpectedly
            System.out.println("Client " + this.username + " disconnected unexpectedly.");
        } finally {
            // STEP 4: Ensure all resources are closed regardless of exit reason
            cleanup();
        }
    }

    /**
     * Removes the client from the active list and closes all streams/sockets.
     */
    private void cleanup() {
        try{
            // Remove the user from the global map
            if (this.username != null) {
                activeClients.remove(this.username);
                System.out.println("User removed: " + this.username);
                return;
            }
            // Close the resources
            if (send != null) send.close();
            if (receive != null) receive.close();
            if (clientSocket != null) clientSocket.close();

        } catch (IOException e) {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
    }

    /**
     * Writes a message string directly to the client's output stream.
     * * @param message The string to be sent to the client.
     */
    public void sendMessage(String message) {
        if(send != null) {
            send.println(message);
        }
    }
}