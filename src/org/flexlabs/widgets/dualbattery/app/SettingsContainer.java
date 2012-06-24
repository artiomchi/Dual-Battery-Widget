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

package org.flexlabs.widgets.dualbattery.app;

import android.content.Context;
import android.content.SharedPreferences;
import org.flexlabs.widgets.dualbattery.Constants;

public class SettingsContainer {
    private int version;
    private boolean showNotificationIcon;

    public SettingsContainer(Context context) {
        SharedPreferences pref = context.getSharedPreferences(Constants.SETTINGS_FILE, Context.MODE_PRIVATE);
        version = pref.getInt(Constants.SETTING_VERSION, 1);
        showNotificationIcon = pref.getBoolean(Constants.SETTING_NOTIFICATION_ICON, Constants.SETTING_NOTIFICATION_ICON_DEFAULT);
    }

    public SettingsContainer(Context context, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        SharedPreferences pref = context.getSharedPreferences(Constants.SETTINGS_FILE, Context.MODE_PRIVATE);
        version = pref.getInt(Constants.SETTING_VERSION, 1);
        showNotificationIcon = pref.getBoolean(Constants.SETTING_NOTIFICATION_ICON, Constants.SETTING_NOTIFICATION_ICON_DEFAULT);
        if (listener != null)
            pref.registerOnSharedPreferenceChangeListener(listener);
    }

    public int getVersion() {
        return version;
    }

    public boolean isShowNotificationIcon() {
        return showNotificationIcon;
    }
}
