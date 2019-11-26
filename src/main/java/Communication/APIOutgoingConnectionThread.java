package Communication;

import Data.ValueStorageBox;
import Interfaces.ClientConnectionListener;
import Interfaces.ServerNotifier;
import Logic.ReadWriteSemaphore;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class APIOutgoingConnectionThread extends AbstractTimerClient implements ServerNotifier {

    private ValueStorageBox valueStorageBox;
    private ReadWriteSemaphore readWriteSemaphore;

    private OutputStreamWriter osw;

    private List<ClientConnectionListener> connectionListeners;

    public APIOutgoingConnectionThread(APIOutgoingServer server, Socket socket)    {
        super(socket);
        this.valueStorageBox = ValueStorageBox.getStorageBox();
        this.readWriteSemaphore = ReadWriteSemaphore.getReadWriteSemaphore();

        this.connectionListeners = new ArrayList<>();
        addListener(server);

        try {
            OutputStream os = socket.getOutputStream(); //Create output stream
            this.osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void initialize()    {
        try {
            getSocket().setSoTimeout(60000);
        }
        catch(SocketException e) {
            System.err.println(LocalDateTime.now().format(getDateTimeFormat()) + " \u274C Error initializing outgoing API socket");
        }
        doConnect();
    }

    @Override
    public synchronized void run() {
        try{
            readWriteSemaphore.acquireRead();
        }
        catch(InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        try {
            osw.write(valueStorageBox.getAllDataAsJsonArray().toString(4));
            osw.write("\n");
            osw.flush();
        }
        catch(SocketException f)  {
            f.getMessage();
            doDisconnect();
        }
        catch(IOException f) {
            f.getMessage();
        }

        readWriteSemaphore.releaseRead();
    }


    @Override
    public void doConnect() {
        for(ClientConnectionListener ccl : this.connectionListeners)    {
            ccl.onConnect();
        }
    }

    @Override
    public void doDisconnect() {
        for(ClientConnectionListener ccl : this.connectionListeners)    {
            ccl.onDisconnect("");
        }
    }

    @Override
    public void addListener(ClientConnectionListener clientConnectionListener) {
        if(!connectionListeners.contains(clientConnectionListener)) {
            this.connectionListeners.add(clientConnectionListener);
        }
    }

    @Override
    public void removeListener(ClientConnectionListener clientConnectionListener) {
        connectionListeners.remove(clientConnectionListener);
    }
}
