package org.flexlabs.dualbattery.batteryengine;

public enum BatteryType {
    Main,
    AsusDock,
    AsusPad;

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
