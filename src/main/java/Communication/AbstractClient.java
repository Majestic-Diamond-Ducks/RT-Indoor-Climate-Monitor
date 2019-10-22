package Communication;

import java.net.Socket;

public abstract class AbstractClient extends Thread {

    private final Socket clientSocket;
    private String IP;

    public AbstractClient(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.IP = clientSocket.getInetAddress().getHostName();
    }

    @Override
    public void run() {}

    protected Socket getSocket()    {
        return this.clientSocket;
    }

    protected String getIP()    {
        return this.IP;
    }
}
