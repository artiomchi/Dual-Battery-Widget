package org.flexlabs.widgets.dualbattery;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Created by IntelliJ IDEA.
 * User: ArtiomChi
 * Date: 13/06/11
 * Time: 21:34
 */
public class Constants {
    public static final String LOG = "FlexLabs.DBW";

    public static final String EXTRA_WIDGET_IDS = "widgetIds";
    public static final String EXTRA_WIDGET_OLD = "WidgetAlreadySetup";
    public static final String SETTINGS_PREFIX = "widgetPref_";
    public static final String FeedbackDestination = "FlexLabs <android@flexlabs.org>";
    public static final String STACKTRACE_FILENAME = "stacktrace.log";

    public static final int DOCK_STATE_UNKNOWN = 0;
    public static final int DOCK_STATE_UNDOCKED = 1;
    public static final int DOCK_STATE_CHARGING = 2;
    public static final int DOCK_STATE_DOCKED = 3;
    public static final int DOCK_STATE_DISCHARGING = 4;

    public static final String SETTING_VERSION = "version";
    public static final int SETTING_VERSION_CURRENT = 2;
    public static final String SETTING_ALWAYS_SHOW_DOCK = "alwaysShowDock";
    public static final boolean SETTING_ALWAYS_SHOW_DOCK_DEFAULT = true;

    public static final int TEXT_POS_INVISIBLE = 0;
    public static final int TEXT_POS_TOP = 1;
    public static final int TEXT_POS_MIDDLE = 2;
    public static final int TEXT_POS_BOTTOM = 3;
    public static final int TEXT_POS_ABOVE = 4;
    public static final int TEXT_POS_BELOW = 5;
    public static final String SETTING_TEXT_POS = "textPosition";
    public static final int SETTING_TEXT_POS_DEFAULT = TEXT_POS_MIDDLE;

    public static final String SETTING_TEXT_SIZE = "textSize";
    public static final int SETTING_TEXT_SIZE_DEFAULT = 14;
    public static final String SETTING_SHOW_NOT_DOCKED = "showNotDockedMessage";
    public static final boolean SETTING_SHOW_NOT_DOCKED_DEFAULT = true;

    public static final int BATTERY_SELECTION_BOTH = 3;
    public static final int BATTERY_SELECTION_MAIN = 1;
    public static final int BATTERY_SELECTION_DOCK = 2;
    public static final String SETTING_SHOW_SELECTION = "batterySelection";
    public static final int SETTING_SHOW_SELECTION_DEFAULT = BATTERY_SELECTION_BOTH;

    public static final int TEXT_COLOR_WHITE = 0;
    public static final int TEXT_COLOR_BLACK = 1;
    public static final String SETTING_TEXT_COLOR = "textColor";
    public static final int SETTING_TEXT_COLOR_DEFAULT = TEXT_COLOR_WHITE;

    public static final int MARGIN_NONE = 0;
    public static final int MARGIN_TOP = 1;
    public static final int MARGIN_BOTTOM = 2;
    public static final int MARGIN_BOTH = 3;
    public static final String SETTING_MARGIN = "marginLocation";
    public static final int SETTING_MARGIN_DEFAULT = MARGIN_NONE;

    public static final String SETTING_SHOW_LABEL = "showBatteryLabel";
    public static final boolean SETTING_SHOW_LABEL_DEFAULT = false;
    public static final String SETTING_SHOW_OLD_DOCK = "showOldDockStatus";
    public static final boolean SETTING_SHOW_OLD_DOCK_DEFAULT = false;
    public static final int TEMP_UNIT_CELSIUS = 0;
    public static final int TEMP_UNIT_FAHRENHEIT = 1;
    public static final String SETTING_TEMP_UNITS_NEW = "tempUnitsNew";
    public static final String SETTING_TEMP_UNITS = "tempUnits";
    public static final int SETTING_TEMP_UNITS_DEFAULT = TEMP_UNIT_CELSIUS;

    public static String getVersion(Context context) {
        String result;
        try {
            String pkg = context.getPackageName();
            result = context.getPackageManager().getPackageInfo(pkg, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            result = "?";
        }
        return result;
    }
}
