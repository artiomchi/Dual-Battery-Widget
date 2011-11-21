package org.flexlabs.widgets.dualbattery.widgetsettings;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import org.flexlabs.widgets.dualbattery.BatteryLevel;
import org.flexlabs.widgets.dualbattery.Constants;
import org.flexlabs.widgets.dualbattery.R;

/**
 * Created by IntelliJ IDEA.
 * User: ArtiomChi
 * Date: 17/06/11
 * Time: 18:44
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
public class WidgetPropertiesFragment extends PreferenceFragment {
    private int appWidgetId;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        WidgetPropertiesActivity activity = (WidgetPropertiesActivity)getActivity();
        appWidgetId = activity.appWidgetId;

        getPreferenceManager().setSharedPreferencesName(Constants.SETTINGS_PREFIX + appWidgetId);
        addPreferencesFromResource(R.xml.widget_properties_general);
        if (BatteryLevel.getCurrent().is_dockFriendly()) {
            addPreferencesFromResource(R.xml.widget_properties_dock);
        }
    }
}
