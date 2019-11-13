package Communication;

import Interfaces.ClientConnectionListener;

import java.io.IOException;
import java.net.Socket;
import java.util.Timer;

public class APIServer extends AbstractServer implements ClientConnectionListener{

    private static final long API_UPDATES_PER_SECOND = 5;
    private static final long API_STARTUP_DELAY = 2; //Startup delay in seconds

    private APIConnectionThread apiConnectionThread;
    private APIConnectionThread apiTryThread;
    private Timer apiTimer;

    public APIServer(int port)  {
        super(port);
        apiTimer = new Timer();
    }

    @Override
    public void startServer() {
        super.startServer();
        try {
            while(true) {
                Socket socket = getServerSocket().accept();

                apiTryThread = new APIConnectionThread(this, socket);
                System.out.println("\uD83D\uDD01 Trying API connection thread");
                apiTryThread.initialize();
            }
        }
        catch(IOException e) {
            System.err.println("\u274C Error accepting socket in " + getClass().getSimpleName() + "\n" + e.getMessage());
        }
    }

    @Override
    public void onConnect() {

        if(apiConnectionThread != null) {
            System.err.println("\u26A0 New API connection tried but an API connection already exist. Ignoring new API connection");
        }
        else {
            apiConnectionThread = apiTryThread;
            apiTimer.scheduleAtFixedRate(apiConnectionThread, API_STARTUP_DELAY*1000 , 1000/API_UPDATES_PER_SECOND);
            System.out.println("\uD83D\uDD17 API connection started");
        }
        apiTryThread = null; //reset api try thread
    }

    @Override
    public void onDisconnect(String string) {
        apiTimer.cancel();
        apiTimer.purge();
        apiConnectionThread = null;
        apiTimer = new Timer();

        System.out.println("\uD83D\uDD0C API Disconnected");
    }
}
