package org.flexlabs.widgets.dualbattery.service;

import android.os.BatteryManager;
import android.os.Bundle;
import org.flexlabs.widgets.dualbattery.Constants;

/**
 * Created by IntelliJ IDEA.
 * User: ArtiomChi
 * Date: 12/11/11
 * Time: 16:51
 */
public class BatteryLevel {
    private boolean _dockFriendly;
    private int _status;
    private int _level;
    private int _dock_status;
    private int _dock_level;
    
    private BatteryLevel() { }
    
    public static BatteryLevel parse(Bundle extras) {
        if (extras == null)
            return null;

        BatteryLevel level = new BatteryLevel();
        level._status = extras.getInt("status", BatteryManager.BATTERY_STATUS_UNKNOWN);
        level._level = extras.getInt("level");
        level._dock_status = extras.getInt("dock_status", Constants.DOCK_STATE_UNKNOWN);
        level._dock_level = extras.getInt("dock_level", -1);
        level._dockFriendly = extras.containsKey("dock_level");
        return level;
    }
    
    public boolean isDifferent(BatteryLevel level) {
        return
            level == null ||
            level._level != _level ||
            level._status != _status ||
            level._dock_status != _dock_status ||
            level._dock_level != _dock_level;
    }

    public boolean is_dockFriendly() {
        return _dockFriendly;
    }
    
    public boolean is_dockConnected() {
        return (_dockFriendly && _dock_status >= Constants.DOCK_STATE_CHARGING && _dock_level >= 0);
    }

    public int get_status() {
        return _status;
    }

    public int get_level() {
        return _level;
    }

    public int get_dock_status() {
        if (!_dockFriendly)
            return Constants.DOCK_STATE_UNKNOWN;
        return _dock_status;
    }

    public Integer get_dock_level() {
        if (is_dockConnected())
            return _dock_level;
        return null;
    }
}
