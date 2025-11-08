# 💬 Java Console Chat Application
A lightweight, multithreaded chat application built in Java using standard Sockets and I/O. This project demonstrates core networking principles, concurrent execution, and client-server architecture, primarily focusing on one-to-one (Private) communication.

## ✨ Features
Multithreading: The server uses dedicated ClientHandler threads to manage I/O for each client concurrently, ensuring the application remains responsive.

  * **One-to-One Messaging (PMs: Private messages):** Implements a robust routing system ([recipient] [message]) using a ConcurrentHashMap for efficient, targeted message delivery.

  * **Simple Console Interface:** Uses command-line I/O for easy setup and testing without a complex GUI.

  * **Clean Architecture:** Server logic is strictly separated: ChatServer handles connections and routing, while ClientHandler manages individual client I/O.

## 🛠️ Project Structure
The code is organized into two main packages, reflecting the server and client components:

```text
chitthichat/
└── chitthi/
    ├── client/
    │   └── ChatClient.java   (The user interface)
    └── server/
        ├── ChatServer.java   (Main server application)
        └── ClientHandler.java (Threaded class for each connection)
```

## 🚀 Getting Started
### Prerequisites

  * Java Development Kit (JDK 8 or later).

### Setup and Compilation
   * **Save Files:** Save the server files (ChatServer.java, ClientHandler.java) in the chitthi/server directory, and the client file (ChatClient.java) in the chitthi/client directory.

  * **Navigate:** Open your terminal and navigate to the root directory of your project (chatapp).

  * **Compile:** Compile both server and client packages:


### Compile server classes
```bash
javac chitthi/server/*.java
```

### Compile client classes
```bash
javac chitthi/client/*.java
```

## ▶️ Running the Application
The application must be run in two stages: Server first, then one or more Clients.

### 1. Start the Server

Open your **first terminal window** and run the main server class using its fully qualified name:

```bash
java chitthi.server.ChatServer
```
Expected Output: ***🚀 Server started. Listening on port 8888*** (Keep this window open and active.)

### 2. Start the Client(s)

Open a **new terminal window** for each client you want to connect.

```bash
java chitthi.client.ChatClient
```
The client will prompt you to enter a username. This name is used by the server as the unique key for message routing.

## 💻 Commands
All messaging and commands are handled via the client console:

| Command | Purpose | Example |
|---|---|---|
| [Any Text] | Sends the message as results in an error, recipient is mandatory | Hello everyone |
| [user] [message] | Private Message: Sends a message only to the specified recipient. The part is recipient and the rest is the message | Taylor Meet at 5 |
| /exit | Gracefully disconnects the client from the server and closes the connection. | /quit |

		
