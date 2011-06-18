package org.flexlabs.widgets.dualbattery;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.*;
import android.os.BatteryManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.RemoteViews;
import org.flexlabs.widgets.dualbattery.settings.WidgetPropertiesActivity;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Artiom Chilaru
 * Date: 13/06/11
 * Time: 20:13
 */
public class BatteryWidget extends AppWidgetProvider {
    private static final String SETTING_AUTOHIDE = "autoHideDock";
    private static final boolean SETTING_AUTOHIDE_DEFAULT = false;
    private static final String SETTING_ALWAYSSHOWDOCK = "alwaysShowDock";
    private static final boolean SETTING_ALWAYSSHOWDOCK_DEFAULT = true;
    private static final String SETTING_TEXTPOS = "textPosition";
    private static final String SETTING_TEXTPOS_DEFAULT = "2";
    private static final String SETTING_TEXTSIZE = "textSize";
    private static final String SETTING_TEXTSIZE_DEFAULT = "14";
    private static final String SETTING_SHOW_NOTDOCKED = "showNotDockedMessage";
    private static final boolean SETTING_SHOW_NOTDOCKED_DEFAULT = true;

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
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        for (int i : appWidgetIds) {
            String file = Constants.SETTINGS_PREFIX + i;
            context.getSharedPreferences(file, Context.MODE_PRIVATE)
                    .edit().clear().commit();
            new File(context.getFilesDir() + "/../shared_prefs/" + file + ".xml").delete();
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (Constants.ACTION_WIDGET_UPDATE.equals(intent.getAction())) {
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
        Log.d("FlexLabs", "Widget count: " + appWidgetIds.length);
        for (int i = 0; i < n; i++) {
            int widgetId = appWidgetIds[i];
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
            SharedPreferences pref = context.getSharedPreferences(Constants.SETTINGS_PREFIX + widgetId, Context.MODE_PRIVATE);
            boolean autoHideOld = pref.getBoolean(SETTING_AUTOHIDE, SETTING_AUTOHIDE_DEFAULT); // legacy reasons.. for widgets added before v0.6
            boolean alwaysShow = pref.getBoolean(SETTING_ALWAYSSHOWDOCK, SETTING_ALWAYSSHOWDOCK_DEFAULT && !autoHideOld);
            boolean showNotDocked = pref.getBoolean(SETTING_SHOW_NOTDOCKED, SETTING_SHOW_NOTDOCKED_DEFAULT);
            int textSize = Integer.valueOf(pref.getString(SETTING_TEXTSIZE, SETTING_TEXTSIZE_DEFAULT));
            int textPositionCode = Integer.valueOf(pref.getString(SETTING_TEXTPOS, SETTING_TEXTPOS_DEFAULT));
            int textStatusTab = 0, textStatusDock = 0;
            switch (textPositionCode) {
                case 1:
                    textStatusTab = R.id.statusTabTop;
                    textStatusDock = R.id.statusDockTop;
                    break;

                case 2:
                    textStatusTab = R.id.statusTabMiddle;
                    textStatusDock = R.id.statusDockMiddle;
                    break;

                case 3:
                    textStatusTab = R.id.statusTabBottom;
                    textStatusDock = R.id.statusDockBottom;
            }
            for (int id : new int[] {R.id.statusTabBottom,R.id.statusTabMiddle,R.id.statusTabTop,
                                     R.id.statusDockBottom,R.id.statusDockMiddle,R.id.statusDockTop}) {
                views.setViewVisibility(id, View.GONE);
            }
            views.setViewVisibility(textStatusTab, View.VISIBLE);
            views.setViewVisibility(textStatusDock, View.VISIBLE);

            /*BatteryApplication.batteryTab = 15;
            BatteryApplication.batteryDock = null;

            /*BatteryApplication.batteryTab = 86;
            BatteryApplication.status = BatteryManager.BATTERY_STATUS_CHARGING;
            BatteryApplication.batteryDock = 30;*/

            if (textPositionCode > 0) {
                views.setFloat(textStatusTab, "setTextSize", textSize);
                if (BatteryApplication.batteryTab != null)
                    views.setTextViewText(textStatusTab, "\n" + BatteryApplication.batteryTab + "%\n");
                else
                    views.setTextViewText(textStatusTab, "\nn/a\n");
            }
            views.setImageViewResource(R.id.batteryTab, getBatteryResource(BatteryApplication.batteryTab));
            if (BatteryApplication.status == BatteryManager.BATTERY_STATUS_CHARGING)
                views.setViewVisibility(R.id.batteryTabCharging, View.VISIBLE);
            else
                views.setViewVisibility(R.id.batteryTabCharging, View.GONE);
            /*if (BatteryApplication.status == BatteryManager.BATTERY_PLUGGED_AC)
                views.setViewVisibility(R.id.batteryTabCharged, View.VISIBLE);
            else
                views.setViewVisibility(R.id.batteryTabCharged, View.GONE);*/

            int dockVisible = BatteryApplication.hasDock && (BatteryApplication.batteryDock != null || alwaysShow)
                ? View.VISIBLE
                : View.GONE;
            views.setViewVisibility(R.id.dockFrame, dockVisible);
            if (BatteryApplication.hasDock) {
                if (textPositionCode > 0) {
                    views.setFloat(textStatusDock, "setTextSize", textSize);
                    if (BatteryApplication.batteryDock != null) {
                        views.setTextViewText(textStatusDock, "\n" + BatteryApplication.batteryDock + "%\n");
                    } else if (BatteryApplication.dockStatus == BatteryApplication.DOCK_STATE_UNDOCKED) {
                        if (showNotDocked)
                            views.setTextViewText(textStatusDock, "\n" + context.getString(R.string.undocked) + "\n");
                        else
                            views.setViewVisibility(textStatusDock, View.GONE);
                    } else {
                        views.setTextViewText(textStatusDock, "\nn/a\n");
                    }
                }
                views.setImageViewResource(R.id.batteryDock, getBatteryResource(BatteryApplication.batteryDock));
                if (BatteryApplication.dockStatus == BatteryApplication.DOCK_STATE_CHARGING)
                    views.setViewVisibility(R.id.batteryDockCharging, View.VISIBLE);
                else
                    views.setViewVisibility(R.id.batteryDockCharging, View.GONE);
            }

            Intent intent = new Intent(context, WidgetPropertiesActivity.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            intent.putExtra(Constants.EXTRA_WIDGET_OLD, true);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, widgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

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

    private int getTextGravity(int value) {
        if (value == 1)
            return Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        if (value == 3)
            return Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        return Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
    }
}
