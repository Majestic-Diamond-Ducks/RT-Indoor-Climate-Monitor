package Communication;

import Data.ValueStorageBox;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class APIConnectionThread extends AbstractTimerClient {

    private ValueStorageBox valueStorageBox;

    private OutputStream os;
    private OutputStreamWriter osw;

    public APIConnectionThread(APIServer server, Socket socket)    {
        super(socket);
        this.valueStorageBox = ValueStorageBox.getStorageBox();
        try {
            this.os = socket.getOutputStream(); //Create output stream
            this.osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            osw.write(valueStorageBox.getAllDataAsJsonArray().toString());
            osw.flush();
        }
        catch(SocketException e)  {
            //TODO write call to close thread in API Server when socket or IO exception occurs
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}
