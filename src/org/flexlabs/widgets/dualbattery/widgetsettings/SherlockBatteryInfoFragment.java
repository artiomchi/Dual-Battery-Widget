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

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import org.flexlabs.widgets.dualbattery.R;

public class SherlockBatteryInfoFragment extends SherlockFragment {
    private IntentFilter mIntentFilter;
    private BatteryInfoViewManager _batteryInfoViewManager;
    private BatteryInfoViewManager batteryInfoViewManager() {
        if (_batteryInfoViewManager == null)
            _batteryInfoViewManager = ((WidgetActivity)getSherlockActivity()).batteryInfoViewManager;
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
        WidgetActivity activity = (WidgetActivity)getActivity();
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
