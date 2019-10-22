import Communication.ControllerServer;
import Communication.SensorServer;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        final int SENSOR_PORT = 6789;
        final int CONTROLLER_PORT = 6790;

        //
        // Start sensor server
        //
        new Thread(() -> {
            SensorServer sensorServer = null;
            try {
                sensorServer = new SensorServer(SENSOR_PORT);
                sensorServer.startServer();
                System.out.println("Sensor server started");
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }).start();

        //
        // Start controller server
        //
        new Thread(() -> {
            ControllerServer controllerServer = null;
            try {
                controllerServer = new ControllerServer(CONTROLLER_PORT);
                controllerServer.startServer();
                System.out.println("Sensor server started");
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }).start();

    }
}
