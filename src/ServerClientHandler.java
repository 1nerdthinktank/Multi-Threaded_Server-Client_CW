// 01/03/2023 Alix Corley & CW Group, University of Greenwich Advanced Programming
import java.io.*;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerClientHandler implements Runnable {
    /**
     * ClientHandler:
     * Abstract Class to implement code for sockets and reader/scanner IO, Implements run() method, for multiple threads.
     **/

    private final ArrayList<ServerClientHandler> peers;
    private final Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public String clientUsername;
    private boolean isCoordinator = false;

    private static final DateTimeFormatter formattedTime = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");


    public ServerClientHandler(Socket socket, ArrayList<ServerClientHandler> peers) {
        this.socket = socket;
        this.peers = peers;

        try {
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            closeEverything();
        }
        this.peers.add(this);
    }

    private void initialise() throws IOException {
        this.clientUsername = bufferedReader.readLine();

        broadcastMessage("SERVER MESSAGE: " + clientUsername + " has entered the group chat");

        // if start of server, checks for coordinator
        // cannot initialise peers.size() with less than 2
        // let (peers.get(0)) be coordinator, if (current username != first username in list of users -> do nothing
        if (!isCoordinator && peers.size() == 2 && (!this.clientUsername.equals(peers.get(0).clientUsername))) {

            broadcastMessage("SERVER MESSAGE: " + peers.get(0).clientUsername +
                             " was the first to join the chat, and is now the coordinator!");

            // isCoordinator = true
            peers.get(0).makeCoordinator();

        } else if (!isCoordinator && peers.size() >= 2) {
            sendToClient("SERVER MESSAGE: " + peers.get(0).clientUsername +
                    " is currently the coordinator.");

        }
    }

    @Override
    public void run() {
        try {
            initialise();

            String messageFromClient;

            while (socket.isConnected()) {
                messageFromClient = bufferedReader.readLine();

                if (messageFromClient == null) {
                    throw new IOException();
                }

                handleMessage(messageFromClient);
            }
        } catch (IOException e) {
            closeEverything();
        }
    }

    // Message Logic
    private void handleMessage(String message) throws IOException {
        if (message.startsWith("\\quit")) {
            quit();
        } else if (message.startsWith("\\help")) {
            sendHelp();
        } else if (message.startsWith("\\users")) {
            sendUserList();
        } else if (message.startsWith("\\dm")) {
            SendDM(message);
        } else {
            broadcastMessage("GROUP CHAT " + timeStamp() + ": " + clientUsername + ": " + message);
        }
    }


    // Send to all client terminals
    private void broadcastMessage(String messageToSend) throws IOException {

        log(messageToSend);

        for (ServerClientHandler clientHandler : peers) {
            if (!clientHandler.clientUsername.equals(clientUsername)) {
                clientHandler.sendNoLogs(messageToSend);
            }
        }
    }

    public void sendNoLogs(String message) throws IOException {
        bufferedWriter.write(message);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    // Send to one client terminal
    public void sendToClient(String message) throws IOException {
        bufferedWriter.write(message);
        bufferedWriter.newLine();
        bufferedWriter.flush();
        log(message);
    }

    // Send to server terminal
    public void log(String message) {
        System.out.println(timeStamp() + ": " + message);
    }

    private String timeStamp() {
        LocalDateTime currentTime = LocalDateTime.now();
        return currentTime.format(formattedTime);
    }

    // Coordinator Logic
    public void makeCoordinator() throws IOException {
        isCoordinator = true;
        broadcastMessage(clientUsername + " is the new coordinator");
    }

    public void removeCoordinator() throws IOException {
        isCoordinator = false;
    }

    // Handling Quit Exceptions
    private void removeClientHandler() throws IOException {

        if ((isCoordinator) && (peers.size() >= 1)) {
            broadcastMessage("SERVER MESSAGE: " + peers.get(0).clientUsername + " is no longer the coordinator, a new one will be chosen. ");
            peers.get(0).removeCoordinator();
            peers.get(1).makeCoordinator();
            peers.remove(this);

        } else if (peers.isEmpty()) {
            log("All Users have disconnected from the the chat!");

        } else {
        broadcastMessage("SERVER MESSAGE: " + clientUsername + " has suddenly disconnected from the the chat!");
        peers.remove(this);
        }
    }


    // Menu Logic
    private void quit() throws IOException {

        if ((isCoordinator) && (peers.size() >= 1)) {
            sendToClient("Connection Terminated");

            broadcastMessage("SERVER MESSAGE: " + clientUsername + " has quit the chat!");
            broadcastMessage("SERVER MESSAGE: " + peers.get(0).clientUsername + " is no longer the coordinator, a new one will be chosen. ");
            peers.get(0).removeCoordinator();
            peers.get(1).makeCoordinator();
            peers.remove(this);

            if (socket != null) {
                socket.close();
            }

        } else if (peers.isEmpty()) {
            log("All Users have disconnected from the the chat!");

        } else {
            broadcastMessage("SERVER MESSAGE: " + clientUsername + " has quit the chat!");
            sendToClient("Connection Terminated");
            peers.remove(this);

        }
    }

    private void sendHelp() throws IOException {
        sendToClient("""
                DON'T PANIC, grab a towel, the answer is 42.
                                                 
                Type the commend in order to select it, making sure you use a backslash '\\'.
                                                 
                \\help --> Displays a very helpful menu
                \\users --> Displays all current usernames
                \\dm <user> <message> --> Sends direct message to user
                \\quit --> Quits chat server \n""");
    }

    private void sendUserList() throws IOException {
        StringBuilder sb = new StringBuilder();
        for (ServerClientHandler client : peers) {
            sb.append(client.clientUsername);
            sb.append("\n");
        }
        sendToClient(sb.toString());
    }

    private static final Pattern dmPattern = Pattern.compile("\\\\dm (\\S*) (.*)");

    private void SendDM(String dm) throws IOException {
        Matcher matcher = dmPattern.matcher(dm);

        if (!matcher.find()) {
            throw new IOException();
        }

        String recipient = matcher.group(1);
        String message = matcher.group(2);

        for (ServerClientHandler clientHandler : peers) {
            if (clientHandler.clientUsername.equals(recipient)) {
                clientHandler.sendToClient("PRIVATE CHAT " + timeStamp() + " - From - " + clientUsername + "\nTo - " + recipient + ": " + message);
                break;
            }
        }
    }

    private void closeEverything() {
        try {
            removeClientHandler();
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ignored) {
        }
    }
}
