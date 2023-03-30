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

    private void Log(String message) {
        System.out.println(timeStamp() + ": " + message);
    }

    private static final DateTimeFormatter formattedTime = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private String timeStamp() {
        LocalDateTime currentTime = LocalDateTime.now();
        return currentTime.format(formattedTime);
    }

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

        // if start of server, informs user they are coordinator
        if (peers.size() == 1) {
            broadcastMessage("SERVER MESSAGE: " + timeStamp() + clientUsername + " is the first to join the chat, and has the status of coordinator");
            // isCoordinator = true;
            peers.get(0).makeCoordinator();
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
            broadcastMessage(clientUsername + ": " + message);
        }
    }

    public void makeCoordinator() throws IOException {
        isCoordinator = true;
        send("You are now coordinator");
    }

    public void send(String message) throws IOException {
        bufferedWriter.write(message);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    private void broadcastMessage(String messageToSend) throws IOException {
        for (ServerClientHandler clientHandler : peers) {
            if (!clientHandler.clientUsername.equals(clientUsername)) {
                clientHandler.send("GROUP CHAT " + timeStamp() + " - " + messageToSend);
                Log(messageToSend);
            }
        }
    }

    private void removeClientHandler() throws IOException {
        broadcastMessage("SERVER MESSAGE - " + clientUsername + " Has disconnected from the the chat!");
        peers.remove(this);

        if (isCoordinator && peers.size() == 1) {
            broadcastMessage("SERVER MESSAGE - " + clientUsername + " Is no longer the coordinator, a new one will be chosen. ");
            peers.get(0).makeCoordinator();

        } else if (peers.size() == 0) {
            Log("All Users have disconnected from the the chat!");
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

    private void quit() {
        Log(clientUsername + " has decided to quit from the chat.");
        closeEverything();
    }

    private void sendHelp() throws IOException {
        send("""
                DON'T PANIC, grab a towel, and count to 42.
                                                 
                Type the commend in order to select it making sure you use a backslash '\\'.
                                                 
                \\help --> Displays a very helpful menu
                \\users --> Displays all users usernames
                \\dm <user> <message> --> sends message to user
                \\quit --> Quit Chat Server""");
    }

    private void sendUserList() throws IOException {
        StringBuilder sb = new StringBuilder();
        for (ServerClientHandler client : peers) {
            sb.append(client.clientUsername);
            sb.append("\n");
        }
        send(sb.toString());
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
                clientHandler.send("PRIVATE CHAT " + timeStamp() +" "+ clientUsername + " : " + message);
                Log("DM " + recipient + " : " + message);
                break;
            }
        }
    }
}
