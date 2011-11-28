package org.flexlabs.widgets.dualbattery;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Created by IntelliJ IDEA.
 * User: ArtiomChi
 * Date: 13/06/11
 * Time: 21:34
 *
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
public class Constants {
    public static final String LOG = "FlexLabs.DBW";

    public static final String EXTRA_WIDGET_IDS = "widgetIds";
    public static final String SETTINGS_PREFIX = "widgetPref_";
    public static final String FeedbackDestination = "FlexLabs <android@flexlabs.org>";

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
    public static final boolean SETTING_SHOW_OLD_DOCK_DEFAULT = true;
    public static final int TEMP_UNIT_CELSIUS = 0;
    public static final int TEMP_UNIT_FAHRENHEIT = 1;
    public static final String SETTING_TEMP_UNITS = "tempUnitsInt";
    public static final int SETTING_TEMP_UNITS_DEFAULT = TEMP_UNIT_CELSIUS;

    public static final String URI_PAYPAL = "https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=6UHRDDAPAMSTL&lc=GB&item_name=Donation%20for%20Artiom%20Chilaru&currency_code=GBP&bn=PP%2dDonationsBF%3abtn_donate_SM%2egif%3aNonHosted";
    public static final String URI_MARKET = "https://market.android.com/details?id=org.flexlabs.widgets.dualbattery";

    public static final String MIXPANEL_KEY = "bce7bb0bd38a73dc88daa8b0588138cb";
    public static final String MIXPANEL_EVENT_WIDGETSETTINGS = "widgetSettings";
    //public static final String MIXPANEL_EVENT_

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
