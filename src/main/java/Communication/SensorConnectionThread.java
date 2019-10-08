package Communication;

import Data.ValueStorageBox;
import Enums.ValueTableIdentifier;
import org.json.JSONObject;
import java.io.*;
import java.net.*;

public class SensorConnectionThread extends Thread    {
    private final Socket clientSocket;
    private final SensorServer server;
    private String clientName; //Client name

    private long responseNumber; //Number of responses

    private ValueStorageBox valueStorageBox;

    public SensorConnectionThread(SensorServer server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
        //this.clientName = clientSocket.getInetAddress().getHostName(); //Client IP address
        this.valueStorageBox = ValueStorageBox.getStorageBox();
        //this.valueStorageBox.addClient(this.clientName);
    }

    @Override
    public void run()   {
        try (BufferedReader bReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));){

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
                    printHandledValues(); //Print handled values
                }
            }
            clientSocket.close(); //Close socket when connection ends

        } catch (IOException ex) {
            System.out.println("Network error: " + ex.getMessage());
        }
        //Disconnect thread
        disconnect();
    }

    private void handleSensorResponse(JSONObject json)   { //Handles the values from the client sent json document
        this.responseNumber++;

        if(this.clientName == null) { //Find a more elegant way to update client name in hashmap, preferably read it in SensorServer
            setClientName(json.getString("N"));
        }

        new Thread(() -> valueStorageBox.updateValues(this.clientName, responseNumber, json)).start();
    }

    private void printHandledValues()    { //Used for printing debug message in the console
        System.out.println(" ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾");
        System.out.println("-=- " + this.clientName + " | " + this.clientSocket.getInetAddress().getHostName() + " -=-");
        System.out.println("Response number: " + this.responseNumber);
        System.out.println("TEMP: \t" + formatValues(ValueTableIdentifier.TEMP));
        System.out.println("HMDT: \t" + formatValues(ValueTableIdentifier.HUMIDITY));
        System.out.println("LGHT: \t" + formatValues(ValueTableIdentifier.LIGHT));
        System.out.println("CO2: \t" + formatValues(ValueTableIdentifier.CO2));
        System.out.println("DUST: \t" + formatValues(ValueTableIdentifier.DUST));
        System.out.println("_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _");
    }

    private String formatValues(ValueTableIdentifier k)    { //Formats float values for print only
        float [] returnedValues = valueStorageBox.getValues(this.clientName, k);
        return String.format("%.02f", returnedValues[0]) +
                "\tMin: " + String.format("%.02f", returnedValues[1]) +
                "\tMax: " + String.format("%.02f", returnedValues[2]) +
                "\tAvg: " + String.format("%.02f", returnedValues[3]);
    }

    public String getClientName()  {
        return this.clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
        this.valueStorageBox.addClient(clientName);
    }

    private void disconnect()   {
        server.disconnectClient(this.clientName);
    }
}
