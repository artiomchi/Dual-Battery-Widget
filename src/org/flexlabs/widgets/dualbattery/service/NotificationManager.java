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
import android.content.Context;
import org.flexlabs.widgets.dualbattery.R;

public class NotificationManager {
    private static final int NOTIFICATION_DOCK = 1;

    private android.app.NotificationManager mNotificationManager;
    private Context mContext;
    private CharSequence title;

    public NotificationManager(Context context) {
        mNotificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mContext = context;
        title = context.getString(R.string.app_name);
    }
    
    public void update(int dockLevel, boolean charging) {
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
        notification.setLatestEventInfo(mContext, title, "Dock battery level: " + dockLevel + "%", null);

        notification.tickerText = null;
        notification.contentView.setInt(android.R.id.icon, "setImageLevel", dockLevel);
        mNotificationManager.notify(NOTIFICATION_DOCK, notification);
    }
    
    public void hide() {
        mNotificationManager.cancel(NOTIFICATION_DOCK);
    }
}
