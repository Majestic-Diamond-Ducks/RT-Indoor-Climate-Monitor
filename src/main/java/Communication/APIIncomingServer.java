package Communication;

import Interfaces.ClientConnectionListener;

import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;

public class APIIncomingServer extends AbstractServer implements ClientConnectionListener {

    private APIIncomingConnectionThread apiIncomingConnectionThread;
    private APIIncomingConnectionThread apiTryThread;

    public APIIncomingServer(int port) throws IOException {
        super(port);
    }

    @Override
    public void startServer()   {
        super.startServer();
        try {
            while(true) {
                Socket socket = getServerSocket().accept();

                apiTryThread = new APIIncomingConnectionThread(this, socket);
                System.out.println(LocalDateTime.now().format(getDateTimeFormat()) + " \uD83D\uDD01 Trying API incoming thread");
                apiTryThread.initialize();
            }

        }
        catch(IOException e) {
            System.err.println(LocalDateTime.now().format(getDateTimeFormat()) + " \u274C Error accepting socket in " + getClass().getSimpleName() + "\n" + e.getMessage());
        }

    }

    @Override
    public void onConnect() {
        if(apiIncomingConnectionThread != null) {
            System.err.println(LocalDateTime.now().format(getDateTimeFormat()) + " \u26A0 New API incoming connection tried but an API connection already exist. Ignoring new API connection");
        }
        else {
            apiIncomingConnectionThread = apiTryThread;
            apiIncomingConnectionThread.start();
            System.out.println(LocalDateTime.now().format(getDateTimeFormat()) + " \uD83D\uDD17 API incoming connection started");
        }
        apiTryThread = null;
    }

    @Override
    public void onDisconnect(String string) {
        apiIncomingConnectionThread = null;
    }
}
