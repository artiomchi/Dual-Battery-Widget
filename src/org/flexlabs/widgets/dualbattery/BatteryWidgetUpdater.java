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
import org.flexlabs.widgets.dualbattery.widgetsettings.WidgetSettingsContainer;

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
    

    private static void updateWidget(Context context, AppWidgetManager widgetManager, int widgetId, BatteryLevel batteryLevel) {
        if (batteryLevel == null)
            return;

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        WidgetSettingsContainer settings = new WidgetSettingsContainer(context, widgetId);

        for (int[][] aTextStyleArray : textStyleArray)
            for (int[] bTextStyleArray : aTextStyleArray)
                for (int cTextStyleArray : bTextStyleArray) {
                    views.setTextViewText(cTextStyleArray, null);
                    views.setViewVisibility(cTextStyleArray, View.GONE);
                }
        int textStatusTab = 0, textStatusDock = 0;
        if (settings.getTextPosition() > 0) {
            textStatusTab = textStyleArray[settings.getTextPosition() - 1][settings.getTextColorCode()][0];
            textStatusDock = textStyleArray[settings.getTextPosition() - 1][settings.getTextColorCode()][1];
            views.setViewVisibility(textStatusTab, View.VISIBLE);
            views.setViewVisibility(textStatusDock, View.VISIBLE);
        }

        // This is here just for the screenshots ;)
        //BatteryApplication.batteryTab = 15;
        //BatteryApplication.batteryDock = null;

        //BatteryApplication.batteryTab = 86;
        //BatteryApplication.status = BatteryManager.BATTERY_STATUS_CHARGING;
        //BatteryApplication.batteryDock = 30;

        if ((settings.getBatterySelection() & Constants.BATTERY_SELECTION_MAIN) > 0) {
            views.setViewVisibility(R.id.batteryFrame_main, View.VISIBLE);
            if ((settings.getMargin() & Constants.MARGIN_TOP) > 0) {
                int id = textStyleArray[Constants.TEXT_POS_ABOVE - 1][settings.getTextColorCode()][0];
                views.setViewVisibility(id, View.VISIBLE);
                views.setFloat(id, "setTextSize", settings.getTextSize());
                views.setTextViewText(id, " ");
            }
            if ((settings.getMargin() & Constants.MARGIN_BOTTOM) > 0 || settings.isShowLabel()) {
                int id = textStyleArray[Constants.TEXT_POS_BELOW - 1][settings.getTextColorCode()][0];
                views.setViewVisibility(id, View.VISIBLE);
                views.setFloat(id, "setTextSize", settings.getTextSize());
                views.setTextViewText(id, settings.isShowLabel() ? context.getString(R.string.battery_main) : " ");
            }
            if (settings.getTextPosition() > 0) {
                String status = String.valueOf(batteryLevel.get_level()) + "%";
                views.setFloat(textStatusTab, "setTextSize", settings.getTextSize());
                if (settings.getTextPosition() <= Constants.TEXT_POS_BOTTOM)
                    status = "\n" + status + "\n";
                views.setTextViewText(textStatusTab, status);
            }

            views.setImageViewResource(R.id.batteryTab, getBatteryResource(batteryLevel.get_level(), false));
            views.setViewVisibility(R.id.batteryTabCharging,
                    getVisible(batteryLevel.get_status() == BatteryManager.BATTERY_STATUS_CHARGING));
        } else {
            views.setViewVisibility(R.id.batteryFrame_main, View.GONE);
        }

        int dockVisible = batteryLevel.is_dockFriendly() &&
                (batteryLevel.is_dockConnected() || settings.isAlwaysShow()) &&
                ((settings.getBatterySelection() & Constants.BATTERY_SELECTION_DOCK) > 0)
            ? View.VISIBLE
            : View.GONE;
        views.setViewVisibility(R.id.batteryFrame_dock, dockVisible);
        if (batteryLevel.is_dockFriendly()) {
            if ((settings.getMargin() & Constants.MARGIN_TOP) > 0) {
                int id = textStyleArray[Constants.TEXT_POS_ABOVE - 1][settings.getTextColorCode()][1];
                views.setViewVisibility(id, View.VISIBLE);
                views.setFloat(id, "setTextSize", settings.getTextSize());
                views.setTextViewText(id, " ");
            }
            if ((settings.getMargin() & Constants.MARGIN_BOTTOM) > 0 || settings.isShowLabel()) {
                int id = textStyleArray[Constants.TEXT_POS_BELOW - 1][settings.getTextColorCode()][1];
                views.setViewVisibility(id, View.VISIBLE);
                views.setFloat(id, "setTextSize", settings.getTextSize());
                views.setTextViewText(id, settings.isShowLabel() ? context.getString(R.string.battery_dock) : " ");
            }
            Integer dockLevel = batteryLevel.get_dock_level();
            if (dockLevel == null && settings.isShowOldStatus())
                dockLevel = BatteryLevel.lastDockLevel;
            if (settings.getTextPosition() > 0) {
                String status = "n/a";
                if (dockLevel != null) {
                    status = dockLevel.toString() + "%";
                } else if (batteryLevel.get_dock_status() == Constants.DOCK_STATE_UNDOCKED) {
                    status = settings.isShowNotDocked() ? context.getString(R.string.undocked) : "";
                }
                views.setFloat(textStatusDock, "setTextSize", settings.getTextSize());
                if (settings.getTextPosition() <= Constants.TEXT_POS_BOTTOM)
                    status = "\n" + status + "\n";
                views.setTextViewText(textStatusDock, status);
            }

            views.setImageViewResource(R.id.batteryDock,
                    getBatteryResource(dockLevel, !batteryLevel.is_dockConnected()));
            views.setViewVisibility(R.id.batteryDockCharging,
                    getVisible(batteryLevel.get_dock_status() == Constants.DOCK_STATE_CHARGING));
        }

        Intent intent = new Intent(context, WidgetActivity.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, widgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget, pendingIntent);

        widgetManager.updateAppWidget(widgetId, views);
    }

    private static int getBatteryResource(Integer status, boolean alt) {
        if (status == null)
            return R.drawable.batt_0;
        if (status <= 10)
            return !alt ? R.drawable.batt_10 : R.drawable.batt_bw_10;
        if (status <= 20)
            return !alt ? R.drawable.batt_20 : R.drawable.batt_bw_20;
        if (status <= 30)
            return !alt ? R.drawable.batt_30 : R.drawable.batt_bw_30;
        if (status <= 40)
            return !alt ? R.drawable.batt_40 : R.drawable.batt_bw_40;
        if (status <= 50)
            return !alt ? R.drawable.batt_50 : R.drawable.batt_bw_50;
        if (status <= 60)
            return !alt ? R.drawable.batt_60 : R.drawable.batt_bw_60;
        if (status <= 70)
            return !alt ? R.drawable.batt_70 : R.drawable.batt_bw_70;
        if (status <= 80)
            return !alt ? R.drawable.batt_80 : R.drawable.batt_bw_80;
        if (status <= 90)
            return !alt ? R.drawable.batt_90 : R.drawable.batt_bw_90;
        return !alt ? R.drawable.batt_100 : R.drawable.batt_bw_100;
    }

    private static int getVisible(boolean visible) {
        return visible ? View.VISIBLE : View.GONE;
    }
}
