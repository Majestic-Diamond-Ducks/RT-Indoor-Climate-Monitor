package Communication;

import java.net.Socket;
import java.time.format.DateTimeFormatter;
import java.util.TimerTask;

public abstract class AbstractTimerClient extends TimerTask {

    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final Socket clientSocket;
    private String IP;

    public AbstractTimerClient(Socket clientSocket) {
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
