
import org.json.JSONObject;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class SensorConnectionThread extends Thread    {
    private final Socket clientSocket;
    private final SensorServer server;
    private String clientName;

    private long responseNumber;
    private Map<String, Float> values;

    public SensorConnectionThread(SensorServer server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
        this.clientName = clientSocket.getInetAddress().getHostName();
        values = new HashMap<>();
        setupValuesMap();
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
                    handleSensorResponse(json);
                    printHandledValues(json);
                }
            }
            clientSocket.close();

        } catch (IOException ex) {
            System.out.println("Network error: " + ex.getMessage());
        }

        disconnect();
    }

    private void handleSensorResponse(JSONObject json)   {
        this.responseNumber++;

        //Handle temperature values
        calculateValues("T", json);

        //Handle humidity values
        calculateValues("H", json);

        //Handle light values
        calculateValues("L", json);

        //Handle CO2 values
        calculateValues("C", json);

        //Handle dust values
        calculateValues("D", json);
    }

    // k determines which type of values should be calculated
    // T for temp, H for humidity, L for light, C for CO2, D for Dust
    private void calculateValues(String k, JSONObject json)  {
        float f = json.getFloat(k);
        values.put(k, f);
        if(values.get(k + "_MIN") > f || responseNumber == 1) {
            values.put(k + "_MIN", f);
        }
        if (values.get(k + "_MAX") < f || responseNumber == 1) {
            values.put(k + "_MAX", f);
        }
        values.put(k + "_AVG", (values.get(k + "_AVG") + ((f - values.get(k + "_AVG"))/responseNumber)));
    }

    private void printHandledValues(JSONObject json)    {
        System.out.println(" ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ‾ ");
        System.out.println("-=-" + this.clientName + " | " + json.getString("N") + "-=-");
        System.out.println("Response number: \t" + this.responseNumber);
        System.out.println("Temperature: \t\t" + formatValues("T"));
        System.out.println("Humidity: \t\t\t" + formatValues("H"));
        System.out.println("Light: \t\t\t\t" + formatValues("L"));
        System.out.println("CO2: \t\t\t\t" + formatValues("C"));
        System.out.println("Dust: \t\t\t\t" + formatValues("D"));
        System.out.println("_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _");
    }

    private String formatValues(String k)    {
        return String.format("%.02f", values.get(k)) +
                "\tMin: " + String.format("%.02f", values.get(k +"_MIN")) +
                "\tMax: " + String.format("%.02f", values.get(k + "_MAX")) +
                "\tAvg: " + String.format("%.02f", values.get(k + "_AVG"));
    }

    private void setupValuesMap()   {
        values.put("T", 0f);
        values.put("T_MIN", 0f);
        values.put("T_MAX", 0f);
        values.put("T_AVG", 0f);

        values.put("H", 0f);
        values.put("H_MIN", 0f);
        values.put("H_MAX", 0f);
        values.put("H_AVG", 0f);

        values.put("L", 0f);
        values.put("L_MIN", 0f);
        values.put("L_MAX", 0f);
        values.put("L_AVG", 0f);

        values.put("C", 0f);
        values.put("C_MIN", 0f);
        values.put("C_MAX", 0f);
        values.put("C_AVG", 0f);

        values.put("D", 0f);
        values.put("D_MIN", 0f);
        values.put("D_MAX", 0f);
        values.put("D_AVG", 0f);
    }

    public String getClientName()  {
        return this.clientName;
    }

    private void disconnect()   {
        server.disconnectClient(this.clientName);
    }
}
