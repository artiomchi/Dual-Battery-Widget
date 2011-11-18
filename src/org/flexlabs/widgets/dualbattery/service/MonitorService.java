package org.flexlabs.widgets.dualbattery.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import org.flexlabs.widgets.dualbattery.BatteryLevel;
import org.flexlabs.widgets.dualbattery.Constants;
import org.flexlabs.widgets.dualbattery.BatteryWidgetUpdater;

/**
 * Created by IntelliJ IDEA.
 * User: ArtiomChi
 * Date: 20/06/11
 * Time: 21:12
 */
public class MonitorService extends Service {
    private IntentReceiver batteryReceiver;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        batteryReceiver = new IntentReceiver(this);
        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_DOCK_EVENT));
        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }
    
    private void processStartIntent(Intent intent) {
        if (intent == null)
            return;
        final int[] widgetIds = intent.getIntArrayExtra(Constants.EXTRA_WIDGET_IDS);
        if (widgetIds != null) {
            // Update our widgets on the non UI thread
            new Thread(new Runnable() {
                @Override
                public void run() {
                    BatteryWidgetUpdater.updateAllWidgets(MonitorService.this, BatteryLevel.getCurrent(), widgetIds);
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
    }
}
