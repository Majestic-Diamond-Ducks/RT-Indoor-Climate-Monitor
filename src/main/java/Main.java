import Communication.APIServer;
import Communication.ControllerServer;
import Communication.SensorServer;

import java.io.IOException;

public class Main {
    // LIST OF PROJECT TODOS //

    //TODO implement interface to server classes (possibly abstract?)
    //TODO add server as listeners in clients using interface
    //TODO add overarching timer for client thread timeout/removal
    //TODO write controller client code
    //TODO change mutex to use read/write semaphores instead

    // LIST OF PROJECT TODOS //

    public static void main(String[] args) {

        final int SENSOR_PORT = 6789;
        final int CONTROLLER_PORT = 6790;
        final int API_PORT = 6969;

        //
        // Start sensor server
        //
        new Thread(() -> {
            SensorServer sensorServer;
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
            ControllerServer controllerServer;
            try {
                controllerServer = new ControllerServer(CONTROLLER_PORT);
                controllerServer.startServer();
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
            APIServer apiServer;
            try {
                apiServer = new APIServer(API_PORT);
                apiServer.startServer();
                System.out.println("API server started");
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
