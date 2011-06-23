package org.flexlabs.widgets.dualbattery;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.*;
import android.os.BatteryManager;
import android.util.Log;
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
    private static final String SETTING_SHOW_SELECTION = "batterySelection";
    private static final String SETTING_SHOW_SELECTION_DEFAULT = "0";
    private static final int BATTERY_SELECTION_BOTH = 0;
    private static final int BATTERY_SELECTION_MAIN = 1;
    private static final int BATTERY_SELECTION_SECOND = 2;
    private static final String SETTING_TEXT_COLOR = "textColor";
    private static final String SETTING_TEXT_COLOR_DEFAULT = "0";

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        context.startService(new Intent(context, BatteryMonitorService.class));
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        context.stopService(new Intent(context, BatteryMonitorService.class));
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

        if (Constants.ACTION_BATTERY_UPDATE.equals(intent.getAction()) ||
            Constants.ACTION_SETTINGS_UPDATE.equals(intent.getAction())) {
            updateWidget(context);
        }
    }

    private void updateWidget(Context context) {
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = widgetManager.getAppWidgetIds(new ComponentName(context, BatteryWidget.class));
        onUpdate(context, widgetManager, appWidgetIds);
    }

    private static final int[][][] textStyleArray = new int[][][]
    {
        new int[][] { // textpos: top = 0,
            new int[] { R.id.statusTabWhiteTop, R.id.statusDockWhiteTop }, // color: white = 1
            new int[] { R.id.statusTabDarkTop, R.id.statusDockDarkTop }, // color: dark = 0
        },
        new int[][] { // textpos: middle = 1
            new int[] { R.id.statusTabWhiteMiddle, R.id.statusDockWhiteMiddle },
            new int[] { R.id.statusTabDarkMiddle, R.id.statusDockDarkMiddle },
        },
        new int[][] { // textpos: bottom = 2
            new int[] { R.id.statusTabWhiteBottom, R.id.statusDockWhiteBottom },
            new int[] { R.id.statusTabDarkBottom, R.id.statusDockDarkBottom },
        }
    };

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // ensuring the service is still running, even if it was killed
        context.startService(new Intent(context, BatteryMonitorService.class));

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
            int batterySelection = Integer.valueOf(pref.getString(SETTING_SHOW_SELECTION, SETTING_SHOW_SELECTION_DEFAULT));
            int textColorCode = Integer.valueOf(pref.getString(SETTING_TEXT_COLOR, SETTING_TEXT_COLOR_DEFAULT));

            if (batterySelection == 0 || autoHideOld != SETTING_AUTOHIDE_DEFAULT) {
                SharedPreferences.Editor editor = pref.edit();
                if (autoHideOld != SETTING_AUTOHIDE_DEFAULT) {
                    editor.putBoolean(SETTING_ALWAYSSHOWDOCK, alwaysShow);
                    editor.remove(SETTING_AUTOHIDE);
                }
                if (batterySelection == 0) {
                    editor.putString(SETTING_SHOW_SELECTION, "3");
                    batterySelection = 3;
                }
                editor.commit();
            }

            int textStatusTab = 0, textStatusDock = 0;
            if (textPositionCode > 0) {
                textStatusTab = textStyleArray[textPositionCode - 1][textColorCode][0];
                textStatusDock = textStyleArray[textPositionCode - 1][textColorCode][1];
            }
            for (int a = 0; a < 3; a++)
                for (int b = 0; b < 2; b++)
                    for (int c = 0; c < 2; c++)
                        views.setTextViewText(textStyleArray[a][b][c], null);

            // This is here just for the screenshots ;)
            /*BatteryApplication.batteryTab = 15;
            BatteryApplication.batteryDock = null;

            /*BatteryApplication.batteryTab = 86;
            BatteryApplication.status = BatteryManager.BATTERY_STATUS_CHARGING;
            BatteryApplication.batteryDock = 30;*/

            if ((batterySelection & BATTERY_SELECTION_MAIN) > 0) {
                views.setViewVisibility(R.id.batteryFrame_main, View.VISIBLE);
                if (textPositionCode > 0) {
                    String status = BatteryMonitorService.batteryTab != null
                        ? BatteryMonitorService.batteryTab.toString() + "%"
                        : "n/a";
                    views.setFloat(textStatusTab, "setTextSize", textSize);
                    views.setTextViewText(textStatusTab, "\n" + status + "\n");
                }
                views.setImageViewResource(R.id.batteryTab, getBatteryResource(BatteryMonitorService.batteryTab));
                views.setViewVisibility(R.id.batteryTabCharging,
                        getVisible(BatteryMonitorService.status == BatteryManager.BATTERY_STATUS_CHARGING));
            } else {
                views.setViewVisibility(R.id.batteryFrame_main, View.GONE);
            }

            int dockVisible = BatteryMonitorService.hasDock && (BatteryMonitorService.batteryDock != null || alwaysShow) &&
                    ((batterySelection & BATTERY_SELECTION_SECOND) > 0)
                ? View.VISIBLE
                : View.GONE;
            views.setViewVisibility(R.id.batteryFrame_dock, dockVisible);
            if (BatteryMonitorService.hasDock) {
                if (textPositionCode > 0) {
                    String status = "n/a";
                    if (BatteryMonitorService.batteryDock != null) {
                        status = BatteryMonitorService.batteryDock.toString() + "%";
                    } else if (BatteryMonitorService.dockStatus == Constants.DOCK_STATE_UNDOCKED) {
                        status = showNotDocked ? context.getString(R.string.undocked) : "";
                    }
                    views.setFloat(textStatusDock, "setTextSize", textSize);
                    views.setTextViewText(textStatusDock, "\n" + status + "\n");
                }
                views.setImageViewResource(R.id.batteryDock, getBatteryResource(BatteryMonitorService.batteryDock));
                views.setViewVisibility(R.id.batteryDockCharging,
                        getVisible(BatteryMonitorService.dockStatus == Constants.DOCK_STATE_CHARGING));
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
            return R.drawable.batt_0;
        if (status <= 10)
            return R.drawable.batt_10;
        if (status <= 20)
            return R.drawable.batt_20;
        if (status <= 30)
            return R.drawable.batt_30;
        if (status <= 40)
            return R.drawable.batt_40;
        if (status <= 50)
            return R.drawable.batt_50;
        if (status <= 60)
            return R.drawable.batt_60;
        if (status <= 70)
            return R.drawable.batt_70;
        if (status <= 80)
            return R.drawable.batt_80;
        if (status <= 90)
            return R.drawable.batt_90;
        return R.drawable.batt_100;
    }

    private int getVisible(boolean visible) {
        return visible ? View.VISIBLE : View.GONE;
    }
}
