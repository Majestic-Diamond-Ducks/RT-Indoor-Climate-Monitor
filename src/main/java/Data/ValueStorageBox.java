package Data;

import Enums.ValueTableIdentifier;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ValueStorageBox {

    private Map<String, ValueTable> clientsValuesMap; //Contains client name and values
    private static ValueStorageBox valueStorageBox;

    private ValueStorageBox()    {
        clientsValuesMap = new HashMap<>();
    }

    public static ValueStorageBox getStorageBox()    {
        if(null == valueStorageBox) {
            valueStorageBox = new ValueStorageBox();
        }
        return valueStorageBox;
    }

    //TODO Threadsafe this entire god damn thing


    public void addClient(String clientName) {
        if(clientsValuesMap.containsKey(clientName))    { //Check if client already exist in table

            //TODO Thread safe the removal of the value table entry
            clientsValuesMap.remove(clientName); //remove old table if that is the case
            System.out.println("Old client detected. Removing old value table");
        }
        clientsValuesMap.put(clientName, new ValueTable());
        System.out.println("Client value table created");
    }

    public void updateValues(String clientName, JSONObject json) {
        //TODO Threadsafe the value table when updating values
        if(!clientsValuesMap.containsKey(clientName))   {
            addClient(clientName);
        }
        clientsValuesMap.get(clientName).incrementResponseNumber();
        clientsValuesMap.get(clientName).putValue(json.getFloat("T"), ValueTableIdentifier.TEMP);
        clientsValuesMap.get(clientName).putValue(json.getFloat("H"), ValueTableIdentifier.HUMIDITY);
        clientsValuesMap.get(clientName).putValue(json.getFloat("L"), ValueTableIdentifier.LIGHT);
        clientsValuesMap.get(clientName).putValue(json.getFloat("C"), ValueTableIdentifier.CO2);
        clientsValuesMap.get(clientName).putValue(json.getFloat("D"), ValueTableIdentifier.DUST);
    }

    /*
        ######## DEBUG ########
        Prints the values for a given client. Use this mainly for debugging and ensuring values are correct.
        //TODO Disable or remove this when we have a web client up and running
     */
    public void printValuesFromClient(String clientName, String IP)    {
        System.out.println(" ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾");
        System.out.println("-=- " + clientName + " | " + IP + " -=-");
        System.out.println("Response number: " + clientsValuesMap.get(clientName).getResponseNumber()); //Possible move response number to the value table
        System.out.println("TEMP: \t" + formatValues(clientName, ValueTableIdentifier.TEMP));
        System.out.println("HMDT: \t" + formatValues(clientName, ValueTableIdentifier.HUMIDITY));
        System.out.println("LGHT: \t" + formatValues(clientName, ValueTableIdentifier.LIGHT));
        System.out.println("CO2: \t" + formatValues(clientName, ValueTableIdentifier.CO2));
        System.out.println("DUST: \t" + formatValues(clientName, ValueTableIdentifier.DUST));
        System.out.println("_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _");
    }

    private String formatValues(String clientName, ValueTableIdentifier v)    { //Formats float values for print only

        return String.format("%.02f", clientsValuesMap.get(clientName).getLast(v)) +
                "\tMin: " + String.format("%.02f", clientsValuesMap.get(clientName).getMin(v)) +
                "\tMax: " + String.format("%.02f", clientsValuesMap.get(clientName).getMax(v)) +
                "\tAvg: " + String.format("%.02f", clientsValuesMap.get(clientName).getAvg(v));
    }
}
