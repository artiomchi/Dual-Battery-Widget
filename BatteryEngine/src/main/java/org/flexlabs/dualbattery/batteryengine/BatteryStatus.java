package org.flexlabs.dualbattery.batteryengine;

public enum BatteryStatus {
    Discharging (0),
    Charging (1),
    Full (2),
    Disconnected (3),
    Unknown (4);

    private final int intValue;
    BatteryStatus(int value) {
        this.intValue = value;
    }

    public int getIntValue() {
        return intValue;
    }

    public int getString() {
        switch (this) {
            case Discharging:
                return R.string.battery_status_discharging;
            case Charging:
                return R.string.battery_status_charging;
            case Full:
                return R.string.battery_status_full;
            case Disconnected:
                return R.string.battery_status_disconnected;
            case Unknown:
                return R.string.battery_status_unknown;
        }
        return 0;
    }

    public boolean isEnabled() {
        return
            this == Discharging ||
            this == Charging ||
            this == Full;
    }
}
