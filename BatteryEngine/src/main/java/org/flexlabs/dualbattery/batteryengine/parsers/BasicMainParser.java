package org.flexlabs.dualbattery.batteryengine.parsers;

import android.content.Intent;
import android.os.BatteryManager;
import android.os.Bundle;

import org.flexlabs.dualbattery.batteryengine.BatteryLevel;
import org.flexlabs.dualbattery.batteryengine.BatteryStatus;
import org.flexlabs.dualbattery.batteryengine.BatteryType;

public class BasicMainParser implements IParser {
    @Override
    public BatteryLevel parseBatteryLevel(Intent intent) {
        if (!Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction()))
            return null;

        Bundle extras = intent.getExtras();
        if (extras == null)
            return null;

        int status = extras.getInt("status");
        int level = extras.getInt("level");

        return new BatteryLevel(BatteryType.Main, getStatus(status), level);
    }

    private BatteryStatus getStatus(int status) {
        switch (status) {
            case  BatteryManager.BATTERY_STATUS_CHARGING:
                return BatteryStatus.Charging;
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                return BatteryStatus.Discharging;
            case BatteryManager.BATTERY_STATUS_FULL:
                return BatteryStatus.Full;
            default:
                return BatteryStatus.Unknown;
        }
    }
}
