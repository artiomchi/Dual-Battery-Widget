package org.flexlabs.widgets.dualbattery;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

/**
 * Created by IntelliJ IDEA.
 * User: Artiom Chilaru
 * Date: 13/06/11
 * Time: 21:56
 */
public class BatteryApplication extends Application {
    private static boolean isRegistered = false;

    public static final int DOCK_STATE_UNKNOWN = 0;
    public static final int DOCK_STATE_UNDOCKED = 1;
    public static final int DOCK_STATE_CHARGING = 2;
    public static final int DOCK_STATE_DOCKED = 3;
    public static final int DOCK_STATE_DISCHARGING = 4;
    public static final String ACTION_WIDGET_UPDATE = "org.flexlabs.action.WIDGET_UPDATE";

    public static Integer batteryTab;
    public static Integer batteryDock;
    public static int status = BatteryManager.BATTERY_STATUS_UNKNOWN;
    public static int dockStatus = DOCK_STATE_UNKNOWN;

    private static final BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                batteryTab = intent.getIntExtra("level", -1);
                if (batteryTab < 0)
                    batteryTab = null;
                batteryDock = intent.getIntExtra("dock_level", -1);
                if (batteryDock < 0)
                    batteryDock = null;
                status = intent.getIntExtra("status", BatteryManager.BATTERY_HEALTH_UNKNOWN);
                dockStatus = intent.getIntExtra("dock_status", DOCK_STATE_UNKNOWN);
                if (dockStatus == DOCK_STATE_UNDOCKED || dockStatus == DOCK_STATE_UNKNOWN)
                    batteryDock = null;

                context.sendBroadcast(new Intent(ACTION_WIDGET_UPDATE));

            } else if (Intent.ACTION_DOCK_EVENT.equals(intent.getAction())) {
                dockStatus = intent.getIntExtra("status", 0);
                if (dockStatus == Intent.EXTRA_DOCK_STATE_UNDOCKED) {
                    dockStatus = DOCK_STATE_UNDOCKED;
                    batteryDock = null;
                } else {
                    dockStatus = DOCK_STATE_DOCKED;
                }

                context.sendBroadcast(new Intent(ACTION_WIDGET_UPDATE));
            }
        }
    };

    public static void registerReceiver(Context context) {
        if (!isRegistered) {
            Context appContext = context.getApplicationContext();
            appContext.registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            appContext.registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_DOCK_EVENT));
            isRegistered = true;
        }
    }

    public static void unregisterReceiver(Context context) {
        if (isRegistered) {
            context.getApplicationContext().unregisterReceiver(batteryReceiver);
            isRegistered = false;
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
