package org.flexlabs.widgets.dualbattery;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Artiom Chilaru
 * Date: 20/06/11
 * Time: 21:12
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
public class BatteryMonitorService extends Service {

    private static boolean isPopulated = false;
    public static Integer batteryTab = null, batteryDock = null;
    public static int status = BatteryManager.BATTERY_STATUS_UNKNOWN;
    public static int dockStatus = Constants.DOCK_STATE_UNKNOWN;
    public static boolean hasDock = false, screenOff = false;
    public static Date dockLastConnected = null, lastCharged = null;

    public IBinder onBind(Intent intent) {
        return null;
    }

    private static void processBatteryIntent(Context context, Intent intent) {
        boolean newData = false;

        if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
            if (!screenOff)
                newData = true;
            screenOff = true;

        } else if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
            if (screenOff)
                newData = true;
            screenOff = false;

        } else if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            if (extras == null)
                return;

            int _status = extras.getInt("status", BatteryManager.BATTERY_STATUS_UNKNOWN);
            int _level = extras.getInt("level");
            int _dock_status = extras.getInt("dock_status", Constants.DOCK_STATE_UNKNOWN);
            int _dock_level = extras.getInt("dock_level", -1);

            if (_status != status || batteryTab == null || batteryDock == null || _level != batteryTab || _dock_status != dockStatus || _dock_level != batteryDock) {
                newData = true;
            }

            if (status == BatteryManager.BATTERY_STATUS_CHARGING && _status != status)
                lastCharged = new Date();
            status = _status;
            batteryTab = _level;

            hasDock = extras.containsKey("dock_level");
            if (hasDock) {
                if (dockStatus >= Constants.DOCK_STATE_CHARGING && _dock_status < Constants.DOCK_STATE_CHARGING)
                    dockLastConnected = new Date();
                dockStatus = _dock_status;

                if (dockStatus >= Constants.DOCK_STATE_CHARGING) {
                    batteryDock = _dock_level >= 0 ? _dock_level : null;
                }
            }
        }

        if (!newData || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
            return;

        BatteryLevelAdapter.Entry entry = new BatteryLevelAdapter.Entry(
            status,
            batteryTab,
            dockStatus,
            batteryDock,
            screenOff);

        BatteryLevelAdapter adapter = new BatteryLevelAdapter(context);
        adapter.open();
        adapter.insertEntry(entry);
        adapter.close();
    }

    public static boolean isDockConnected(Context context) {
        try {
            if (!isPopulated) {
                processBatteryIntent(context, context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED)));
            }
        } catch (Exception ignored) { }
        return dockStatus >= Constants.DOCK_STATE_CHARGING;
    }

    public static boolean isDockSupported(Context context) {
        try {
            if (!isPopulated) {
                processBatteryIntent(context, context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED)));
            }
        } catch (Exception ignored) { }
        return dockStatus != Constants.DOCK_STATE_UNKNOWN;
    }

    private final BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            processBatteryIntent(context, intent);

            context.sendBroadcast(new Intent(Constants.ACTION_BATTERY_UPDATE));
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
        processBatteryIntent(this, registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED)));
        isPopulated = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(batteryReceiver);
        isPopulated = false;
    }
}
