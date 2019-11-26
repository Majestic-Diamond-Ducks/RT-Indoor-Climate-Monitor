package Communication;

import Data.ValueStorageBox;
import Interfaces.ClientConnectionListener;
import Interfaces.ServerNotifier;
import Logic.ReadWriteSemaphore;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class APIIncomingConnectionThread extends AbstractClient implements ServerNotifier {

    private ValueStorageBox valueStorageBox;
    private ReadWriteSemaphore readWriteSemaphore;

    private List<ClientConnectionListener> connectionListeners;


    public APIIncomingConnectionThread(APIIncomingServer server, Socket socket)    {
        super(socket);
        this.valueStorageBox = ValueStorageBox.getStorageBox();
        this.readWriteSemaphore = ReadWriteSemaphore.getReadWriteSemaphore();

        this.connectionListeners = new ArrayList<>();
        addListener(server);
    }

    @Override
    public void run()   {
        System.out.println(LocalDateTime.now().format(getDateTimeFormat()) + " \uD83D\uDD17 API incoming connection started");
        try (BufferedReader bReader = new BufferedReader(new InputStreamReader(this.getSocket().getInputStream()));){

            StringBuilder sb = new StringBuilder();
            String msgIn;
            getSocket().setSoTimeout(60000); //set a 1 minute socket timeout

            while (null != (msgIn = bReader.readLine()))  {
                if(!("").equals(msgIn))    { //If line is not empty
                    sb.append(msgIn); //Add characters to string builder
                }
                else    { //If line is empty, create and print json
                    System.out.println(sb.toString());
                    try {
                        JSONArray json = new JSONArray(sb.toString());
                        handleIncomingAPIDocument(json); //Handle json document
                    }
                    catch(JSONException e)    {
                        System.out.println(LocalDateTime.now().format(getDateTimeFormat()) + " \u26A0 Malformed JSON Array from API incoming");
                    }
                    sb.setLength(0); //Reset String builder
                }
            }
            this.getSocket().close(); //Close socket when connection ends

        }
        catch (SocketTimeoutException e)  {
            System.err.println(LocalDateTime.now().format(getDateTimeFormat()) + " \u274C Network connection closed by timeout");
        }
        catch (IOException e) {
            System.err.println(LocalDateTime.now().format(getDateTimeFormat()) + " \u274C Network error in " + getClass().getSimpleName() + ": " + e.getMessage());
        }

        System.out.println(LocalDateTime.now().format(getDateTimeFormat()) + " \u23F9 API incoming thread stopped");
        //Disconnect thread
        doDisconnect();
    }

    public void initialize()    {
        try {
            getSocket().setSoTimeout(60000);
        }
        catch(SocketException e) {
            System.err.println(LocalDateTime.now().format(getDateTimeFormat()) + " \u274C Error initializing incoming API socket");
        }
        doConnect();
    }

    private synchronized void handleIncomingAPIDocument(JSONArray jsonArray)    {
        try {
            readWriteSemaphore.acquireWrite();
            try {
                for(int i = 0; i < jsonArray.length(); i++) {
                    valueStorageBox.updateLimits(jsonArray.getJSONObject(i).getString("N"), jsonArray.getJSONObject(i));
                }
            }
            catch(JSONException e)  {
                System.out.println(LocalDateTime.now().format(getDateTimeFormat()) + " \u26A0 Malformed JSON object inside API incoming array");

            }
        }
        catch(InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        readWriteSemaphore.releaseWrite();
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
