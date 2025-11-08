package chitthi.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @class ChatServer
 * * The main application class for the multithreaded chat server.
 * This server manages client connections, maintains a global list of active users,
 * and handles message routing (specifically one-to-one messaging).
 */
public class ChatServer {
    
    /**
     * Global, thread-safe map storing all active client connections.
     * The key is the user's unique username (String), and the value is the 
     * corresponding ClientHandler thread responsible for their I/O.
     */
    private static final Map<String, ClientHandler> activeClients = new ConcurrentHashMap<>();
    private static final int PORT = 8888; // The port number the server will listen on

    /**
     * The entry point for the server application. Initializes the ServerSocket
     * and enters an infinite loop to accept new client connections.
     */
    public static void main(String[] args) {
    
        // Use try-with-resources to ensure the ServerSocket is automatically closed
        try(ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("🚀 Server started. Listening on port " + PORT);

            // Infinite loop to continuously listen for and accept new clients
            while(true) {
                // Blocks execution until a client attempts to connect
                Socket clientSocket = server.accept();
                System.out.println("New client connected from: " + clientSocket.getInetAddress());
                
                // Create a new handler thread for the newly connected client, 
                // passing it the socket and a reference to the global client map.
                ClientHandler handler = new ClientHandler(clientSocket, activeClients);
                handler.start(); // Starts the handler thread's run() method
            }
        } catch(IOException io) {
            System.err.println("Server exception: " + io.getMessage());
        }
    }

    /**
     * Handles routing for one-to-one (private) messages.
     * It looks up the recipient in the activeClients map for targeted delivery.
     * * @param sender The username of the client who sent the message.
     * @param recipient The username of the intended receiver.
     * @param message The content of the private message.
     */
    public static void sendPrivateMessage(String sender, String recipient, String message) {
        String formattedMessageToRecipient = "[Message from " + sender + "]: " + message;
        String formattedMessageToSender = "[You] to [" + recipient + "]: " + message;

        // 1. Send confirmation message back to the sender
        // We assume the sender is always in the map at this point.
        activeClients.get(sender).sendMessage(formattedMessageToSender);

        // 2. Check if the recipient is currently online (present in the map)
        if(activeClients.containsKey(recipient)) {
            // Recipient found: Retrieve their handler and deliver the message
            ClientHandler recipientHandler = activeClients.get(recipient);
            recipientHandler.sendMessage(formattedMessageToRecipient);
        } else {
            // Recipient not found: Send an error notification back to the sender
            activeClients.get(sender).sendMessage("ERROR: User " + recipient + " is not online.");
        }
    }

    // public static void broadcastMessage(String sender, String message) {
    //     String formattedString = "All from " + sender +": " + message;

    //     for(ClientHandler handler: activeClients.values()) {
    //         if(!handler.getUsername().equals(sender) && handler.getUsername() != null) {
    //             handler.sendMessage(formattedString);
    //         }
    //     }
    // }

}