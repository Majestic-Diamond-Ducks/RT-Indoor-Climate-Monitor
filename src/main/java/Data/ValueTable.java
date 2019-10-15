package Data;

import Enums.ValueTableIdentifier;

public class ValueTable {

    //TODO rename to something amongst the lines of "Sensor values"

    /*
    Contains all values sent by the sensor as well as calculated values
    Array positions correspond to values
    Temp:   cur min max avg
            0   1   2   3
    Humid:  cur min max avg
            4   5   6   7
    Light:  cur min max avg
            8   9   10  11
    CO2:    cur min max avg
            12  13  14  15
    Dust:   cur min max avg
            16  17  18  19

     */

    private float [] values;
    private long responseNumber;

    public ValueTable() {
        this.values = new float[20];
        this.responseNumber = 0;
    }

    public void incrementResponseNumber()   {
        this.responseNumber++;
    }

    //Response number used for avg, calculation
    public void putValue(float value, ValueTableIdentifier v)   {

        int offset = v.getValue()*4;
        this.values[offset] = value;

        if(this.values[1 + offset] > value || this.responseNumber == 1) {
            this.values[1 + offset] = value;
        }

        if(this.values[2 + offset] < value || this.responseNumber == 1) {
            this.values[2 + offset] = value;
        }
        this.values[3 + offset] = this.values[3 + offset] + ((value - this.values[3 + offset])/this.responseNumber);
    }

    public float getLast(ValueTableIdentifier v)  {
        return this.values[v.getValue()*4];
    }

    public float getMin(ValueTableIdentifier v)  {
        return this.values[(v.getValue()*4)+1];
    }

    public float getMax(ValueTableIdentifier v)  {
        return this.values[(v.getValue()*4)+2];
    }

    public float getAvg(ValueTableIdentifier v)  {
        return this.values[(v.getValue()*4)+3];
    }

    public long getResponseNumber() {
        return this.responseNumber;
    }
}
