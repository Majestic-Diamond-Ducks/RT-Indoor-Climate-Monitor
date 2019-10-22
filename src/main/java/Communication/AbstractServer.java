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
            System.err.println("Error creating server socket in " + getClass().getSimpleName() + " : " + e.getMessage());
        }
    }

    public void startServer()   {
        System.out.println(getClass().getSimpleName() + " started");
    }

    public void disconnectClient(String clientIP)   {}

    protected ServerSocket getServerSocket()    {
        return this.serverSocket;
    }

}
