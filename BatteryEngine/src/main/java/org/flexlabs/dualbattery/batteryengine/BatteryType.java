package org.flexlabs.dualbattery.batteryengine;

public enum BatteryType {
    Main (1),
    AsusDock (2),
    AsusPad (3);

    private final int intValue;
    BatteryType(int value) {
        this.intValue = value;
    }

    public int getIntValue() {
        return intValue;
    }

    public int getString() {
        switch (this) {
            case Main:
                return R.string.battery_type_main;
            case AsusDock:
                return R.string.battery_type_asus_dock;
            case AsusPad:
                return R.string.battery_type_asus_pad;
        }
        return 0;
    }
}
