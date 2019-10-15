package Data;

import Enums.ValueTableIdentifier;

public class SensorValues {

    /*
    Contains all values relevant to an individual sensor sensor as well as calculated values
    Array positions correspond to values
    cur => Current/Latest measurement
    min => Lowest measured value
    max => Highest measured value
    avg => Average of all measurements
    lLm => Lower limit for trigger (decided by controls on the web/mobile client)
    hLm => Higher limit for trigger (decided by controls on the web/mobile client)

    Temp:   cur min max avg lLm hLm
            0   1   2   3   4   5
    Humid:  cur min max avg lLm hLm :: lLm might not be used and stays 0
            6   7   8   9   10  11
    Light:  cur min max avg lLm hLm
            12  13  14  15  16  17
    CO2:    cur min max avg lLm hLm :: lLm might not be used and stays 0
            18  19  20  21  22  23
    Dust:   cur min max avg lLm hLm :: lLm might not be used and stays 0
            24  25  26  27  28  29
     */

    private float[] valueTable;
    private static final int TABLE_ROW_OFFSET = 6;

    private long responseNumber;

    public SensorValues() {
        this.valueTable = new float[5*TABLE_ROW_OFFSET];
        this.responseNumber = 0;
    }

    public void incrementResponseNumber()   {
        this.responseNumber++;
    }

    //Response number used for avg, calculation
    public void putValue(float value, ValueTableIdentifier v)   {

        int offset = v.getValue()*TABLE_ROW_OFFSET;
        this.valueTable[offset] = value;

        if(this.valueTable[1 + offset] > value || this.responseNumber == 1) {
            this.valueTable[1 + offset] = value;
        }

        if(this.valueTable[2 + offset] < value || this.responseNumber == 1) {
            this.valueTable[2 + offset] = value;
        }
        this.valueTable[3 + offset] = this.valueTable[3 + offset] + ((value - this.valueTable[3 + offset])/this.responseNumber);
    }

    public void setValueLowerLimit(float newValue, ValueTableIdentifier v)    {
        valueTable[4 + (v.getValue()*TABLE_ROW_OFFSET)] = newValue;
    }

    public void setValueUpperLimit(float newValue, ValueTableIdentifier v)    {
        valueTable[5 + (v.getValue()*TABLE_ROW_OFFSET)] = newValue;
    }

    public float getLastValue(ValueTableIdentifier v)  {
        return this.valueTable[v.getValue()*TABLE_ROW_OFFSET];
    }

    public float getMinValue(ValueTableIdentifier v)  {
        return this.valueTable[(v.getValue()*TABLE_ROW_OFFSET)+1];
    }

    public float getMaxValue(ValueTableIdentifier v)  {
        return this.valueTable[(v.getValue()*TABLE_ROW_OFFSET)+2];
    }

    public float getAvgValue(ValueTableIdentifier v)  {
        return this.valueTable[(v.getValue()*TABLE_ROW_OFFSET)+3];
    }

    public float getValueLowerLimit(ValueTableIdentifier v)  {
        return this.valueTable[(v.getValue()*TABLE_ROW_OFFSET)+4];
    }

    public float getValueUpperLimit(ValueTableIdentifier v)  {
        return this.valueTable[(v.getValue()*TABLE_ROW_OFFSET)+5];
    }

    public long getResponseNumber() {
        return this.responseNumber;
    }
}
