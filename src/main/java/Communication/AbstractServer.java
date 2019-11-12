package Communication;

import java.io.IOException;
import java.net.ServerSocket;

public abstract class AbstractServer {

    private ServerSocket serverSocket;

    public AbstractServer(int port) {
        try {
            //Create server socket
            serverSocket = new ServerSocket(port);
        }
        catch(IOException e) {
            System.err.println("\u274C Error creating server socket in " + getClass().getSimpleName() + " : " + e.getMessage());
        }
    }

    public void startServer()   {
        System.out.println("\u25B6 " + getClass().getSimpleName() + " started");
    }

    protected ServerSocket getServerSocket()    {
        return this.serverSocket;
    }

}
