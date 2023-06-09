import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ServerSocket {
    private final ArrayList<ServerClientHandler> handlers = new ArrayList<>();
    private java.net.ServerSocket serverSocket;

    public void startServer() {

        try {
            // initiate socket on port number, construct Server object
            serverSocket = new java.net.ServerSocket(8080);

            Log("SERVER STARTED");

            // Recursively checking if a client socket is trying to connect on host/port
            while (!serverSocket.isClosed()) {

                // Accept connection
                Socket socket = serverSocket.accept();

                // Print information to server terminal/CLI
                Log("A new user is connecting to the group chat!");

                // Construct a new object for the ClientHandler class, where "Runnable" interface is
                // implemented and the "run()" method is "Handled" for each socket object that connects
                ServerClientHandler clientHandler = new ServerClientHandler(socket, handlers);

                // A thread is spawned for each new instance
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            Log("Connection Error: Server Port (8080) is not available. ");
        } finally {
            closeServerSocket();
        }
    }

    private void Log(String message) {
        System.out.println(timeStamp() + ": " + message);
    }

    private static final DateTimeFormatter formattedTime = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private String timeStamp() {
        LocalDateTime currentTime = LocalDateTime.now();
        return currentTime.format(formattedTime);
    }

    private void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException ignored) {

        }
    }
}
