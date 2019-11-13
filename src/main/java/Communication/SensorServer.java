package Communication;

import Interfaces.ClientConnectionListener;
import Interfaces.TimeoutListener;
import Logic.TimeoutNotifier;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

public class SensorServer extends AbstractServer implements ClientConnectionListener, TimeoutListener {

    private static final long TIMEOUT_CHECK_PERIOD = 10; //seconds

    private final Map<String, SensorConnectionThread> connectedSensors; //Map of client IPs and their sensor threads

    private Timer timer;
    private TimeoutNotifier timeoutNotifier;

    public SensorServer(int port) throws IOException {
        super(port);
        connectedSensors = new HashMap<>();

        timer = new Timer();
        timeoutNotifier = new TimeoutNotifier();
        timeoutNotifier.addListener(this);
    }

    @Override
    public void startServer()  {
        super.startServer();

        timer.scheduleAtFixedRate(timeoutNotifier, 1000, TIMEOUT_CHECK_PERIOD*1000);

        try {
            while(true) { //Keep this running (almost) forever
                Socket socket = getServerSocket().accept();

                //Create and start a new thread when a new client is accepted
                SensorConnectionThread sensorClientThread = new SensorConnectionThread(this, socket);
                sensorClientThread.start();

                //Put client in table containing all clients
                connectedSensors.put(sensorClientThread.getIP(), sensorClientThread);
                System.out.println("\u2714 New client put in table");
            }
        }
        catch(IOException e) {
            System.err.println("\u274C Error accepting socket in " + getClass().getSimpleName() + "\n" + e.getMessage());
        }
    }

    @Override
    public void onConnect() {
        System.out.println("\uD83D\uDD17 New sensor client connected");
    }

    @Override
    public void onDisconnect(String clientIP) {
        connectedSensors.remove(clientIP);
        System.out.println("\uD83D\uDD0C Sensor client disconnected");
    }

    @Override
    public void checkTimeout() {
        connectedSensors.forEach((ip, client) -> {
            client.performTimeoutCheck();
        });
    }
}
