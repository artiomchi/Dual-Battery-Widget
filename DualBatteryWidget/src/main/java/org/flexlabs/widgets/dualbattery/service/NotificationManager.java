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

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.SystemService;
import org.flexlabs.dualbattery.batteryengine.BatteryStatus;
import org.flexlabs.widgets.dualbattery.BatteryLevelMonitor;
import org.flexlabs.widgets.dualbattery.Constants;
import org.flexlabs.widgets.dualbattery.R;
import org.flexlabs.widgets.dualbattery.app.BatteryHistoryActivity;
import org.flexlabs.widgets.dualbattery.app.SettingsContainer;

@EBean
public class NotificationManager implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final int NOTIFICATION_DOCK = 1;
    private static final int NOTIFICATION_PAD = 2;

    @SystemService android.app.NotificationManager mNotificationManager;
    @Bean BatteryLevelMonitor batteryMonitor;
    Context mContext;
    private CharSequence title;
    private boolean enabled;

    public NotificationManager(Context context) {
        mContext = context;
        title = context.getString(R.string.app_name);

        enabled = new SettingsContainer(context, this).isShowNotificationIcon();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (Constants.SETTING_NOTIFICATION_ICON.equals(key)) {
            enabled = new SettingsContainer(mContext).isShowNotificationIcon();
            if (enabled) {
                if (batteryMonitor.dockBattery != null && batteryMonitor.dockBattery.getStatus().isEnabled())
                    updateDock(batteryMonitor.dockBattery.getLevel(), batteryMonitor.dockBattery.getStatus() == BatteryStatus.Charging);
                else
                    hideDock();

                if (batteryMonitor.padBattery != null && batteryMonitor.padBattery.getStatus().isEnabled())
                    updatePad(batteryMonitor.padBattery.getLevel(), batteryMonitor.padBattery.getStatus() == BatteryStatus.Charging);
                else
                    hidePad();
            }
        }
    }

    public void updateDock(int dockLevel, boolean charging) {
        update(NOTIFICATION_DOCK, "Dock", dockLevel, charging);
    }

    public void updatePad(int padLevel, boolean charging) {
        update(NOTIFICATION_PAD, "Pad", padLevel, charging);
    }

    private void update(int notificationType, String batteryName, int dockLevel, boolean charging) {
        if (!enabled) {
            return;
        }

        int icon = charging
                ? R.drawable.stat_sys_battery_charge // local copy of android.R.drawable.stat_sys_battery_charge
                : R.drawable.stat_sys_battery;       // local copy of android.R.drawable.stat_sys_battery

        // Notification Builder straight up refuses to create a notification without a ticker :(
        /*Notification notification = new Notification.Builder(mContext)
                .setSmallIcon(icon, dockLevel)
                .setContentTitle(title)
                .setContentText("Dock battery level: " + dockLevel + "%")
                //.setNumber(dockLevel)
                .setAutoCancel(false)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setTicker(null)
                .getNotification();*/
        
        Notification notification = new Notification(icon, null, System.currentTimeMillis());
        notification.iconLevel = dockLevel;
        notification.flags =
            Notification.FLAG_ONGOING_EVENT |
            Notification.FLAG_ONLY_ALERT_ONCE |
            Notification.FLAG_NO_CLEAR;
        Intent intent = new Intent(mContext, BatteryHistoryActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 1, intent, 0);
        notification.setLatestEventInfo(mContext, title, batteryName + " battery level: " + dockLevel + "%", contentIntent);

        notification.tickerText = null;
        notification.contentView.setInt(android.R.id.icon, "setImageLevel", dockLevel);
        mNotificationManager.notify(notificationType, notification);
    }
    
    public void hideDock() {
        mNotificationManager.cancel(NOTIFICATION_DOCK);
    }

    public void hidePad() {
        mNotificationManager.cancel(NOTIFICATION_PAD);
    }
}
