// 01/03/2023 Alix Corley & CW Group, University of Greenwich Advanced Programming
import java.io.*;
import java.net.Socket;
import java.lang.System;
import java.util.Scanner;

public class ClientController {

    public static void main(String[] args) throws IOException {

        // unique username validation
//        String username = "";
//        try {
//            Scanner scanner = new Scanner(System.in);
//            System.out.println("Enter your ID or username for the group chat: ");
//            String selectedUsername = scanner.nextLine();
//            boolean isNameUnique = ClientHandler.clientHandlers.toString().contains(selectedUsername);
//
//            if (isNameUnique) {
//                username = selectedUsername;
//            }
//        } catch (IllegalArgumentException e) {
//            System.out.println("Please select a different name/id - Username Already in Use!");
//        }
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your ID or username for the group chat: ");
        String username = scanner.nextLine();


        System.out.println("SERVER MESSAGE: Welcome to the group chat, " + username + "! " +
                "\nSERVER MESSAGE: Please type a message and press 'Enter'." +
                "\nSERVER MESSAGE: Type '\\help' for options/commands or '\\quit' to exit.");

        // Socket socket = new Socket(InetAddress.getLocalHost(), 1234);
        Socket socket = new Socket("localhost", 9000);
        ClientSocket client = new ClientSocket(socket, username);

        // listen / send on separate processes, so they do not block each other.
        client.listenForMessage();
        client.sendMessage();
    }
}
