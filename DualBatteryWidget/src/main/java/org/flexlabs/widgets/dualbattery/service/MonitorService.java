/*
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

package org.flexlabs.widgets.dualbattery.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;
import org.flexlabs.dualbattery.batteryengine.BatteryStatus;
import org.flexlabs.widgets.dualbattery.BatteryLevelMonitor;
import org.flexlabs.widgets.dualbattery.Constants;
import org.flexlabs.widgets.dualbattery.BatteryWidgetUpdater;
import org.flexlabs.widgets.dualbattery.storage.DualBatteryDao;

@EService
public class MonitorService extends Service implements BatteryLevelMonitor.OnBatteriesUpdatedListener {
    @Bean DualBatteryDao batteryDao;
    @Bean BatteryLevelMonitor batteryMonitor;
    @Bean NotificationManager mNotificationManager;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
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
        batteryMonitor.stopMonitoring();
        unregisterReceiver(screenReceiver);
    }

    @AfterInject
    public void afterInject() {
        batteryMonitor.setOnBatteriesUpdatedListener(this);
        batteryMonitor.startMonitoring();

        registerReceiver(screenReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
        registerReceiver(screenReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
    }

    Boolean screenOn = null;
    BroadcastReceiver screenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean newScreenOn = Intent.ACTION_SCREEN_ON.equals(intent.getAction());
            if (screenOn == null || screenOn != newScreenOn) {
                screenOn = newScreenOn;
                batteryDao.addScreenStatus(screenOn);
            }
        }
    };

    @Override
    public void batteriesUpdated() {
        updateWidgets(null);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            if (batteryMonitor.dockBattery != null)
                mNotificationManager.updateDock(
                        batteryMonitor.dockBattery.getLevel(),
                        batteryMonitor.dockBattery.getStatus() == BatteryStatus.Charging);
            else
                mNotificationManager.hideDock();

            if (batteryMonitor.padBattery != null)
                mNotificationManager.updatePad(
                        batteryMonitor.padBattery.getLevel(),
                        batteryMonitor.padBattery.getStatus() == BatteryStatus.Charging);
            else
                mNotificationManager.hidePad();
        }
    }

    private void processStartIntent(Intent intent) {
        if (intent == null)
            return;
        int[] widgetIds = intent.getIntArrayExtra(Constants.EXTRA_WIDGET_IDS);
        if (widgetIds != null && widgetIds.length > 0)
            updateWidgets(widgetIds);
    }

    @Background
    public void updateWidgets(int[] widgetIds) {
        try {
            BatteryWidgetUpdater.updateAllWidgets(MonitorService.this, batteryMonitor.currentBatteryLevels, widgetIds);
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }
}
