// 01/03/2023 Alix Corley & CW Group, University of Greenwich Advanced Programming
import java.io.*;
import java.net.Socket;
import java.lang.System;
import java.util.Scanner;

public class ClientController {

    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);

        String username = input(scanner, "Enter your ID or username for the group chat: ");

        String ip = input(scanner, "Enter ip to bind to, or press enter to use default value [Default = localhost]: ");
        ip = ip.isBlank() ? "localhost" : ip;

        String portStr = input(scanner, "Enter port to bind to, or press enter to use default value [Default = 8080]: ");
        int port = portStr.isBlank() ? 8080 : Integer.parseInt(portStr);

        System.out.println("CLIENT MESSAGE: Welcome to the group chat, " + username + "! " +
                "\nCLIENT MESSAGE: Please type a message and press 'Enter'." +
                "\nCLIENT MESSAGE: Type '\\help' for options/commands or '\\quit' to exit.");

        Socket socket = new Socket(ip, port);
        ClientSocket client = new ClientSocket(socket, username);

        // separate threads, so they do not block each other.
        client.startMessageListener();
        client.sendLoop();
    }

    private static String input(Scanner inScanner, String prompt) {
        System.out.println(prompt);
        return inScanner.nextLine();
    }
}
