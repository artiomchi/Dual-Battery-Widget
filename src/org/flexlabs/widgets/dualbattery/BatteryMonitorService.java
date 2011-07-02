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

    private static boolean isPopulated = false;
    public static Integer batteryTab;
    public static Integer batteryDock;
    public static int status = BatteryManager.BATTERY_STATUS_UNKNOWN;
    public static int dockStatus = Constants.DOCK_STATE_UNKNOWN;
    public static boolean hasDock = false;
    public static Date dockLastConnected = null, lastCharged = null;

    public IBinder onBind(Intent intent) {
        return null;
    }

    private static void processBatteryIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras == null)
            return;

        int oldStatus = status;
        status = extras.getInt("status", BatteryManager.BATTERY_STATUS_UNKNOWN);
        if (oldStatus == BatteryManager.BATTERY_STATUS_CHARGING && status != BatteryManager.BATTERY_STATUS_CHARGING)
            lastCharged = new Date();
        batteryTab = extras.getInt("level", -1);
        if (batteryTab < 0)
            batteryTab = null;
        hasDock = extras.containsKey("dock_level");
        if (hasDock) {
            int oldDockStatus = dockStatus;
            dockStatus = extras.getInt("dock_status", Constants.DOCK_STATE_UNKNOWN);
            if (oldDockStatus >= Constants.DOCK_STATE_CHARGING && dockStatus < Constants.DOCK_STATE_CHARGING) {
                dockLastConnected = new Date();
            }
            if (dockStatus >= Constants.DOCK_STATE_CHARGING) {
                batteryDock = extras.getInt("dock_level", -1);
                if (batteryDock <= 0)
                    batteryDock = null;
            }
        } else {
            batteryDock = null;
        }
    }

    public static boolean isDockConnected(Context context) {
        if (!isPopulated) {
            processBatteryIntent(context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED)));
        }
        return dockStatus >= Constants.DOCK_STATE_CHARGING;
    }

    public static boolean isDockSupported(Context context) {
        if (!isPopulated) {
            processBatteryIntent(context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED)));
        }
        return dockStatus != Constants.DOCK_STATE_UNKNOWN;
    }

    private final BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            processBatteryIntent(intent);

            context.sendBroadcast(new Intent(Constants.ACTION_BATTERY_UPDATE));
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        processBatteryIntent(registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED)));
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
