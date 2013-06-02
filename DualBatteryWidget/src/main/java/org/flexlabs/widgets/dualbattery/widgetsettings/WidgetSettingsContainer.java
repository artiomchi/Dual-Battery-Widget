/*
 * Copyright 2012 Artiom Chilaru (http://flexlabs.org)
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

package org.flexlabs.widgets.dualbattery.widgetsettings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import org.flexlabs.widgets.dualbattery.BatteryLevel;
import org.flexlabs.widgets.dualbattery.Constants;

public class WidgetSettingsContainer {
    private int textSize;
    private int textPosition;
    private int batterySelection;
    private int textColorCode;
    private int margin;
    private boolean alwaysShow;
    private boolean showNotDocked;
    private boolean showLabel;
    private boolean showOldStatus;
    private boolean swapBatteries;
    private String theme;

    private static SharedPreferences getPreferences(Context context, int widgetId) {
        SharedPreferences pref = context.getSharedPreferences(Constants.SETTINGS_WIDGET_FILE + widgetId, Context.MODE_PRIVATE);
        int version = pref.getInt(Constants.SETTING_VERSION, 1);
        if (version != Constants.SETTING_VERSION_CURRENT)
            updateWidgetSettings(pref, version);
        return pref;
    }

    public WidgetSettingsContainer(Context context, int widgetId) {
        SharedPreferences pref = getPreferences(context, widgetId);
        alwaysShow = pref.getBoolean(Constants.SETTING_ALWAYS_SHOW_DOCK, Constants.SETTING_ALWAYS_SHOW_DOCK_DEFAULT);
        showNotDocked = pref.getBoolean(Constants.SETTING_SHOW_NOT_DOCKED, Constants.SETTING_SHOW_NOT_DOCKED_DEFAULT);
        showLabel = pref.getBoolean(Constants.SETTING_SHOW_LABEL, Constants.SETTING_SHOW_LABEL_DEFAULT);
        showOldStatus = pref.getBoolean(Constants.SETTING_SHOW_OLD_DOCK, Constants.SETTING_SHOW_OLD_DOCK_DEFAULT);
        swapBatteries = pref.getBoolean(Constants.SETTING_SWAP_BATTERIES, Constants.SETTING_SWAP_BATTERIES_DEFAULT);
        textSize = pref.getInt(Constants.SETTING_TEXT_SIZE, Constants.SETTING_TEXT_SIZE_DEFAULT);
        textPosition = pref.getInt(Constants.SETTING_TEXT_POS, Constants.SETTING_TEXT_POS_DEFAULT);
        batterySelection = pref.getInt(Constants.SETTING_SHOW_SELECTION, Constants.SETTING_SHOW_SELECTION_DEFAULT);
        textColorCode = pref.getInt(Constants.SETTING_TEXT_COLOR, Constants.SETTING_TEXT_COLOR_DEFAULT);
        margin = pref.getInt(Constants.SETTING_MARGIN, Constants.SETTING_MARGIN_DEFAULT);
        theme = pref.getString(Constants.SETTING_THEME, Constants.SETTING_THEME_DEFAULT);
    }

    public static boolean getTempUnits(Context context, int widgetId) {
        SharedPreferences pref = getPreferences(context, widgetId);
        return pref.getInt(Constants.SETTING_TEMP_UNITS, Constants.SETTING_TEMP_UNITS_DEFAULT) == Constants.TEMP_UNIT_CELSIUS;
    }

    public static void setTempUnits(Context context, int widgetId, boolean tempUnitsC) {
        SharedPreferences pref = getPreferences(context, widgetId);
        pref.edit()
            .putInt(Constants.SETTING_TEMP_UNITS, tempUnitsC
                ? Constants.TEMP_UNIT_CELSIUS
                : Constants.TEMP_UNIT_FAHRENHEIT)
            .commit();
    }

    public static boolean getUpgradeSwappedSingle(Context context, int widgetId) {
        SharedPreferences pref = getPreferences(context, widgetId);
        boolean justSwapped = pref.getBoolean(Constants.SETTING_JUST_SWAPPED, Constants.SETTING_JUST_SWAPPED_DEFAULT);
        if (justSwapped) {
            pref.edit()
                .remove(Constants.SETTING_JUST_SWAPPED)
                .commit();
        }
        return justSwapped;
    }

    private static void updateWidgetSettings(SharedPreferences pref, int version) {
        SharedPreferences.Editor editor = pref.edit();
        if (version == 1 && pref.getAll().size() == 0)
            version = Constants.SETTING_VERSION_CURRENT;

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
            if (BatteryLevel.getCurrent().is_dockFriendly())
                editor.putBoolean(Constants.SETTING_JUST_SWAPPED, true);
            version = 2;
        }

        if (version == 2) {
            if (BatteryLevel.getCurrent().is_dockFriendly())
                editor.putBoolean(Constants.SETTING_JUST_SWAPPED, true);
            version = 3;
        }

        if (version == 3) {
            boolean showLabel = pref.getBoolean(Constants.SETTING_SHOW_LABEL, Constants.SETTING_SHOW_LABEL_DEFAULT);
            if (showLabel && !BatteryLevel.getCurrent().is_dockFriendly())
                editor.remove(Constants.SETTING_SHOW_LABEL);
            version = 4;
        }

        editor.putInt(Constants.SETTING_VERSION, version);
        if (Build.VERSION.SDK_INT < 9)
            editor.commit();
        else
            editor.apply();
    }

    public int getTextSize() {
        return textSize;
    }

    public int getTextPosition() {
        return textPosition;
    }

    public int getBatterySelection() {
        return batterySelection;
    }

    public int getTextColorCode() {
        return textColorCode;
    }

    public int getMargin() {
        return margin;
    }

    public boolean isAlwaysShow() {
        return alwaysShow;
    }

    public boolean isShowNotDocked() {
        return showNotDocked;
    }

    public boolean isShowLabel() {
        return showLabel;
    }

    public boolean isShowOldStatus() {
        return showOldStatus;
    }

    public boolean isSwapBatteries() {
        return swapBatteries;
    }

    public String getTheme() {
        return theme;
    }
}
