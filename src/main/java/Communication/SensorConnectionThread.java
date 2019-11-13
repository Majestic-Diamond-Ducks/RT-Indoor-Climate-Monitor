package Communication;

import Data.ValueStorageBox;
import Interfaces.ClientConnectionListener;
import Interfaces.ServerNotifier;
import org.json.JSONObject;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class SensorConnectionThread extends AbstractClient implements ServerNotifier {

    private String clientName; //Client name

    private ValueStorageBox valueStorageBox;

    private List<ClientConnectionListener> connectionListeners;

    public SensorConnectionThread(SensorServer server, Socket clientSocket) {
        super(clientSocket);
        this.valueStorageBox = ValueStorageBox.getStorageBox();

        this.connectionListeners = new ArrayList<>();
        addListener(server);
    }

    @Override
    public void run()   {
        System.out.println("\u25B6 Sensor thread started");
        try (BufferedReader bReader = new BufferedReader(new InputStreamReader(this.getSocket().getInputStream()));){

            StringBuilder sb = new StringBuilder();
            String msgIn;

            while (null != (msgIn = bReader.readLine()))   {
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

        } catch (IOException e) {
            System.err.println("\u274C Network error in " + getClass().getSimpleName() + ": " + e.getMessage());
        }
        System.out.println("\u23F9 Sensor thread stopped");
        //Disconnect thread
        doDisconnect();
    }

    private void handleSensorResponse(JSONObject json)   { //Handles the values from the client sent json document

        if(this.clientName == null) { //Find a more elegant way to update client name in hashmap, preferably read it in SensorServer
            setClientName(json.getString("N"));
            doConnect(); //Notify that connection has been established
        }

        valueStorageBox.updateValues(this.clientName, json);

        valueStorageBox.printSensorValueDebugMessage(this.clientName, this.getIP()); //Prints the values. mainly for debug
    }

    private void setClientName(String clientName) {
        this.clientName = clientName;
        this.valueStorageBox.addClient(clientName);
    }

    @Override
    public void doConnect() {
        for(ClientConnectionListener ccl : this.connectionListeners)    {
            ccl.onConnect();
        }
    }

    @Override
    public void doDisconnect() {
        valueStorageBox.removeClient(this.clientName);
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
