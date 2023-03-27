// 01/03/2023 Alix Corley & CW Group, University of Greenwich Advanced Programming
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

//        Multi-Threaded Server-Client Chat App Using Java Sockets
//
//        TODO: Basic group chat message and direct message functionality
//        • Group formation, connection, and communication [10 marks]
//        o A group should be correctly formed connecting with all members where all
//        members can communicate without any error.
//
//        TODO: Server Log with Timestamps (Save in Terminal Output or Log file)
//        • Group state maintenance [10 marks]
//        o The state of the group must be maintained correctly. This includes recording of
//        the messages exchanged among members of the group with timestamps.
//
//        TODO: Tag first user to connect as "Coordinator" if this user disconnects, this "role" should be transferred to a new user
//        • Coordinator selection [10 marks]
//        o A correct implementation to automatically choose the coordinator even when the
//        existing coordinator is disrupted/disconnected abnormally.
//
//        TODO: Refactor with design patterns -
//         - Server and Coordinator -> "Singleton Pattern"
//         - Socket Constructor -> "SocketBuilder Pattern"
//         - Get/Set Array of clientHandlers -> "Observer Pattern"
//         -
//        • Use of design patterns [10 marks]
//        o Adequate use of various design patterns in the implementation of the project.
//
//        • Fault tolerance [10 marks]
//        o Adequate strategy implementation for the fault tolerance, when a member
//        terminates abnormally or when a coordinator terminates abnormally.
//
//          TODO: JUnit Testing
//        • JUnit based testing of the application [10 marks]
//        o Desired testing for implementation of all of the main requirements.
//
//        • Use of component-based development [10 marks]
//        o Adequate design and development of components in the implementation.

public class Server {
    /**
     Multi-Threaded Server: Controller for Clients
     **/
    private final ServerSocket serverSocket;

    // TODO: Add Singleton Design Pattern (We only need one Server at a time)
    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    // Start
    public void startServer() {

        try {
            System.out.println("SERVER LOG - SERVER STARTED");
            // A recursive while loop, constantly checking if a client socket is trying to connect on host/port
            while (!serverSocket.isClosed()) {
                // Accept connection
                Socket socket = serverSocket.accept();

                // Print information to server terminal/CLI TODO: Add timestamps to messages
                System.out.println("SERVER LOG - A new user is connecting to the group chat!");

                // Construct a new object instance for the ClientHandler class, where "Runnable" interface is
                // implemented and the "run()" method is "Handled" for each socket object that connects
                ClientHandler clientHandler = new ClientHandler(socket);

                // A thread is spawned for each instance of clientHandler object
                Thread thread = new Thread(clientHandler);
                // ".start()" MUST be run for each thread
                thread.start();
            }




        } catch (IOException e) {
            closeServerSocket();
        }
    }

    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // initiate socket on port number, construct Server object and then start server method
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9000);
        Server server = new Server(serverSocket);
        server.startServer();
        //while loop.. else
        System.out.println("SERVER LOG - SERVER MESSAGE: All Users Disconnected From Chat");
    }

}
