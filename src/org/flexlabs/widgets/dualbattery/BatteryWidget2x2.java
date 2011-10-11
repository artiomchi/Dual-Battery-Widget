package org.flexlabs.widgets.dualbattery;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by IntelliJ IDEA.
 * User: Flexer
 * Date: 11/10/11
 * Time: 22:52
 */
public class BatteryWidget2x2 extends BatteryWidget {
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        Log.d(LOG, "onReceive: " + intent.getAction());
        if (Constants.ACTION_BATTERY_UPDATE.equals(intent.getAction()) ||
            Constants.ACTION_SETTINGS_UPDATE.equals(intent.getAction())) {
            updateWidget(context);
        }
    }

    private void updateWidget(Context context) {
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = widgetManager.getAppWidgetIds(new ComponentName(context, BatteryWidget2x2.class));
        onUpdate(context, widgetManager, appWidgetIds);
    }
}
