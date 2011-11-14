package org.flexlabs.widgets.dualbattery.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;
import org.flexlabs.widgets.dualbattery.Constants;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: ArtiomChi
 * Date: 20/06/11
 * Time: 21:12
 */
public class BatteryMonitorService extends Service {
    private static boolean isPopulated = false;
    public static final String EXTRA_WIDGET_IDS = "widgetIds";
    private static NotificationManager mNotificationManager;

    public static BatteryLevel level;
    public static boolean screenOff = false;
    public static Date dockLastConnected = null, lastCharged = null;
    public static Integer lastDockLevel = null;

    public IBinder onBind(Intent intent) {
        return null;
    }

    private static void processBatteryIntent(final Context context, Intent intent) {
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
            BatteryLevel newLevel = BatteryLevel.parse(intent.getExtras());
            if (newLevel == null)
                return;
            
            if (newLevel.isDifferent(level))
                newData = true;

            if ((level == null || level.get_status() == BatteryManager.BATTERY_STATUS_CHARGING) &&
                newLevel.get_status() != BatteryManager.BATTERY_STATUS_CHARGING)
                lastCharged = new Date();
            
            if (newLevel.is_dockFriendly() && level != null && level.get_dock_status() >= Constants.DOCK_STATE_CHARGING &&
                newLevel.get_dock_status() < Constants.DOCK_STATE_CHARGING)
                dockLastConnected = new Date();

            if (newLevel.get_dock_level() != null)
                lastDockLevel = newLevel.get_dock_level();

            level = newLevel;
        }
        
        if (!newData || level == null)
            return;

        // Running database operation away from the UI thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                BatteryLevelAdapter.Entry entry = new BatteryLevelAdapter.Entry(
                    level.get_status(),
                    level.get_level(),
                    level.get_dock_status(),
                    level.get_dock_level(),
                    screenOff);
        
                BatteryLevelAdapter adapter = new BatteryLevelAdapter(context);
                adapter.open();
                adapter.insertEntry(entry);
                adapter.close();

                if (level.is_dockConnected())
                    mNotificationManager.update(level.get_dock_level());
                else
                    mNotificationManager.hide();
                WidgetUpdater.updateAllWidgets(context, level, null);
            }
        }).run(); // TODO: Change this to .start();
    }

    /**
     * A static method to find out if the dock is available on this device
     * @param context some context used to get the status
     * @return true if dock is supported on this device
     */
    public static boolean isDockSupported(Context context) {
        try {
            if (!isPopulated) {
                processBatteryIntent(context, context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED)));
            }
        } catch (Exception ignored) { }
        return level != null && level.is_dockFriendly();
    }

    private final BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            processBatteryIntent(context, intent);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = new NotificationManager(this);

        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
        //registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_DOCK_EVENT));
        processBatteryIntent(this, registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED)));
        isPopulated = true;
    }
    
    private void processStartIntent(Intent intent) {
        if (intent == null)
            return;
        final int[] widgetIds = intent.getIntArrayExtra(EXTRA_WIDGET_IDS);
        if (widgetIds != null) {
            // Update our widgets on the non UI thread
            new Thread(new Runnable() {
                @Override
                public void run() {
                    WidgetUpdater.updateAllWidgets(BatteryMonitorService.this, level, widgetIds);
                }
            }).start();
        }
    }

    @Override
    public void onStart(Intent intent, int startId) { // For compatibility with android 1.6
        processStartIntent(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        processStartIntent(intent);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(batteryReceiver);
        isPopulated = false;
    }
}
