package Communication;

import Data.ValueStorageBox;

import java.net.Socket;

public class APIConnectionThread extends AbstractTimerClient {

    private ValueStorageBox valueStorageBox;

    public APIConnectionThread(APIServer server, Socket socket)    {
        super(socket);
        this.valueStorageBox = ValueStorageBox.getStorageBox();
    }

    @Override
    public void run() {
        //TODO read all and construct JSON from Value storage box
    }
}
