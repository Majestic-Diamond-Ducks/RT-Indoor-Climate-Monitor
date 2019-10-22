package Communication;

import Data.ValueStorageBox;
import org.json.JSONObject;
import java.io.*;
import java.net.*;

public class SensorConnectionThread extends AbstractClient{

    private final SensorServer server;
    private String clientName; //Client name

    private ValueStorageBox valueStorageBox;

    public SensorConnectionThread(SensorServer server, Socket clientSocket) {
        super(clientSocket);
        this.server = server;
        this.valueStorageBox = ValueStorageBox.getStorageBox();
    }

    @Override
    public void run()   {
        System.out.println("Sensor thread started");
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
            System.out.println("Network error: " + e.getMessage());
        }
        //Disconnect thread
        disconnect();
    }

    private void handleSensorResponse(JSONObject json)   { //Handles the values from the client sent json document

        if(this.clientName == null) { //Find a more elegant way to update client name in hashmap, preferably read it in SensorServer
            setClientName(json.getString("N"));
        }

        valueStorageBox.updateValues(this.clientName, json);

        valueStorageBox.printSensorValueDebugMessage(this.clientName, this.getIP()); //Prints the values. mainly for debug
    }

    private void setClientName(String clientName) {
        this.clientName = clientName;
        this.valueStorageBox.addClient(clientName);
    }

    private void disconnect()   {
        server.disconnectClient(this.getIP());
    }
}
