import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class ServerHandler {

    private ServerHandler(ServerSocket serverSocket) {
    }

    public static void startServer() throws IOException {

        // initiate socket on port number, construct Server object
        // Singleton serverSocket = Singleton.getInstance();
        ServerSocket serverSocket = new ServerSocket(9000);

        //ServerHandler serverHandler = new ServerHandler(serverSocket);

        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formattedTime = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String timestamp = currentTime.format(formattedTime);


        try {
            System.out.println("SERVER LOG " + timestamp + " - SERVER STARTED");

            // recursively checking if a client socket is trying to connect on host/port
            while (!serverSocket.isClosed()) {
                // Accept connection
                Socket socket = serverSocket.accept();

                // Print information to server terminal/CLI
                System.out.println("SERVER LOG " + timestamp + " - A new user is connecting to the group chat!");

                // Construct a new object instance for the ClientHandler class, where "Runnable" interface is
                // implemented and the "run()" method is "Handled" for each socket object that connects
                ClientHandler clientHandler = new ClientHandler(socket);

                // A thread is spawned for each instance of clientHandler object
                Thread thread = new Thread(clientHandler);
                // ".start()" MUST be run for each thread
                thread.start();
            }

        } catch (IOException e) {
            System.out.println("SERVER LOG" + timestamp + " - SERVER MESSAGE: All Users Disconnected From Chat");
            closeServerSocket(serverSocket);
        }
    }

    public static void closeServerSocket(ServerSocket serverSocket) {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
