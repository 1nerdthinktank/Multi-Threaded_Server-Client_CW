import org.junit.jupiter.api.Test;
import java.io.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServerClientHandlerTest {
    // [set up]
    private boolean isCoordinator = false;

    @Test
    void makeCoordinatorTest() throws IOException {
        makeCoordinator();
        assertTrue(isCoordinator);
    }

    private void makeCoordinator() {
        isCoordinator = true;
    }
}