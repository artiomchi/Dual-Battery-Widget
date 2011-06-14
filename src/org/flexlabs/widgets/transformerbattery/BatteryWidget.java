package org.flexlabs.widgets.transformerbattery;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.*;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Created by IntelliJ IDEA.
 * User: Artiom Chilaru
 * Date: 13/06/11
 * Time: 20:13
 */
public class BatteryWidget extends AppWidgetProvider {
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        BatteryApplication.registerReceiver(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        BatteryApplication.unregisterReceiver(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (BatteryApplication.ACTION_WIDGET_UPDATE.equals(intent.getAction())) {
            updateWidget(context);
        }
    }

    private void updateWidget(Context context) {
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = widgetManager.getAppWidgetIds(new ComponentName(context, BatteryWidget.class));
        onUpdate(context, widgetManager, appWidgetIds);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        BatteryApplication.registerReceiver(context);

        final int n = appWidgetIds.length;
        for (int i = 0; i < n; i++) {
            int widgetId = appWidgetIds[i];
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

            /*BatteryApplication.batteryTab = 15;
            BatteryApplication.batteryDock = null;

            /*BatteryApplication.batteryTab = 33;
            BatteryApplication.batteryDock = 72;*/

            if (BatteryApplication.batteryTab != null)
                views.setTextViewText(R.id.statusTab, String.valueOf(BatteryApplication.batteryTab) + "%");
            else
                views.setTextViewText(R.id.statusTab, "n/a");
            views.setImageViewResource(R.id.batteryTab, getBatteryResource(BatteryApplication.batteryTab));

            if (BatteryApplication.batteryDock != null)
                views.setTextViewText(R.id.statusDock, String.valueOf(BatteryApplication.batteryDock) + "%");
            else
                views.setTextViewText(R.id.statusDock, "n/a");
            views.setImageViewResource(R.id.batteryDock, getBatteryResource(BatteryApplication.batteryDock));

            appWidgetManager.updateAppWidget(widgetId, views);
        }
    }

    private int getBatteryResource(Integer status) {
        if (status == null)
            return R.drawable.empty;
        if (status <= 15)
            return R.drawable.critical;
        if (status <= 70)
            return R.drawable.medium;
        return R.drawable.full;
    }
}
