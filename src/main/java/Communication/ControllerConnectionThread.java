package Communication;

import Data.ValueStorageBox;
import Interfaces.ClientConnectionListener;
import Interfaces.ServerNotifier;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ControllerConnectionThread extends AbstractTimerClient implements ServerNotifier {

    private String clientName;

    private ValueStorageBox valueStorageBox;

    private List<ClientConnectionListener> connectionListeners;

    public ControllerConnectionThread(ControllerServer server, Socket clientSocket) {
        super(clientSocket);
        this.valueStorageBox = ValueStorageBox.getStorageBox();

        this.connectionListeners = new ArrayList<>();
        addListener(server);
    }

    @Override
    public void run() {

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
            ccl.onDisconnect(this.getIP());
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
