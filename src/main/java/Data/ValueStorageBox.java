package Data;

import Enums.ValueTableIdentifier;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ValueStorageBox {

    private Map<String, SensorValues> clientsValuesMap; //Contains client name and values
    private static ValueStorageBox valueStorageBox;

    private ValueStorageBox()    {
        clientsValuesMap = new HashMap<>();
    }

    public static synchronized ValueStorageBox getStorageBox()    {
        if(null == valueStorageBox) {
            valueStorageBox = new ValueStorageBox();
        }
        return valueStorageBox;
    }

    public synchronized void addClient(String clientName) {
        if(clientsValuesMap.containsKey(clientName))    { //Check if client already exist in table

            removeClient(clientName);
            System.out.println("\u267B Old client detected. Replacing old value table");
        }

        clientsValuesMap.put(clientName, new SensorValues());
        System.out.println("\uD83D\uDCDD Client value table created");
    }

    public synchronized void removeClient(String clientName) {
        clientsValuesMap.remove(clientName); //remove old table if that is the case
        System.out.println("\uD83E\uDDF9 Client value table removed");
    }

    public synchronized void updateValues(String clientName, JSONObject json) {
        if(!clientsValuesMap.containsKey(clientName))   { //First make sure that the client exist in the map, if not add it.
            addClient(clientName);
        }

        clientsValuesMap.get(clientName).putValue(json.getFloat("T"), ValueTableIdentifier.TEMP);
        clientsValuesMap.get(clientName).putValue(json.getFloat("H"), ValueTableIdentifier.HUMIDITY);
        clientsValuesMap.get(clientName).putValue(json.getFloat("L"), ValueTableIdentifier.LIGHT);
        clientsValuesMap.get(clientName).putValue(json.getFloat("C"), ValueTableIdentifier.CO2);
        clientsValuesMap.get(clientName).putValue(json.getFloat("D"), ValueTableIdentifier.DUST);
    }

    public synchronized JSONArray getAllDataAsJsonArray() {
        JSONArray jArray = new JSONArray();

        clientsValuesMap.forEach((sensor, sensorValues) -> {
            JSONObject outerJson = new JSONObject();
            JSONObject innerJson = new JSONObject();

            outerJson.put("NAME", sensor);

            for(ValueTableIdentifier v : ValueTableIdentifier.values()) {

                if(sensorValues.getLastValue(v) > -100) {
                    innerJson.put("Current", sensorValues.getLastValue(v));
                }
                else if(innerJson.has("Current"))   {
                    innerJson.remove("Current");
                }

                if(sensorValues.getMinValue(v) > -100) {
                    innerJson.put("Min", sensorValues.getMinValue(v));
                }
                else if(innerJson.has("Min"))   {
                    innerJson.remove("Min");
                }

                if(sensorValues.getMaxValue(v) > -100) {
                    innerJson.put("Max", sensorValues.getMaxValue(v));
                }
                else if(innerJson.has("Max"))   {
                    innerJson.remove("Max");
                }

                if(sensorValues.getAvgValue(v) > -100) {
                    innerJson.put("Average", sensorValues.getAvgValue(v));
                }
                else if(innerJson.has("Average"))   {
                    innerJson.remove("Average");
                }

                if(sensorValues.getValueLowerLimit(v) > -100) {
                    innerJson.put("Lower limit", sensorValues.getValueLowerLimit(v));
                }
                else if(innerJson.has("Lower limit"))   {
                    innerJson.remove("Lower limit");
                }

                if(sensorValues.getValueUpperLimit(v) > -100) {
                    innerJson.put("Upper limit", sensorValues.getValueUpperLimit(v));
                }
                else if(innerJson.has("Upper limit"))   {
                    innerJson.remove("Upper limit");
                }

                outerJson.put(v.toString(), innerJson);
            }
            jArray.put(outerJson);
        });
        return jArray;
    }

    /*
        ######## DEBUG ########
        Prints the values for a given client. Use this mainly for debugging and ensuring values are correct.

  */

    /*public synchronized void printSensorValueDebugMessage(String clientName, String IP)    {

        if(!clientsValuesMap.containsKey(clientName))    {
            return; //Check and break if map does not contain requested client
        }

        System.out.println(" ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾");
        System.out.println("-=- " + clientName + " | " + IP + " -=-");
        System.out.println("TEMP: \t" + formatValues(clientName, ValueTableIdentifier.TEMP));
        System.out.println("HUM: \t" + formatValues(clientName, ValueTableIdentifier.HUMIDITY));
        System.out.println("LGHT: \t" + formatValues(clientName, ValueTableIdentifier.LIGHT));
        System.out.println("CO2: \t" + formatValues(clientName, ValueTableIdentifier.CO2));
        System.out.println("DUST: \t" + formatValues(clientName, ValueTableIdentifier.DUST));
        System.out.println("_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _");

    }

    private String formatValues(String clientName, ValueTableIdentifier v)    { //Formats float values for print only
        return String.format("%.02f", clientsValuesMap.get(clientName).getLastValue(v)) +
                "\tMin: " + String.format("%.02f", clientsValuesMap.get(clientName).getMinValue(v)) +
                "\tMax: " + String.format("%.02f", clientsValuesMap.get(clientName).getMaxValue(v)) +
                "\tAvg: " + String.format("%.02f", clientsValuesMap.get(clientName).getAvgValue(v));
    }*/
}
