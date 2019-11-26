import Communication.APIIncomingServer;
import Communication.APIOutgoingServer;
import Communication.ControllerServer;
import Communication.SensorServer;

import java.io.IOException;

public class Main {
    // LIST OF PROJECT TODOS //

    //TODO write controller client code

    // LIST OF PROJECT TODOS //

    public static void main(String[] args) {

        final int SENSOR_PORT = 6789;
        final int CONTROLLER_PORT = 6790;
        final int API_OUT_PORT = 6969;
        final int API_IN_PORT = 6970;

        //
        // Start sensor server
        //
        new Thread(() -> {
            SensorServer sensorServer;
            try {
                sensorServer = new SensorServer(SENSOR_PORT);
                sensorServer.startServer();
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
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }).start();

        //
        // Start API outgoing server
        //
        new Thread(() -> {
            APIOutgoingServer apiOutgoingServer;
            try {
                apiOutgoingServer = new APIOutgoingServer(API_OUT_PORT);
                apiOutgoingServer.startServer();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }).start();

        //
        // Start API outgoing server
        //
        new Thread(() -> {
            APIIncomingServer apiIncomingServer;
            try {
                apiIncomingServer = new APIIncomingServer(API_IN_PORT);
                apiIncomingServer.startServer();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
