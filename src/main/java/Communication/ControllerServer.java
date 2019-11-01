package Communication;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

public class ControllerServer extends AbstractServer{

    private final long CONTROLLER_UPDATES_PER_SECOND = 10;
    private final long CONTROLLER_STARTUP_DELAY = 30; //in seconds

    private final Map<String, ControllerConnectionThread> connectedControllers;

    private Timer controllerUpdateTimer;

    public ControllerServer(int port) throws IOException    {
        super(port);
        connectedControllers = new HashMap<>();
    }

    @Override
    public void startServer() {
        super.startServer();
        try {
            while(true) { //Keep this running (almost) forever
                Socket socket = getServerSocket().accept();

                ControllerConnectionThread controllerConnectionThread = new ControllerConnectionThread(this, socket);
                controllerUpdateTimer.scheduleAtFixedRate(controllerConnectionThread, CONTROLLER_STARTUP_DELAY*1000, 1000/CONTROLLER_UPDATES_PER_SECOND);

                connectedControllers.put(controllerConnectionThread.getIP(), controllerConnectionThread);
            }
        }
        catch(IOException e) {
            System.err.println("Error accepting socket\n" + e.getMessage());
        }
    }

    @Override
    //Remove client from table
    public void disconnectClient(String clientIP) {
        connectedControllers.remove(clientIP);
        System.out.println("Client disconnected");
    }
}
