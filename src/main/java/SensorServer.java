import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class SensorServer {

    private ServerSocket serverSocket;
    private final Map<String, SensorConnectionThread> connectedSensors;

    public SensorServer(int port) throws IOException {
        connectedSensors = new HashMap<>();
        try {
            serverSocket = new ServerSocket(port);
        }
        catch(IOException e) {
            System.err.println("Error creating server socket\n" + e.getMessage());
        }
    }

    public void startServer()  {
        System.out.println("Server started");
        while(true) {
            try {
                Socket newClient = serverSocket.accept();

                SensorConnectionThread sensorClientThread = new SensorConnectionThread(this, newClient);
                System.out.println("New client created");

                sensorClientThread.start();
                System.out.println("New client started");

                connectedSensors.put(sensorClientThread.getClientName(), sensorClientThread);
                System.out.println("New client put in table");

            }
            catch(IOException e) {
                System.err.println("Error accepting socket\n" + e.getMessage());
            }
        }
    }

    public void disconnectClient(String clientName) {
        connectedSensors.remove(clientName);
        System.out.println("Client disconnected");
    }
}
