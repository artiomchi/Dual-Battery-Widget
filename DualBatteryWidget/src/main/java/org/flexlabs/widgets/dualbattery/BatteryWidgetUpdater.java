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
import android.os.BatteryManager;
import android.view.View;
import android.widget.RemoteViews;
import org.flexlabs.widgets.dualbattery.widgetsettings.WidgetActivity;
import org.flexlabs.widgets.dualbattery.widgetsettings.WidgetActivity_;
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
            int[] widgets2 = manager.getAppWidgetIds(new ComponentName(context, BatteryWidget1x1.class));
            int[] widgets3 = manager.getAppWidgetIds(new ComponentName(context, BatteryWidget2x2.class));
            int[] widgets4 = manager.getAppWidgetIds(new ComponentName(context, BatteryWidget3x4.class));
            widgets = new int[widgets1.length + widgets2.length + widgets3.length];
            System.arraycopy(widgets1, 0, widgets, 0, widgets1.length);
            System.arraycopy(widgets2, 0, widgets, widgets1.length, widgets2.length);
            System.arraycopy(widgets3, 0, widgets, widgets1.length + widgets2.length, widgets3.length);
            System.arraycopy(widgets4, 0, widgets, widgets1.length + widgets2.length + widgets3.length, widgets4.length);
        }

        for (int widgetId : widgets) {
            updateWidget(context, manager, widgetId, level);
        }
    }

    private static final int[][] textStyleArray = new int[][]
    {
        new int[] { // textpos: top = 0,
            R.id.statusWhiteTop, // color: white = 1
            R.id.statusDarkTop, // color: dark = 0
        },
        new int[] { // textpos: middle = 1
            R.id.statusWhiteMiddle,
            R.id.statusDarkMiddle,
        },
        new int[] { // textpos: bottom = 2
            R.id.statusWhiteBottom,
            R.id.statusDarkBottom,
        },
        new int[] { // textpos: above = 4
            R.id.batteryLabel_top,
            R.id.batteryLabel_top_dark,
        },
        new int[] { // textpos: below = 5
            R.id.batteryLabel_bottom,
            R.id.batteryLabel_bottom_dark,
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

        WidgetSettingsContainer settings = new WidgetSettingsContainer(context, widgetId);
        RemoteViews views = new RemoteViews(context.getPackageName(), settings.getTheme().equals(Constants.SETTING_THEME_DEFAULT) ? R.layout.widget : R.layout.widget_90deg);
        views.removeAllViews(R.id.widget);

        // This is here just for the screenshots ;)
        //BatteryApplication.batteryTab = 15;
        //BatteryApplication.batteryDock = null;

        //BatteryApplication.batteryTab = 86;
        //BatteryApplication.status = BatteryManager.BATTERY_STATUS_CHARGING;
        //BatteryApplication.batteryDock = 30;

        RemoteViews viewsBattery = null, viewsDock = null;

        if ((settings.getBatterySelection() & Constants.BATTERY_SELECTION_MAIN) > 0) {
            String status = String.valueOf(batteryLevel.get_level()) + "%";
            if (settings.getTextPosition() <= Constants.TEXT_POS_BOTTOM)
                status = "\n" + status + "\n";
            viewsBattery = loadBatteryView(
                    context, settings, R.string.battery_main, status, batteryLevel.get_level(),
                    false, batteryLevel.get_status() == BatteryManager.BATTERY_STATUS_CHARGING);
        }

        if (batteryLevel.is_dockFriendly() &&
                (batteryLevel.is_dockConnected() || settings.isAlwaysShow()) &&
                ((settings.getBatterySelection() & Constants.BATTERY_SELECTION_DOCK) > 0)) {
            Integer dockLevel = batteryLevel.get_dock_level();
            if (dockLevel == null && settings.isShowOldStatus())
                dockLevel = BatteryLevel.lastDockLevel;
            String status = "n/a";
            if (dockLevel != null) {
                status = dockLevel.toString() + "%";
            } else if (batteryLevel.get_dock_status() == Constants.DOCK_STATE_UNDOCKED) {
                status = settings.isShowNotDocked() ? context.getString(R.string.undocked) : "";
            }
            if (settings.getTextPosition() <= Constants.TEXT_POS_BOTTOM)
                status = "\n" + status + "\n";

            viewsDock = loadBatteryView(
                    context,  settings,  R.string.battery_dock, status,  dockLevel,
                    batteryLevel.get_dock_level() == null,
                    batteryLevel.get_dock_status() == Constants.DOCK_STATE_CHARGING);

        }

        if (settings.isSwapBatteries()) {
            if (viewsBattery != null)
                views.addView(R.id.widget, viewsBattery);
            if (viewsDock != null)
                views.addView(R.id.widget, viewsDock);
        } else {
            if (viewsDock != null)
                views.addView(R.id.widget, viewsDock);
            if (viewsBattery != null)
                views.addView(R.id.widget, viewsBattery);

        }

        Intent intent = WidgetActivity_.intent(context)
                .appWidgetId(widgetId)
                .flags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
                .get();
        PendingIntent pendingIntent = PendingIntent.getActivity(context, widgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget, pendingIntent);

        widgetManager.updateAppWidget(widgetId, views);
    }

    private static RemoteViews loadBatteryView(Context context, WidgetSettingsContainer settings, int label, String status, Integer level, boolean disabled, boolean charging) {
        RemoteViews views = new RemoteViews(context.getPackageName(), settings.getTheme().equals(Constants.SETTING_THEME_DEFAULT) ? R.layout.widget_battery : R.layout.widget_battery_90deg);
        for (int[] aTextStyleArray : textStyleArray)
            for (int bTextStyleArray : aTextStyleArray) {
                views.setTextViewText(bTextStyleArray, null);
                views.setViewVisibility(bTextStyleArray, View.GONE);
            }
        if ((settings.getMargin() & Constants.MARGIN_TOP) > 0) {
            int id = textStyleArray[Constants.TEXT_POS_ABOVE - 1][settings.getTextColorCode()];
            views.setViewVisibility(id, View.VISIBLE);
            views.setFloat(id, "setTextSize", settings.getTextSize());
            views.setTextViewText(id, " ");
        }
        if ((settings.getMargin() & Constants.MARGIN_BOTTOM) > 0 || settings.isShowLabel()) {
            int id = textStyleArray[Constants.TEXT_POS_BELOW - 1][settings.getTextColorCode()];
            views.setViewVisibility(id, View.VISIBLE);
            views.setFloat(id, "setTextSize", settings.getTextSize());
            views.setTextViewText(id, settings.isShowLabel() ? context.getString(label) : " ");
        }
        if (settings.getTextPosition() > 0) {
            int textStatus = textStyleArray[settings.getTextPosition() - 1][settings.getTextColorCode()];
            views.setViewVisibility(textStatus, View.VISIBLE);
            views.setFloat(textStatus, "setTextSize", settings.getTextSize());
            views.setTextViewText(textStatus, status);
        }

        views.setInt(R.id.battery, "setImageLevel", (level != null ? level : 0) + (disabled ? 200 : 0));
        views.setViewVisibility(R.id.batteryCharging, charging ? View.VISIBLE : View.GONE);

        return views;
    }
}
