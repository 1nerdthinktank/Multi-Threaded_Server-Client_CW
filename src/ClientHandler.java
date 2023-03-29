// 01/03/2023 Alix Corley & CW Group, University of Greenwich Advanced Programming
import java.io.*;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClientHandler implements Runnable {
    /**
    ClientHandler:
     Abstract Class to implement code for sockets and reader/scanner IO, Implements run() method, for multiple threads.
     **/

    // keep track of current connections of by making a list of clients, that we can also iterate though when we need
    // broadcast to all active clients.
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    public String clientUsername;

    private final LocalDateTime currentTime = LocalDateTime.now();
    private final DateTimeFormatter formattedTime = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    private final String timestamp = currentTime.format(formattedTime);

    public boolean isCoordinator = false; // initialises as false, only set to true if start of server or previous coordinator disconnects



    public ClientHandler (Socket socket) {

        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine();
            clientHandlers.add(this);
            broadcastMessage("SERVER BROADCAST: " + timestamp + clientUsername + " has entered the group chat");

            // if start of server, informs user they are coordinator
            if (clientHandlers.size() == 1) {
                broadcastMessage("SERVER BROADCAST: " + timestamp + clientUsername +
                        " Is the first to join the chat, and has the status of coordinator");
                        isCoordinator = true;
            }

        } catch (IOException e) {

            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }


    @Override
    public void run() {

        String messageFromClient;

        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                if(messageFromClient == null) throw new IOException(); // Added to check if message is not null
                broadcastMessage(messageFromClient);

            } catch (IOException e) {

                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }
        public void broadcastMessage(String messageToSend) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                // CANNOT USE DUPLICATE USERNAMES --> Add Unique Name Validation on start?
                if (!clientHandler.clientUsername.equals(clientUsername)) {
                    clientHandler.bufferedWriter.write("GROUP CHAT " + timestamp + " - " + messageToSend);
                    System.out.println(("SERVER LOG " + timestamp + " - " + messageToSend));
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {

                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }
// TODO: send message to socket username
//    public void directMessage(String messageToSend, String recipient) {
//        for (ClientHandler clientHandler : clientHandlers) {
//            try {
//                // CANNOT USE DUPLICATE USERNAMES --> Add Unique Name Validation?
//                if (recipient.equals(clientUsername)) {
//                    clientHandler.bufferedWriter.write("PRIVATE CHAT " + timestamp + " - " + messageToSend);
//                    System.out.println(("SERVER LOG PRIVATE CHAT " + timestamp + " - " + messageToSend));
//                    clientHandler.bufferedWriter.newLine();
//                    clientHandler.bufferedWriter.flush();
//                }
//            } catch (IOException e) {
//                removeClientHandler();
//                closeEverything(socket, bufferedReader, bufferedWriter);
//            }
//        }
//    }

    public void removeClientHandler() {

        clientHandlers.remove(this);
        broadcastMessage("SERVER MESSAGE - " + clientUsername + " Has Unexpectedly disconnected from the the chat!" );

        // TODO: Pass on coordinator status to another client at random
        if (isCoordinator) {
            broadcastMessage("SERVER MESSAGE - " + clientUsername + " Is no longer the coordinator, a new one will be chosen. " );
            // FIXME: clientHandlers.get(0).isCoordinator.isTrue --> set one of the other clients isCoordinator to true
        }

        if (clientHandlers.isEmpty()) {
            System.out.println("SERVER LOG " + timestamp + " - " + "All Users have disconnected from the the chat!");
        }
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler(); // leave this line or things break
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
