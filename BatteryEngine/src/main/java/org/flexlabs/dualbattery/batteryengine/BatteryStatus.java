package org.flexlabs.dualbattery.batteryengine;

public enum BatteryStatus {
    Discharging,
    Charging,
    Full,
    Disconnected,
    Unknown;

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
}
