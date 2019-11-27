package Data;

import Enums.ValueTableIdentifier;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ValueStorageBox {

    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

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
            System.out.println(LocalDateTime.now().format(dateTimeFormatter) + " \u267B Old client detected. Replacing old value table");
        }

        clientsValuesMap.put(clientName, new SensorValues());
        System.out.println(LocalDateTime.now().format(dateTimeFormatter) + " \uD83D\uDCDD Client value table created");
    }

    public synchronized void removeClient(String clientName) {
        clientsValuesMap.remove(clientName); //remove old table if that is the case
        System.out.println(LocalDateTime.now().format(dateTimeFormatter) + " \uD83E\uDDF9 Client value table removed");
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

    public synchronized void updateLimits(String clientName, JSONObject json) {
        if(json.has("TempMin")) {
            clientsValuesMap.get(clientName).setValueLowerLimit(json.getFloat("TempMin"), ValueTableIdentifier.TEMP);
        }
        if(json.has("TempMax")) {
            clientsValuesMap.get(clientName).setValueUpperLimit(json.getFloat("TempMax"), ValueTableIdentifier.TEMP);
        }
        if(json.has("CO2Limit")) {
            clientsValuesMap.get(clientName).setValueUpperLimit(json.getFloat("CO2Limit"), ValueTableIdentifier.CO2);
        }
        if(json.has("DustLimit")) {
            clientsValuesMap.get(clientName).setValueUpperLimit(json.getFloat("DustLimit"), ValueTableIdentifier.DUST);
        }
    }

    public synchronized JSONArray getAllDataAsJsonArray() {
        JSONArray jArray = new JSONArray();

        clientsValuesMap.forEach((sensor, sensorValues) -> {
            JSONObject outerJson = new JSONObject();
            JSONObject innerJson = new JSONObject();

            outerJson.put("NAME", sensor);

            for(ValueTableIdentifier v : ValueTableIdentifier.values()) {
                if(null == innerJson)   {
                    innerJson = new JSONObject();
                }

                if(sensorValues.getLastValue(v) > -100) {
                    innerJson.put("Current", sensorValues.getLastValue(v));
                }

                if(sensorValues.getMinValue(v) > -100) {
                    innerJson.put("Min", sensorValues.getMinValue(v));
                }

                if(sensorValues.getMaxValue(v) > -100) {
                    innerJson.put("Max", sensorValues.getMaxValue(v));
                }

                if(sensorValues.getAvgValue(v) > -100) {
                    innerJson.put("Average", sensorValues.getAvgValue(v));
                }

                if(sensorValues.getValueLowerLimit(v) > -100) {
                    innerJson.put("Lower limit", sensorValues.getValueLowerLimit(v));
                }

                if(sensorValues.getValueUpperLimit(v) > -100) {
                    innerJson.put("Upper limit", sensorValues.getValueUpperLimit(v));
                }

                outerJson.put(v.toString(), innerJson);
                innerJson = null;
            }
            jArray.put(outerJson);
        });
        return jArray;
    }
}
