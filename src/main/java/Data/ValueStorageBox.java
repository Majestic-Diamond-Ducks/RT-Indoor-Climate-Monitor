package Data;

import Enums.ValueTableIdentifier;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ValueStorageBox {

    private Map<String, ValueTable> clientsValuesMap; //Contains client name and values

    public ValueStorageBox()    {
        clientsValuesMap = new HashMap<>();
    }

    //TODO Threadsafe this entire god damn thing


    public void addClient(String clientName) {
        if(clientsValuesMap.containsKey(clientName))    { //Check if client already exist in table

            //TODO Thread safe the removal of the value table entry
            clientsValuesMap.remove(clientName); //remove old table if that is the case
        }
        clientsValuesMap.put(clientName, new ValueTable());
    }

    public void updateValues(String clientName, long responseNumber, JSONObject json) {
        //TODO Threadsafe the value table when updating values
        clientsValuesMap.get(clientName).putValue(json.getFloat("T"), responseNumber, ValueTableIdentifier.TEMP);
        clientsValuesMap.get(clientName).putValue(json.getFloat("H"), responseNumber, ValueTableIdentifier.HUMIDITY);
        clientsValuesMap.get(clientName).putValue(json.getFloat("L"), responseNumber, ValueTableIdentifier.LIGHT);
        clientsValuesMap.get(clientName).putValue(json.getFloat("C"), responseNumber, ValueTableIdentifier.CO2);
        clientsValuesMap.get(clientName).putValue(json.getFloat("D"), responseNumber, ValueTableIdentifier.DUST);
    }

    //TODO possible change the return value to something more sophisticated
    public float[] getValues(String clientName, ValueTableIdentifier k)    {
        return clientsValuesMap.get(clientName).getValues(k);
    }

}
