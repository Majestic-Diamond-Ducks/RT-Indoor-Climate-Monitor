package Data;

import Enums.ValueTableIdentifier;

public class ValueTable {

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

    public ValueTable() {
        values = new float[20];
    }

    //Response number used for avg, calculation
    public void putValue(float value, long responseNumber, ValueTableIdentifier k)   {
        int offset = k.getValue()*4;

        values[offset] = value;

        if(values[1 + offset] > value || responseNumber == 1) {
            values[1 + offset] = value;
        }

        if(values[2 + offset] < value || responseNumber == 1) {
            values[2 + offset] = value;
        }
        values[3 + offset] = values[3 + offset] + ((value - values[3 + offset])/responseNumber);
    }

    //Returns a 4 long sub-array with values as: current = [0], min = [1], max = [2], avg = [3]
    public float[] getValues(ValueTableIdentifier k)  {
        float [] returnValues = new float[4];
        int beginningOffset = k.getValue()*4;

        System.arraycopy(values, beginningOffset, returnValues,0, returnValues.length);

        return returnValues;
    }

    public void debugPrintTable()    {
        int i = 0;
        for(float f : values) {
            System.out.print(i + " : "+ f + " \t|\t");
            i++;
            if(i%4 == 0)    {
                System.out.println("\n=========================");
            }
        }
    }
}
