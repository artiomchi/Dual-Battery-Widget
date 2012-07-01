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

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.flexlabs.widgets.dualbattery.BatteryLevel;
import org.flexlabs.widgets.dualbattery.Constants;
import org.flexlabs.widgets.dualbattery.R;
import org.flexlabs.widgets.dualbattery.ui.PreferenceListFragment;

public class SettingsFragment extends PreferenceListFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b) {
        View view = super.onCreateView(inflater, container, b);
        Log.d(Constants.LOG, "onCreateView?");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(Constants.LOG, "onActivityCreated?");
        getPreferenceManager().setSharedPreferencesName(Constants.SETTINGS_FILE);
        if (BatteryLevel.getCurrent().is_dockFriendly()) {
            addPreferencesFromResource(R.xml.settings_0_dock_notif);
        }
        addPreferencesFromResource(R.xml.settings_1_general);
    }
}
