import Communication.SensorServer;
import Data.ValueStorageBox;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        final int PORT = 6789;

        try { //Try to create and start server
            ValueStorageBox valueStorageBox = new ValueStorageBox(); //TODO find an efficient way to refference the storage box
            SensorServer server = new SensorServer(PORT, valueStorageBox);
            server.startServer();
        }
        catch(IOException e)    {
            System.err.println("Error while starting server\n" + e.getMessage());
        }
    }
}
