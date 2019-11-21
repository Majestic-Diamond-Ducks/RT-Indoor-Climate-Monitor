package Communication;

import java.net.Socket;
import java.time.format.DateTimeFormatter;

public abstract class AbstractClient extends Thread {

    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

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

    protected DateTimeFormatter getDateTimeFormat() {
        return dateTimeFormatter;
    }
}
