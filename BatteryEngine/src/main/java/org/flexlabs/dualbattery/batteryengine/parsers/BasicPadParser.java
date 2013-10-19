package org.flexlabs.dualbattery.batteryengine.parsers;

import android.content.Intent;
import android.os.Bundle;

import org.flexlabs.dualbattery.batteryengine.BatteryLevel;
import org.flexlabs.dualbattery.batteryengine.BatteryStatus;
import org.flexlabs.dualbattery.batteryengine.BatteryType;

public class BasicPadParser implements IParser {
    @Override
    public BatteryLevel parseBatteryLevel(Intent intent) {
        if (!Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction()))
            return null;

        Bundle extras = intent.getExtras();
        if (extras == null)
            return null;

        if (extras.containsKey("pad_status"))
            return null;

        int status = extras.getInt("pad_status");
        int level = extras.getInt("pad_level");

        return new BatteryLevel(BatteryType.AsusDock, getStatus(status), level);
    }

    public static BatteryStatus getStatus(int status) {
        switch (status) {
            case 2:
                return BatteryStatus.Charging;
            case 4:
                return BatteryStatus.Discharging;
            //case BatteryManager.BATTERY_STATUS_FULL:
            //    return BatteryStatus.Full;
            case 1:
                return BatteryStatus.Disconnected;
            //case 3:
            //    return "Docked"
            default:
                return BatteryStatus.Unknown;
        }
    }
}
