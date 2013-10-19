package org.flexlabs.dualbattery.batteryengine.parsers;

import android.content.Intent;

import org.flexlabs.dualbattery.batteryengine.BatteryLevel;

public interface IParser {
    BatteryLevel parseBatteryLevel(Intent intent);
}
