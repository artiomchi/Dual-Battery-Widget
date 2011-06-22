package org.flexlabs.widgets.dualbattery;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.IBinder;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Artiom Chilaru
 * Date: 20/06/11
 * Time: 21:12
 */
public class BatteryMonitorService extends Service {
    private boolean isRegistered = false;

    public static Integer batteryTab;
    public static Integer batteryDock;
    public static int status = BatteryManager.BATTERY_STATUS_UNKNOWN;
    public static int dockStatus = Constants.DOCK_STATE_UNKNOWN;
    public static boolean hasDock = false;
    public static Date dockLastConnected = null;

    public IBinder onBind(Intent intent) {
        return null;
    }

    private static void processBatteryIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras == null)
            return;

        status = extras.getInt("status", BatteryManager.BATTERY_HEALTH_UNKNOWN);
        batteryTab = extras.getInt("level", -1);
        if (batteryTab < 0)
            batteryTab = null;
        hasDock = extras.containsKey("dock_level");
        if (hasDock) {
            batteryDock = extras.getInt("dock_level", -1);
            if (batteryDock < 0)
                batteryDock = null;
            int oldDockStatus = dockStatus;
            dockStatus = extras.getInt("dock_status", Constants.DOCK_STATE_UNKNOWN);
            if (dockStatus == Constants.DOCK_STATE_UNDOCKED || dockStatus == Constants.DOCK_STATE_UNKNOWN)
                batteryDock = null;
            if (oldDockStatus >= Constants.DOCK_STATE_CHARGING && dockStatus < Constants.DOCK_STATE_CHARGING) {
                dockLastConnected = new Date();
            }
        } else {
            batteryDock = null;
        }
    }

    public static boolean isDockSupported(Context context) {
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        return intent.getExtras().containsKey("dock_status");
    }

    private final BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            processBatteryIntent(intent);

            context.sendBroadcast(new Intent(Constants.ACTION_WIDGET_UPDATE));
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        isRegistered = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(batteryReceiver);
        isRegistered = false;
    }
}
