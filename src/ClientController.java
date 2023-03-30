// 01/03/2023 Alix Corley & CW Group, University of Greenwich Advanced Programming
import java.io.*;
import java.net.Socket;
import java.lang.System;
import java.util.Scanner;

public class ClientController {

    public static void main(String[] args) throws IOException {



        Scanner scanner = new Scanner(System.in);

        String username = input(scanner, "Enter your ID or username for the group chat: ");

//            boolean isNameUnique = ClientHandler.clientHandlers.toString().contains(selectedUsername);
//
//            while (!isNameUnique) {
//            System.out.println("Please select a different name/id - Username Already in Use!");
//            String username = input(scanner, "Enter your ID or username for the group chat: ");

//            } else {
//               break;
//            }

        String ip = input(scanner, "Enter ip to bind to [localhost]: ");
        ip = ip.isBlank() ? "localhost" : ip;

        String portStr = input(scanner, "Enter port to bind to [8080]: ");
        int port = portStr.isBlank() ? 8080 : Integer.parseInt(portStr);

        System.out.println("CLIENT MESSAGE: Welcome to the group chat, " + username + "! " +
                "\nCLIENT MESSAGE: Please type a message and press 'Enter'." +
                "\nCLIENT MESSAGE: Type '\\help' for options/commands or '\\quit' to exit.");

        // Socket socket = new Socket(InetAddress.getLocalHost(), 1234);
        Socket socket = new Socket(ip, port);
        ClientSocket client = new ClientSocket(socket, username);

        // listen / send on separate processes, so they do not block each other.
        client.startMessageListener();
        client.sendLoop();
    }

    private static String input(Scanner inScanner, String prompt) {
        System.out.println(prompt);
        return inScanner.nextLine();
    }
}
