package Communication;

import java.io.IOException;
import java.net.ServerSocket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class AbstractServer {

    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private ServerSocket serverSocket;

    public AbstractServer(int port) {
        try {
            //Create server socket
            serverSocket = new ServerSocket(port);
        }
        catch(IOException e) {
            System.err.println(LocalDateTime.now().format(getDateTimeFormat()) + " \u274C Error creating server socket in " + getClass().getSimpleName() + " : " + e.getMessage());
        }
    }

    public void startServer()   {
        System.out.println(LocalDateTime.now().format(getDateTimeFormat()) + " \u25B6 " + getClass().getSimpleName() + " started");
    }

    protected ServerSocket getServerSocket()    {
        return this.serverSocket;
    }

    protected DateTimeFormatter getDateTimeFormat() {
        return dateTimeFormatter;
    }

}
