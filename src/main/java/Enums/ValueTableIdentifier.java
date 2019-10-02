package Enums;

public enum ValueTableIdentifier {
    TEMP(0),
    HUMIDITY(1),
    LIGHT(2),
    CO2(3),
    DUST(4);

    private final int value;

    ValueTableIdentifier(final int newValue)   {
        value = newValue;
    }

    public int getValue()   {
        return value;
    }
}
