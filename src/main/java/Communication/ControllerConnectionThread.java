package Communication;

import Data.ValueStorageBox;

import java.net.Socket;

public class ControllerConnectionThread extends AbstractClient  {

    private final ControllerServer server;
    private String clientName;

    private ValueStorageBox valueStorageBox;

    public ControllerConnectionThread(ControllerServer server, Socket clientSocket) {
        super(clientSocket);
        this.server = server;
        this.valueStorageBox = ValueStorageBox.getStorageBox();
    }

    @Override
    public void run() {

    }

    private void disconnect()   {
        server.disconnectClient(this.getIP());
    }
}
