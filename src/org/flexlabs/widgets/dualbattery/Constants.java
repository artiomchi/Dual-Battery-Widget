package org.flexlabs.widgets.dualbattery;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Created by IntelliJ IDEA.
 * User: Artiom Chilaru
 * Date: 13/06/11
 * Time: 21:34
 */
public class Constants {
    public static final String ACTION_BATTERY_UPDATE = "org.flexlabs.action.BATTERY_UPDATED";
    public static final String ACTION_SETTINGS_UPDATE = "org.flexlabs.action.dualbattery.SETTINGS_UPDATED";
    public static final String EXTRA_WIDGET_OLD = "WidgetAlreadySetup";
    public static final String SETTINGS_PREFIX = "widgetPref_";
    public static final String FeedbackDestination = "Android @ FlexLabs <android@flexlabs.org>";
    public static final String STACKTRACE_FILENAME = "stacktrace.log";

    public static final int DOCK_STATE_UNKNOWN = 0;
    public static final int DOCK_STATE_UNDOCKED = 1;
    public static final int DOCK_STATE_CHARGING = 2;
    public static final int DOCK_STATE_DOCKED = 3;
    public static final int DOCK_STATE_DISCHARGING = 4;

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
