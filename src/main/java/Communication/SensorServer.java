package Communication;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class SensorServer extends AbstractServer{

    private final Map<String, SensorConnectionThread> connectedSensors; //Map of client IPs and their sensor threads

    public SensorServer(int port) throws IOException {
        super(port);
        connectedSensors = new HashMap<>();
    }

    @Override
    public void startServer()  {
        super.startServer();
        try {
            while(true) { //Keep this running (almost) forever
                Socket socket = getServerSocket().accept();

                //Create and start a new thread when a new client is accepted
                SensorConnectionThread sensorClientThread = new SensorConnectionThread(this, socket);
                sensorClientThread.start();

                //Put client in table containing all clients
                connectedSensors.put(sensorClientThread.getIP(), sensorClientThread);
                System.out.println("New client put in table");
            }
        }
        catch(IOException e) {
            System.err.println("Error accepting socket\n" + e.getMessage());
        }
    }

@Override
    //Remove client from table
    public void disconnectClient(String clientIP) {
        connectedSensors.remove(clientIP);
        System.out.println("Client disconnected");
    }
}
