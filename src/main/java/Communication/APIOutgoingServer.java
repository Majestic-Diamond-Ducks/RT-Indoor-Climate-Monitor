package Communication;

import Interfaces.ClientConnectionListener;

import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Timer;

public class APIOutgoingServer extends AbstractServer implements ClientConnectionListener{

    private static final long API_UPDATES_PER_SECOND = 2;
    private static final long API_STARTUP_DELAY = 2; //Startup delay in seconds

    private APIOutgoingConnectionThread apiOutgoingConnectionThread;
    private APIOutgoingConnectionThread apiTryThread;
    private Timer apiTimer;

    public APIOutgoingServer(int port)  {
        super(port);
        apiTimer = new Timer();
    }

    @Override
    public void startServer() {
        super.startServer();
        try {
            while(true) {
                Socket socket = getServerSocket().accept();

                apiTryThread = new APIOutgoingConnectionThread(this, socket);
                System.out.println(LocalDateTime.now().format(getDateTimeFormat()) + " \uD83D\uDD01 Trying API outgoing thread");
                apiTryThread.initialize();
            }
        }
        catch(IOException e) {
            System.err.println(LocalDateTime.now().format(getDateTimeFormat()) + " \u274C Error accepting socket in " + getClass().getSimpleName() + "\n" + e.getMessage());
        }
    }

    @Override
    public void onConnect() {

        if(apiOutgoingConnectionThread != null) {
            System.err.println(LocalDateTime.now().format(getDateTimeFormat()) + " \u26A0 New API outgoing connection tried but an API connection already exist. Ignoring new API connection");
        }
        else {
            apiOutgoingConnectionThread = apiTryThread;
            apiTimer.scheduleAtFixedRate(apiOutgoingConnectionThread, API_STARTUP_DELAY*1000 , 1000/API_UPDATES_PER_SECOND);
            System.out.println(LocalDateTime.now().format(getDateTimeFormat()) + " \uD83D\uDD17 API outgoing connection started");
        }
        apiTryThread = null; //reset api try thread
    }

    @Override
    public void onDisconnect(String string) {
        apiTimer.cancel();
        apiTimer.purge();
        apiOutgoingConnectionThread = null;
        apiTimer = new Timer();

        System.out.println(LocalDateTime.now().format(getDateTimeFormat()) + " \uD83D\uDD0C API Disconnected");
    }
}
