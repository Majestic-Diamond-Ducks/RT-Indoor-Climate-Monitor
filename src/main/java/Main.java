import Communication.SensorServer;
import Data.ValueStorageBox;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        final int PORT = 6789;

        new Thread(() -> {
            SensorServer server = null;
            try {
                server = new SensorServer(PORT);
                server.startServer();
                System.out.println("Sensor server started");
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
