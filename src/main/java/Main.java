import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {
            SensorServer server = new SensorServer(6789);
            server.startServer();
        }
        catch(IOException e)    {
            System.err.println("Error while starting server\n" + e.getMessage());
        }
    }
}
