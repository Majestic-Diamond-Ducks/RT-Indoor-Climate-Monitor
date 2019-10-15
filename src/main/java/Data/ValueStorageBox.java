package Data;

import Enums.ValueTableIdentifier;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ValueStorageBox {

    private Map<String, SensorValues> clientsValuesMap; //Contains client name and values
    private static ValueStorageBox valueStorageBox;

    private boolean available;

    private ValueStorageBox()    {
        clientsValuesMap = new HashMap<>();
        this.available = true;
    }

    public static synchronized ValueStorageBox getStorageBox()    {
        if(null == valueStorageBox) {
            valueStorageBox = new ValueStorageBox();
        }
        return valueStorageBox;
    }

    public synchronized void addClient(String clientName) {
        while(!this.available)    {
            try {
                wait();
            }
            catch(InterruptedException e)   {
            }
        }
        this.available = false;

        if(clientsValuesMap.containsKey(clientName))    { //Check if client already exist in table

            clientsValuesMap.remove(clientName); //remove old table if that is the case
            System.out.println("Old client detected. Removing old value table");
        }
        clientsValuesMap.put(clientName, new SensorValues());
        System.out.println("Client value table created");

        this.available = true;
        notifyAll();
    }

    public synchronized void updateValues(String clientName, JSONObject json) {

        if(!clientsValuesMap.containsKey(clientName))   { //First make sure that the client exist in the map, if not add it.
            addClient(clientName);
        }

        while(!this.available)    {
            try {
                wait();
            }
            catch(InterruptedException e)   {
            }
        }
        this.available = false;

        clientsValuesMap.get(clientName).incrementResponseNumber();
        clientsValuesMap.get(clientName).putValue(json.getFloat("T"), ValueTableIdentifier.TEMP);
        clientsValuesMap.get(clientName).putValue(json.getFloat("H"), ValueTableIdentifier.HUMIDITY);
        clientsValuesMap.get(clientName).putValue(json.getFloat("L"), ValueTableIdentifier.LIGHT);
        clientsValuesMap.get(clientName).putValue(json.getFloat("C"), ValueTableIdentifier.CO2);
        clientsValuesMap.get(clientName).putValue(json.getFloat("D"), ValueTableIdentifier.DUST);

        this.available = true;
        notifyAll();
    }

    /*
        ######## DEBUG ########
        Prints the values for a given client. Use this mainly for debugging and ensuring values are correct.
        //TODO Disable or remove this when we have a web client up and running
     */
    public synchronized void printSensorValueDebugMessage(String clientName, String IP)    {

        if(!clientsValuesMap.containsKey(clientName))    {
            return; //Check and break if map does not contain requested client
        }

        while(!this.available)    {
            try {
                wait();
            }
            catch(InterruptedException e)   {
            }
        }
        this.available = false;

        System.out.println(" ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾");
        System.out.println("-=- " + clientName + " | " + IP + " -=-");
        System.out.println("Response number: " + clientsValuesMap.get(clientName).getResponseNumber()); //Possible move response number to the value table
        System.out.println("TEMP: \t" + formatValues(clientName, ValueTableIdentifier.TEMP));
        System.out.println("HUM: \t" + formatValues(clientName, ValueTableIdentifier.HUMIDITY));
        System.out.println("LGHT: \t" + formatValues(clientName, ValueTableIdentifier.LIGHT));
        System.out.println("CO2: \t" + formatValues(clientName, ValueTableIdentifier.CO2));
        System.out.println("DUST: \t" + formatValues(clientName, ValueTableIdentifier.DUST));
        System.out.println("_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _");

        this.available = true;
        notifyAll();
    }

    private String formatValues(String clientName, ValueTableIdentifier v)    { //Formats float values for print only
        return String.format("%.02f", clientsValuesMap.get(clientName).getLastValue(v)) +
                "\tMin: " + String.format("%.02f", clientsValuesMap.get(clientName).getMinValue(v)) +
                "\tMax: " + String.format("%.02f", clientsValuesMap.get(clientName).getMaxValue(v)) +
                "\tAvg: " + String.format("%.02f", clientsValuesMap.get(clientName).getAvgValue(v));
    }
}
