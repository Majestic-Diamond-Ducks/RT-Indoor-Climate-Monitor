package Communication;

import Data.ValueStorageBox;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class APIConnectionThread extends AbstractTimerClient {

    private ValueStorageBox valueStorageBox;
    private final String HOSTNAME = "localhost";
    private final int PORT = 6970;

    public APIConnectionThread(APIServer server, Socket socket)    {
        super(socket);
        this.valueStorageBox = ValueStorageBox.getStorageBox();
    }

    @Override
    public void run() {

        try(Socket socket = new Socket(HOSTNAME, PORT);) {

            OutputStream os = socket.getOutputStream(); //Create output stream
            OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);

            System.out.println("Press Enter to send message. Type exit to exit the program");
            while(true) {
                osw.write(valueStorageBox.getAllDataAsJsonArray().toString());
                osw.flush();
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}
