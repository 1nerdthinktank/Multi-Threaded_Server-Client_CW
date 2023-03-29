import java.io.IOException;

// 01/03/2023 Alix Corley & CW Group, University of Greenwich Advanced Programming
//        Multi-Threaded Server-Client Chat App Using Java Sockets
//
//        TODO: Basic group chat message and direct message functionality
//        DONE: Basic Group Chat
//        FIXME: Direct Message
//        FIXME: Add Switch Case for command input, \q or \quit to quit, \h or \help for help menu
//        • Group formation, connection, and communication [10 marks]
//        o A group should be correctly formed connecting with all members where all
//        members can communicate without any error.
//
//        TODO: Server Log with Timestamps and log file
//        FIXME: Add log file
//        • Group state maintenance [10 marks]
//        o The state of the group must be maintained correctly. This includes recording of
//        the messages exchanged among members of the group with timestamps.
//
//        TODO: Tag first user to connect as "Coordinator" if this user disconnects, this "role" should be transferred to a new user
//        FIXME: First User connected to be set as Coordinator - User messaged after connecting
//        FIXME: Either add boolean state to each client OR have server associate one socket with Coordinator
//        • Coordinator selection [10 marks]
//        o A correct implementation to automatically choose the coordinator even when the
//        existing coordinator is disrupted/disconnected abnormally.
//
//        TODO: Refactor with design patterns -
//         - Server and Coordinator -> "Singleton Pattern"
//         - clientHandlers -> "Observer Pattern"
//        FIXME: Add Singleton design pattern to Server and Client
//        FIXME: Add Observer Pattern to report on state of group
//          
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

public class ServerController {
    /**
     Multi Threaded Server
     **/

    public static void main(String[] args) throws IOException {

        // initialise server
        ServerHandler.startServer();

    }
}
