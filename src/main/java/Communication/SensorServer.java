package Communication;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class SensorServer {

    private ServerSocket serverSocket;
    private final Map<String, SensorConnectionThread> connectedSensors; //Map of client IPs and their sensor threads

    public SensorServer(int port) throws IOException {
        connectedSensors = new HashMap<>();
        try {
            //Create server socket
            serverSocket = new ServerSocket(port);
        }
        catch(IOException e) {
            System.err.println("Error creating server socket\n" + e.getMessage());
        }
    }

    public void startServer()  {
        System.out.println("Server started");

        try {
            while(true) { //Keep this running (almost) forever
                Socket newClient = serverSocket.accept();

                //Create a new thread when a new client is accepted
                SensorConnectionThread sensorClientThread = new SensorConnectionThread(this, newClient);
                System.out.println("New client created");

                //Start client thread
                sensorClientThread.start();
                System.out.println("New client started");

                //Put client in table containing all clients
                connectedSensors.put(sensorClientThread.getClientIP(), sensorClientThread);
                System.out.println("New client put in table");
            }
        }
        catch(IOException e) {
            System.err.println("Error accepting socket\n" + e.getMessage());
        }
    }

    //Remove client from table
    public void disconnectClient(String clientIP) {
        connectedSensors.remove(clientIP);
        System.out.println("Client disconnected");
    }
}
