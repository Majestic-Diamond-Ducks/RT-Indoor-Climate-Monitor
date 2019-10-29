package Communication;

import java.io.IOException;
import java.net.Socket;
import java.util.Timer;

public class APIServer extends AbstractServer{

    private final long API_UPDATES_PER_SECOND = 10;
    private final long API_STARTUP_DELAY = 5; //Startup delay in seconds

    private APIConnectionThread apiConnectionThread;
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

                //TODO rework this so the API Thread also sends a disconnect signal when the api disconnects
                if(apiConnectionThread != null) { //Reset timer and timer task if the api-reconnects
                    apiTimer.cancel();
                    apiTimer.purge();
                    apiConnectionThread = null;
                    apiTimer = new Timer();
                }
                apiConnectionThread = new APIConnectionThread(this, socket);
                apiTimer.scheduleAtFixedRate(apiConnectionThread, API_STARTUP_DELAY*1000 , 1000/API_UPDATES_PER_SECOND);
                System.out.println("API connection created");
            }
        }
        catch(IOException e) {
            System.err.println("Error accepting socket\n" + e.getMessage());
        }
    }

    @Override
    public void disconnectClient(String clientIP) {
        apiConnectionThread = null;
        System.out.println("API connection removed");
    }
}
