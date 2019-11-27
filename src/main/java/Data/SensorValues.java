package Data;

import Enums.ValueTableIdentifier;

import java.util.Arrays;

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

    Array positions:
    Temp:   cur min max avg lLm hLm
            0   1   2   3   4   5
    Humid:  cur min max avg lLm hLm :: lLm, hLm not used and stays -1000
            6   7   8   9   10  11
    Light:  cur min max avg lLm hLm :: lLm, hLm not used and stays -1000
            12  13  14  15  16  17
    CO2:    cur min max avg lLm hLm :: lLm might not used and stays -1000
            18  19  20  21  22  23
    Dust:   cur min max avg lLm hLm :: lLm might not used and stays -1000
            24  25  26  27  28  29
     */

    private float[] valueTable;
    private static final int TABLE_ROW_OFFSET = 6;

    /*Array of the number of measurement of each value type, used for calculating exponential moving average
            tmp hum lgt co2 dst
            0   1   2   3   4
     */
    private int[] avgCount;
    //average limit for weighted rolling average
    private static final int AVERAGE_MEASUREMENT_LIMIT = 120;

    public SensorValues() {
        this.valueTable = new float[5*TABLE_ROW_OFFSET];
        this.avgCount = new int[5];
        Arrays.fill(valueTable, -1000f); //start array with impossibly low number
        Arrays.fill(avgCount, 0);
    }

    //Response number used for avg, calculation
    public void putValue(float value, ValueTableIdentifier v)   {

        if(value > -100) { //Discard impossibly low measurement. Lower than -100 in this case

            int offset = v.getValue() * TABLE_ROW_OFFSET;
            this.valueTable[offset] = value;
            incrementAvgCount(v);

            if(this.valueTable[1 + offset] > value || getAvgCount(v) == 1) {
                this.valueTable[1 + offset] = value;
            }

            if(this.valueTable[2 + offset] < value || getAvgCount(v) == 1) {
                this.valueTable[2 + offset] = value;
            }

            //Calculate exponential moving average
            float a;
            if(getAvgCount(v) < AVERAGE_MEASUREMENT_LIMIT)  {
                a = 2 / (1 + (float)getAvgCount(v));
            }
            else {
                a = 2 / (1 + (float)AVERAGE_MEASUREMENT_LIMIT);
            }

            this.valueTable[3 + offset] = (value * a) + (this.valueTable[3 + offset] * (1 - a));
        }
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

    private void incrementAvgCount(ValueTableIdentifier v) {
        avgCount[v.getValue()]++;
    }

    private int getAvgCount(ValueTableIdentifier v)    {
        return this.avgCount[v.getValue()];
    }
}
