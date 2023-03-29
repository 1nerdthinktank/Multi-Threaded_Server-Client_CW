import java.io.IOException;
import java.net.ServerSocket;

class Singleton {
    // Static variable reference of single_instance
    // of type Singleton
    private static Singleton single_instance = null;


    // Constructor
    // restricted to this class itself
    private Singleton() throws IOException {
        ServerSocket serverSocket = new ServerSocket(9000);
    }

    // Static method to create instance of Singleton class
    public static synchronized Singleton getInstance() throws IOException {
        if (single_instance == null)
            single_instance = new Singleton();

        return single_instance;
    }
}
