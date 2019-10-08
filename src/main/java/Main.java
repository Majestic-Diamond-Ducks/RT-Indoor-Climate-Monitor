import Communication.SensorServer;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        final int PORT = 6789;

        try { //Try to create and start server
            SensorServer server = new SensorServer(PORT);
            server.startServer();
        }
        catch(IOException e)    {
            System.err.println("Error while starting server\n" + e.getMessage());
        }
    }
}
