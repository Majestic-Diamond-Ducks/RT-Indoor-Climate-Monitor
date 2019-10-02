package Communication;

import Data.ValueTable;
import Enums.ValueTableIdentifier;
import org.json.JSONObject;
import java.io.*;
import java.net.*;

public class SensorConnectionThread extends Thread    {
    private final Socket clientSocket;
    private final SensorServer server;
    private String clientName; //Client name

    private long responseNumber; //Number of responses

    private ValueTable values;

    public SensorConnectionThread(SensorServer server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
        this.clientName = clientSocket.getInetAddress().getHostName(); //Client IP address
        this.values = new ValueTable();
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
                    printHandledValues(json); //Print handled values
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

        values.putValue(json.getFloat("T"), responseNumber, ValueTableIdentifier.TEMP);
        values.putValue(json.getFloat("H"), responseNumber, ValueTableIdentifier.HUMIDITY);
        values.putValue(json.getFloat("L"), responseNumber, ValueTableIdentifier.LIGHT);
        values.putValue(json.getFloat("C"), responseNumber, ValueTableIdentifier.CO2);
        values.putValue(json.getFloat("D"), responseNumber, ValueTableIdentifier.DUST);
    }

    private void printHandledValues(JSONObject json)    { //Used for printing debug message in the console
        System.out.println(" ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ");
        System.out.println("-=-" + this.clientName + " | " + json.getString("N") + "-=-");
        System.out.println("Response number: \t" + this.responseNumber);
        System.out.println("Temperature: \t\t" + formatValues(ValueTableIdentifier.TEMP));
        System.out.println("Humidity: \t\t\t" + formatValues(ValueTableIdentifier.HUMIDITY));
        System.out.println("Light: \t\t\t\t" + formatValues(ValueTableIdentifier.LIGHT));
        System.out.println("CO2: \t\t\t\t" + formatValues(ValueTableIdentifier.CO2));
        System.out.println("Dust: \t\t\t\t" + formatValues(ValueTableIdentifier.DUST));
        System.out.println("_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _");
    }

    private String formatValues(ValueTableIdentifier k)    { //Formats float values for print only
        return String.format("%.02f", values.getValues(k)[0]) +
                "\tMin: " + String.format("%.02f", values.getValues(k)[1]) +
                "\tMax: " + String.format("%.02f", values.getValues(k)[2]) +
                "\tAvg: " + String.format("%.02f", values.getValues(k)[3]);
    }

    public String getClientName()  {
        return this.clientName;
    }

    private void disconnect()   {
        server.disconnectClient(this.clientName);
    }
}
