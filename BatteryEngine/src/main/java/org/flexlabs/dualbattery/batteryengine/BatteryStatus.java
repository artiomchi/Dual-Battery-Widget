package org.flexlabs.dualbattery.batteryengine;

public enum BatteryStatus {
    NotCharging(0),
    Discharging (1),
    Charging (2),
    Full (3),
    Disconnected (4),
    Unknown (100);

    private final int intValue;
    BatteryStatus(int value) {
        this.intValue = value;
    }

    public int getIntValue() {
        return intValue;
    }

    public int getString() {
        switch (this) {
            case NotCharging:
                return R.string.battery_status_notcharging;
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
            this == NotCharging ||
            this == Discharging ||
            this == Charging ||
            this == Full;
    }
}
