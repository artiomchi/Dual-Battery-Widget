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
import org.flexlabs.dualbattery.batteryengine.BatteryMonitor;
import org.flexlabs.dualbattery.batteryengine.BatteryStatus;
import org.flexlabs.dualbattery.batteryengine.BatteryType;
import org.flexlabs.widgets.dualbattery.BatteryLevel;
import org.flexlabs.widgets.dualbattery.Constants;
import org.flexlabs.widgets.dualbattery.BatteryWidgetUpdater;
import org.flexlabs.widgets.dualbattery.storage.DualBatteryDao;

import java.util.List;

@EService
public class MonitorService extends Service implements BatteryMonitor.OnBatteryStatusUpdatedListener {
    BatteryMonitor batteryMonitor;
    @Bean DualBatteryDao batteryDao;
    @Bean NotificationManager mNotificationManager;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        batteryMonitor = new BatteryMonitor(this);
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
        batteryMonitor.setBatteryUpdatedListener(this);
        batteryMonitor.startMonitoring(false);

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

    private List<org.flexlabs.dualbattery.batteryengine.BatteryLevel> currentBatteryLevels = null;
    @Override
    public void batteryLevelsUpdated(List<org.flexlabs.dualbattery.batteryengine.BatteryLevel> batteryLevels) {
        org.flexlabs.dualbattery.batteryengine.BatteryLevel dockLevel = null;
        for (org.flexlabs.dualbattery.batteryengine.BatteryLevel level : batteryLevels) {
            if (level.getType() == BatteryType.AsusDock)
                dockLevel = level;
            boolean updated = false;
            if (currentBatteryLevels != null) {
                boolean foundMatch = false;
                for (org.flexlabs.dualbattery.batteryengine.BatteryLevel currentLevel : currentBatteryLevels) {
                    if (currentLevel.getType() != level.getType())
                        continue;
                    foundMatch = true;
                    if (currentLevel.getStatus() != level.getStatus() || currentLevel.getLevel() != level.getLevel())
                        updated = true;
                }
                if (!foundMatch)
                    updated = true;
            } else {
                updated = true;
            }

            if (updated) {
                batteryDao.addBatteryLevel(level.getType(), level.getStatus().getIntValue(), level.getLevel());
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                    if (dockLevel != null)
                        mNotificationManager.update(
                                dockLevel.getLevel(),
                                dockLevel.getStatus() == BatteryStatus.Charging);
                    else
                        mNotificationManager.hide();
                }
            }
        }
        currentBatteryLevels = batteryLevels;
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
            BatteryWidgetUpdater.updateAllWidgets(MonitorService.this, BatteryLevel.getCurrent(), widgetIds);
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }
}
