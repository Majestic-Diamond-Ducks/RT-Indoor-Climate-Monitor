package Communication;

import Data.ValueStorageBox;
import Interfaces.ClientConnectionListener;
import Interfaces.ServerNotifier;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class APIConnectionThread extends AbstractTimerClient implements ServerNotifier {

    private ValueStorageBox valueStorageBox;

    private OutputStreamWriter osw;

    private List<ClientConnectionListener> connectionListeners;

    public APIConnectionThread(APIServer server, Socket socket)    {
        super(socket);
        this.valueStorageBox = ValueStorageBox.getStorageBox();

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
        notifyConnect();
    }

    @Override
    public void run() {
        try {
            osw.write(valueStorageBox.getAllDataAsJsonArray().toString(4));
            osw.flush();
        }
        catch(SocketException e)  {
            e.getMessage();
            notifyDisconnect();
        }
        catch(IOException e) {
            e.getMessage();
        }
    }


    @Override
    public void notifyConnect() {
        for(ClientConnectionListener ccl : this.connectionListeners)    {
            ccl.onConnect();
        }
    }

    @Override
    public void notifyDisconnect() {
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
