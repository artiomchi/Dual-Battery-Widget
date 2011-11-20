package org.flexlabs.widgets.dualbattery.service;

import android.app.Notification;
import android.content.Context;
import org.flexlabs.widgets.dualbattery.R;

/**
 * Created by IntelliJ IDEA.
 * User: ArtiomChi
 * Date: 13/11/11
 * Time: 14:32
 */
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
