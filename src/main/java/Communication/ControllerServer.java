package Communication;

import Interfaces.ClientConnectionListener;

import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

public class ControllerServer extends AbstractServer implements ClientConnectionListener {

    private static final long CONTROLLER_UPDATES_PER_SECOND = 10;
    private static final long CONTROLLER_STARTUP_DELAY = 30; //in seconds

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
                System.out.println(LocalDateTime.now().format(getDateTimeFormat()) + " \u2714 New client put in table");
            }
        }
        catch(IOException e) {
            System.err.println(LocalDateTime.now().format(getDateTimeFormat()) + " \u274C Error accepting socket in " + getClass().getSimpleName() + "\n" + e.getMessage());
        }
    }

    @Override
    public void onConnect() {
        System.out.println(LocalDateTime.now().format(getDateTimeFormat()) + " \uD83D\uDD17 New controller client connected");
    }

    @Override
    public void onDisconnect(String clientIP) {
        connectedControllers.remove(clientIP);
        System.out.println(LocalDateTime.now().format(getDateTimeFormat()) + " \uD83D\uDD0C Controller client disconnected");
    }
}
