package org.flexlabs.widgets.dualbattery;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: ArtiomChi
 * Date: 12/11/11
 * Time: 16:51
 *
 * Copyright 2011 Artiom Chilaru (http://flexlabs.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class BatteryLevel {
    private static BatteryLevel _instance;
    public static Date dockLastConnected = null, lastCharged = null;
    public static Integer lastDockLevel = null;

    private boolean _dockFriendly;
    private int _status;
    private int _level;
    private int _dock_status;
    private int _dock_level;
    
    private BatteryLevel() { }
    
    public static void update(BatteryLevel level) { _instance = level; }
    public static BatteryLevel getCurrent() {
        if (_instance == null) {
            Intent intent = BatteryApplication.getInstance().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            _instance = parse(intent.getExtras());
        }
        return _instance;
    }
    
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
    
    public void undock() {
        _dock_status = Constants.DOCK_STATE_UNDOCKED;
    }
    
    public void dock(int dock_level) {
        _dock_status = Constants.DOCK_STATE_DOCKED;
        _dock_level = dock_level;
    }
}
