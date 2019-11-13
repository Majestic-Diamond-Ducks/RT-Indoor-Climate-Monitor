package Communication;

import Data.ValueStorageBox;
import Interfaces.ClientConnectionListener;
import Interfaces.ServerNotifier;
import Logic.ReadWriteSemaphore;
import org.json.JSONObject;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class SensorConnectionThread extends AbstractClient implements ServerNotifier {

    private String clientName; //Client name

    private ValueStorageBox valueStorageBox;
    private ReadWriteSemaphore readWriteSemaphore;

    private List<ClientConnectionListener> connectionListeners;

    private int responseNumber;
    private int lastMeasuredResponseNumber;
    private int timeouts;

    public SensorConnectionThread(SensorServer server, Socket clientSocket) {
        super(clientSocket);
        this.valueStorageBox = ValueStorageBox.getStorageBox();
        this.readWriteSemaphore = ReadWriteSemaphore.getReadWriteSemaphore();

        this.connectionListeners = new ArrayList<>();
        addListener(server);

        this.responseNumber = 0;
        this.lastMeasuredResponseNumber = 0;
        this.timeouts = 0;
    }

    @Override
    public void run()   {
        System.out.println("\u25B6 Sensor thread started");
        try (BufferedReader bReader = new BufferedReader(new InputStreamReader(this.getSocket().getInputStream()));){

            StringBuilder sb = new StringBuilder();
            String msgIn;
            getSocket().setSoTimeout(60000); //set a 1 minute socket timeout

            while (null != (msgIn = bReader.readLine()))  {
                if(!("").equals(msgIn))    { //If line is not empty
                    sb.append(msgIn); //Add characters to string builder
                }
                else    { //If line is empty, create and print json
                    JSONObject json = new JSONObject(sb.toString());
                    sb.setLength(0); //Reset String builder
                    handleSensorResponse(json); //Handle json document
                }
            }
            this.getSocket().close(); //Close socket when connection ends

        }
        catch (SocketTimeoutException e)  {
            System.err.println("\u274C Network connection closed by timeout");
        }
        catch (IOException e) {
            System.err.println("\u274C Network error in " + getClass().getSimpleName() + ": " + e.getMessage());
        }

        System.out.println("\u23F9 Sensor thread stopped");
        //Disconnect thread
        doDisconnect();
    }

    private void handleSensorResponse(JSONObject json)   { //Handles the values from the client sent json document

        if(this.clientName == null) {
            setClientName(json.getString("N"));
            doConnect(); //Notify that connection has been established
        }
        incrementResponseNumber();

        try {
            readWriteSemaphore.acquireWrite();
        }
        catch(InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        valueStorageBox.updateValues(this.clientName, json);
        //valueStorageBox.printSensorValueDebugMessage(this.clientName, this.getIP()); //Prints the values. mainly for debug

        readWriteSemaphore.releaseWrite();
    }

    public void performTimeoutCheck()   {
        if(lastMeasuredResponseNumber == responseNumber)    {
            timeouts++;
            if(timeouts == 4) {
                System.out.println("\uD83D\uDD52 Sensor thread " + getId() + " with name " + this.clientName + " is about to time out");
            }
        }
        else if (timeouts >= 4){
            lastMeasuredResponseNumber = responseNumber;
            timeouts = 0;
            System.out.println("\uD83D\uDD52 " + this.clientName + " responded. Timeout counter reset");
        }
        else {
            lastMeasuredResponseNumber = responseNumber;
            timeouts = 0;
        }
    }

    private void setClientName(String clientName) {
        this.clientName = clientName;
        try{
            readWriteSemaphore.acquireWrite();
        }
        catch(InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        this.valueStorageBox.addClient(clientName);

        readWriteSemaphore.releaseWrite();
    }

    private void incrementResponseNumber()  {
        this.responseNumber++;
    }

    public int getResponseNumber()  {
        return this.responseNumber;
    }

    @Override
    public void doConnect() {
        for(ClientConnectionListener ccl : this.connectionListeners)    {
            ccl.onConnect();
        }
    }

    @Override
    public void doDisconnect() {
        try {
            readWriteSemaphore.acquireWrite();
        }
        catch(InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        valueStorageBox.removeClient(this.clientName);

        readWriteSemaphore.releaseWrite();

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
