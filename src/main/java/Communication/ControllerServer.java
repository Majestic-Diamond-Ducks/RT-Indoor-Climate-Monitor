package Communication;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ControllerServer extends AbstractServer{

    private final Map<String, ControllerConnectionThread> connectedControllers;

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
                controllerConnectionThread.start();

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
