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

package org.flexlabs.widgets.dualbattery;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Build;
import android.view.View;
import android.widget.RemoteViews;
import org.flexlabs.widgets.dualbattery.widgetsettings.WidgetActivity;

public class BatteryWidgetUpdater {
    // Suppress default constructor for non-instantiability
    private BatteryWidgetUpdater() {
        throw new AssertionError();
    }

    public static void updateAllWidgets(Context context, BatteryLevel level, int[] widgets) {
        // Get all "running" widgets
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        if (widgets == null) {
            int[] widgets1 = manager.getAppWidgetIds(new ComponentName(context, BatteryWidget.class));
            int[] widgets2 = manager.getAppWidgetIds(new ComponentName(context, BatteryWidget2x2.class));
            int[] widgets3 = manager.getAppWidgetIds(new ComponentName(context, BatteryWidget3x4.class));
            widgets = new int[widgets1.length + widgets2.length + widgets3.length];
            System.arraycopy(widgets1, 0, widgets, 0, widgets1.length);
            System.arraycopy(widgets2, 0, widgets, widgets1.length, widgets2.length);
            System.arraycopy(widgets3, 0, widgets, widgets1.length + widgets2.length, widgets3.length);
        }

        for (int widgetId : widgets) {
            updateWidget(context, manager, widgetId, level);
        }
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
        },
        new int[][] { // textpos: above = 4
            new int[] { R.id.batteryLabel_main_top, R.id.batteryLabel_dock_top },
            new int[] { R.id.batteryLabel_main_top_dark, R.id.batteryLabel_dock_top_dark },
        },
        new int[][] { // textpos: below = 5
            new int[] { R.id.batteryLabel_main_bottom, R.id.batteryLabel_dock_bottom },
            new int[] { R.id.batteryLabel_main_bottom_dark, R.id.batteryLabel_dock_bottom_dark },
        }
    };
    
    public static void updateWidget(final Context context, final int widgetId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWidget(context, AppWidgetManager.getInstance(context), widgetId, BatteryLevel.getCurrent());
            }
        }).start();
    }
    
    private static void updateWidgetSettings(SharedPreferences pref, int version) {
        SharedPreferences.Editor editor = pref.edit();
        if (version == 1) {
            if (pref.contains(Constants.SETTING_TEXT_SIZE)) {
                int textPosition = Integer.valueOf(pref.getString(Constants.SETTING_TEXT_SIZE, String.valueOf(Constants.SETTING_TEXT_SIZE_DEFAULT)));
                editor.putInt(Constants.SETTING_TEXT_SIZE, textPosition);
            }
            if (pref.contains(Constants.SETTING_TEXT_POS)) {
                int textPosition = Integer.valueOf(pref.getString(Constants.SETTING_TEXT_POS, String.valueOf(Constants.SETTING_TEXT_POS_DEFAULT)));
                editor.putInt(Constants.SETTING_TEXT_POS, textPosition);
            }
            if (pref.contains(Constants.SETTING_SHOW_SELECTION)) {
                int batterySelection = Integer.valueOf(pref.getString(Constants.SETTING_SHOW_SELECTION, String.valueOf(Constants.SETTING_SHOW_SELECTION_DEFAULT)));
                editor.putInt(Constants.SETTING_SHOW_SELECTION, batterySelection);
            }
            if (pref.contains(Constants.SETTING_TEXT_COLOR)) {
                int textColorCode = Integer.valueOf(pref.getString(Constants.SETTING_TEXT_COLOR, String.valueOf(Constants.SETTING_TEXT_COLOR_DEFAULT)));
                editor.putInt(Constants.SETTING_TEXT_COLOR, textColorCode);
            }
            if (pref.contains(Constants.SETTING_MARGIN)) {
                int margin = Integer.valueOf(pref.getString(Constants.SETTING_MARGIN, String.valueOf(Constants.SETTING_MARGIN_DEFAULT)));
                editor.putInt(Constants.SETTING_MARGIN, margin);
            }
            version = 2;

            editor.putInt(Constants.SETTING_VERSION, version);
            if (Build.VERSION.SDK_INT < 9)
                editor.commit();
            else
                editor.apply();
        }
    }

    private static void updateWidget(Context context, AppWidgetManager widgetManager, int widgetId, BatteryLevel batteryLevel) {
        if (batteryLevel == null)
            return;

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        SharedPreferences pref = context.getSharedPreferences(Constants.SETTINGS_PREFIX + widgetId, Context.MODE_PRIVATE);
        int version = pref.getInt(Constants.SETTING_VERSION, 1);
        if (version != Constants.SETTING_VERSION_CURRENT)
            updateWidgetSettings(pref, version);
        boolean alwaysShow = pref.getBoolean(Constants.SETTING_ALWAYS_SHOW_DOCK, Constants.SETTING_ALWAYS_SHOW_DOCK_DEFAULT);
        boolean showNotDocked = pref.getBoolean(Constants.SETTING_SHOW_NOT_DOCKED, Constants.SETTING_SHOW_NOT_DOCKED_DEFAULT);
        boolean showLabel = pref.getBoolean(Constants.SETTING_SHOW_LABEL, Constants.SETTING_SHOW_LABEL_DEFAULT);
        boolean showOldStatus = pref.getBoolean(Constants.SETTING_SHOW_OLD_DOCK, Constants.SETTING_SHOW_OLD_DOCK_DEFAULT);
        int textSize = pref.getInt(Constants.SETTING_TEXT_SIZE, Constants.SETTING_TEXT_SIZE_DEFAULT);
        int textPosition = pref.getInt(Constants.SETTING_TEXT_POS, Constants.SETTING_TEXT_POS_DEFAULT);
        int batterySelection = pref.getInt(Constants.SETTING_SHOW_SELECTION, Constants.SETTING_SHOW_SELECTION_DEFAULT);
        int textColorCode = pref.getInt(Constants.SETTING_TEXT_COLOR, Constants.SETTING_TEXT_COLOR_DEFAULT);
        int margin = pref.getInt(Constants.SETTING_MARGIN, Constants.SETTING_MARGIN_DEFAULT);

        for (int[][] aTextStyleArray : textStyleArray)
            for (int[] bTextStyleArray : aTextStyleArray)
                for (int cTextStyleArray : bTextStyleArray) {
                    views.setTextViewText(cTextStyleArray, null);
                    views.setViewVisibility(cTextStyleArray, View.GONE);
                }
        int textStatusTab = 0, textStatusDock = 0;
        if (textPosition > 0) {
            textStatusTab = textStyleArray[textPosition - 1][textColorCode][0];
            textStatusDock = textStyleArray[textPosition - 1][textColorCode][1];
            views.setViewVisibility(textStatusTab, View.VISIBLE);
            views.setViewVisibility(textStatusDock, View.VISIBLE);
        }

        // This is here just for the screenshots ;)
        /*BatteryApplication.batteryTab = 15;
        BatteryApplication.batteryDock = null;

        /*BatteryApplication.batteryTab = 86;
        BatteryApplication.status = BatteryManager.BATTERY_STATUS_CHARGING;
        BatteryApplication.batteryDock = 30;*/

        if ((batterySelection & Constants.BATTERY_SELECTION_MAIN) > 0) {
            views.setViewVisibility(R.id.batteryFrame_main, View.VISIBLE);
            if ((margin & Constants.MARGIN_TOP) > 0) {
                int id = textStyleArray[Constants.TEXT_POS_ABOVE - 1][textColorCode][0];
                views.setViewVisibility(id, View.VISIBLE);
                views.setFloat(id, "setTextSize", textSize);
                views.setTextViewText(id, " ");
            }
            if ((margin & Constants.MARGIN_BOTTOM) > 0 || showLabel) {
                int id = textStyleArray[Constants.TEXT_POS_BELOW - 1][textColorCode][0];
                views.setViewVisibility(id, View.VISIBLE);
                views.setFloat(id, "setTextSize", textSize);
                views.setTextViewText(id, showLabel ? context.getString(R.string.battery_main) : " ");
            }
            if (textPosition > 0) {
                String status = String.valueOf(batteryLevel.get_level()) + "%";
                views.setFloat(textStatusTab, "setTextSize", textSize);
                if (textPosition <= Constants.TEXT_POS_BOTTOM)
                    status = "\n" + status + "\n";
                views.setTextViewText(textStatusTab, status);
            }

            int imgRes = batteryLevel.get_status() == BatteryManager.BATTERY_STATUS_CHARGING
                    ? R.drawable.batt_charging
                    : R.drawable.batt;
            views.setImageViewResource(R.id.batteryTab, imgRes);
            views.setInt(R.id.batteryTab, "setImageLevel", batteryLevel.get_level());
        } else {
            views.setViewVisibility(R.id.batteryFrame_main, View.GONE);
        }

        int dockVisible = batteryLevel.is_dockFriendly() &&
                (batteryLevel.is_dockConnected() || alwaysShow) &&
                ((batterySelection & Constants.BATTERY_SELECTION_DOCK) > 0)
            ? View.VISIBLE
            : View.GONE;
        views.setViewVisibility(R.id.batteryFrame_dock, dockVisible);
        if (batteryLevel.is_dockFriendly()) {
            if ((margin & Constants.MARGIN_TOP) > 0) {
                int id = textStyleArray[Constants.TEXT_POS_ABOVE - 1][textColorCode][1];
                views.setViewVisibility(id, View.VISIBLE);
                views.setFloat(id, "setTextSize", textSize);
                views.setTextViewText(id, " ");
            }
            if ((margin & Constants.MARGIN_BOTTOM) > 0 || showLabel) {
                int id = textStyleArray[Constants.TEXT_POS_BELOW - 1][textColorCode][1];
                views.setViewVisibility(id, View.VISIBLE);
                views.setFloat(id, "setTextSize", textSize);
                views.setTextViewText(id, showLabel ? context.getString(R.string.battery_dock) : " ");
            }
            Integer dockLevel = batteryLevel.get_dock_level();
            if (dockLevel == null && showOldStatus)
                dockLevel = BatteryLevel.lastDockLevel;
            if (textPosition > 0) {
                String status = "n/a";
                if (dockLevel != null) {
                    status = dockLevel.toString() + "%";
                } else if (batteryLevel.get_dock_status() == Constants.DOCK_STATE_UNDOCKED) {
                    status = showNotDocked ? context.getString(R.string.undocked) : "";
                }
                views.setFloat(textStatusDock, "setTextSize", textSize);
                if (textPosition <= Constants.TEXT_POS_BOTTOM)
                    status = "\n" + status + "\n";
                views.setTextViewText(textStatusDock, status);
            }

            int imgRes = batteryLevel.is_dockConnected()
                    ? batteryLevel.get_dock_status() == Constants.DOCK_STATE_CHARGING
                        ? R.drawable.batt_charging
                        : R.drawable.batt
                    : R.drawable.batt_bw;
            views.setImageViewResource(R.id.batteryDock, imgRes);
            views.setInt(R.id.batteryDock, "setImageLevel", dockLevel);
        }

        Intent intent = new Intent(context, WidgetActivity.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, widgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget, pendingIntent);

        widgetManager.updateAppWidget(widgetId, views);
    }
}
