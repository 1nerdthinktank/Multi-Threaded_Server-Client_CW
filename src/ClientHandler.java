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
     Otherwise, the code would have to be added to the Server and Client classes, increasing the amount of coupling.
     **/

    // keep track of current connections of by making a list of clients, that we can also iterate though when we need
    // broadcast to all active clients. Static variable, to declare that it belongs to the super class only.
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    public String clientUsername;

    private final LocalDateTime currentTime = LocalDateTime.now();
    private final DateTimeFormatter formattedTime = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    private final String timestamp = currentTime.format(formattedTime);



    public ClientHandler (Socket socket) {

        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine();
            clientHandlers.add(this);
            broadcastMessage("SERVER BROADCAST: " + timestamp + clientUsername + " has entered the group chat");

            // if start of server, print first users name (controller)
            if (clientHandlers.size() == 1) {
                System.out.println("SERVER LOG " + timestamp + "- SERVER BROADCAST: " +  clientUsername +  " has entered the group chat, you are the client 'controller'");
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
                // if message to send username equals current socket username, send message as that user --> CANNOT USE DUPLICATE USERNAMES --> Add Unique Name Validation?
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

    public void removeClientHandler() {
        clientHandlers.remove(this);

        broadcastMessage("SERVER MESSAGE " + timestamp + " - " + clientUsername + " Has Unexpectedly disconnected from the the chat!" );

        if (clientHandlers.isEmpty()) {
            System.out.println("SERVER LOG " + timestamp + " - " + "All Users have disconnected from the the chat!");
        }
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
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
