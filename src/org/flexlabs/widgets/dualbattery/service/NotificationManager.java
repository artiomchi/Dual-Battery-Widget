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
    private Notification mNotification;
    private Context mContext;
    private CharSequence title;

    public NotificationManager(Context context) {
        mNotificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mContext = context;
        title = context.getString(R.string.app_name);
        //Intent notificationIntent = new Intent(context, BatteryMonitorService.class);
        //PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        mNotification = new Notification(
                R.drawable.icon,
                null,
                System.currentTimeMillis());
        mNotification.setLatestEventInfo(context, title, null, null);
        mNotification.flags =
                Notification.FLAG_ONGOING_EVENT |
                Notification.FLAG_ONLY_ALERT_ONCE |
                Notification.FLAG_NO_CLEAR;
    }
    
    public void update(int dockLevel) {
        mNotification.setLatestEventInfo(mContext, title, dockLevel + " %", null);
        mNotificationManager.notify(NOTIFICATION_DOCK, mNotification);
    }
    
    public void hide() {
        mNotificationManager.cancel(NOTIFICATION_DOCK);
    }
}
