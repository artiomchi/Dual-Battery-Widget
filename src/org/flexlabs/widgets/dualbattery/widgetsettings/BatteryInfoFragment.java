package org.flexlabs.widgets.dualbattery.widgetsettings;

import android.app.Fragment;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.*;
import android.view.Menu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import org.flexlabs.widgets.dualbattery.R;

/**
 * Created by IntelliJ IDEA.
 * User: ArtiomChi
 * Date: 17/06/11
 * Time: 18:53
 *
 * Copyright 2011 Artiom Chilaru (http://flexlabs.org)
 * Some lines based on the android source files (Copyright 2006, The Android Open Source Project)
 * See: http://android.git.kernel.org/?p=platform/packages/apps/Settings.git;a=blob;f=src/com/android/widgetsettings/BatteryInfo.java
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
public class BatteryInfoFragment extends Fragment {
    private IntentFilter mIntentFilter;
    private BatteryInfoViewManager _batteryInfoViewManager;
    private BatteryInfoViewManager batteryInfoViewManager() {
        if (_batteryInfoViewManager == null)
            _batteryInfoViewManager = ((WidgetTabbedActivity)getActivity()).batteryInfoViewManager;
        return _batteryInfoViewManager;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.battery_info_table, null);
        WidgetTabbedActivity activity = (WidgetTabbedActivity)getActivity();
        int appWidgetId = activity.appWidgetId;
        view.findViewById(R.id.batterySummary).setOnClickListener(batteryInfoViewManager().batterySummaryListener);
        batteryInfoViewManager().loadData(getActivity(), view, appWidgetId);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(batteryInfoViewManager());
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(batteryInfoViewManager(), mIntentFilter);
        batteryInfoViewManager().buildChart();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.widget_batteryinfo, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.temperature).setTitle(batteryInfoViewManager().getMenuTitle());
    }
}
