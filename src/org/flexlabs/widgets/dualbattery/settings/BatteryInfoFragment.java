package org.flexlabs.widgets.dualbattery.settings;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.*;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.flexlabs.widgets.dualbattery.BatteryLevelAdapter;
import org.flexlabs.widgets.dualbattery.BatteryMonitorService;
import org.flexlabs.widgets.dualbattery.Constants;
import org.flexlabs.widgets.dualbattery.R;

import java.text.DateFormat;

/**
 * Created by IntelliJ IDEA.
 * User: Flexer
 * Date: 17/06/11
 * Time: 18:53
 * Source partially based on: http://android.git.kernel.org/?p=platform/packages/apps/Settings.git;a=blob;f=src/com/android/settings/BatteryInfo.java
 */
/* Copyright from the original code
**
** Copyright 2006, The Android Open Source Project
**
** Licensed under the Apache License, Version 2.0 (the "License");
** you may not use this file except in compliance with the License.
** You may obtain a copy of the License at
**
**     http://www.apache.org/licenses/LICENSE-2.0
**
** Unless required by applicable law or agreed to in writing, software
** distributed under the License is distributed on an "AS IS" BASIS,
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
** See the License for the specific language governing permissions and
** limitations under the License.
*/
public class BatteryInfoFragment extends Fragment {
    private TextView mStatus;
    private TextView mLevel;
    private TextView mScale;
    private TextView mHealth;
    private TextView mVoltage;
    private TextView mTemperature;
    private TextView mTechnology;
    private TextView mUptime;
    private TextView mDockStatus;
    private TextView mDockLevel;
    private TextView mDockLastConnected;
    private TextView mLastCharged;
    private boolean tempUnitsC;

    private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
    private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
    private XYSeries mMainSeries, mDockSeries;
    private XYSeriesRenderer mMainRenderer, mDockRenderer;
    private GraphicalView mChartView;
    private LinearLayout mChartContainer;

    private static final int EVENT_TICK = 1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EVENT_TICK:
                    updateBatteryStats();
                    sendEmptyMessageDelayed(EVENT_TICK, 1000);
                    break;
            }
        }
    };

    /**
     * Format a number of tenths-units as a decimal string without using a
     * conversion to float.  E.g. 347 -> "34.7"
     */
    private String tenthsToFixedString(int x) {
        int tens = x / 10;
        return "" + tens + "." + (x - 10 * tens);
    }

   /**
    *Listens for intent broadcasts
    */
    private IntentFilter mIntentFilter;

    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                int plugType = intent.getIntExtra("plugged", 0);

                mLevel.setText("" + intent.getIntExtra("level", 0));
                mScale.setText("" + intent.getIntExtra("scale", 0));
                int voltage = intent.getIntExtra("voltage", 0);
                int voltageRes = voltage > 1000
                        ? R.string.battery_info_voltage_units_mV
                        : R.string.battery_info_voltage_units_V;
                mVoltage.setText("" + voltage + " "
                        + getString(voltageRes));
                int tempVal = intent.getIntExtra("temperature", 0);
                if (!tempUnitsC)
                    tempVal = tempVal * 9 / 5 + 320;
                mTemperature.setText("" + tenthsToFixedString(tempVal)
                        + getString(tempUnitsC
                            ? R.string.battery_info_temperature_units_c
                            : R.string.battery_info_temperature_units_f));
                mTechnology.setText("" + intent.getStringExtra("technology"));

                int status = intent.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN);
                String statusString;
                if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
                    statusString = getString(R.string.battery_info_status_charging);
                    if (plugType > 0) {
                        statusString = statusString + " " + getString(
                                (plugType == BatteryManager.BATTERY_PLUGGED_AC)
                                        ? R.string.battery_info_status_charging_ac
                                        : R.string.battery_info_status_charging_usb);
                    }
                } else if (status == BatteryManager.BATTERY_STATUS_DISCHARGING) {
                    statusString = getString(R.string.battery_info_status_discharging);
                } else if (status == BatteryManager.BATTERY_STATUS_NOT_CHARGING) {
                    statusString = getString(R.string.battery_info_status_not_charging);
                } else if (status == BatteryManager.BATTERY_STATUS_FULL) {
                    statusString = getString(R.string.battery_info_status_full);
                } else {
                    statusString = getString(R.string.unknown);
                }
                mStatus.setText(statusString);

                int health = intent.getIntExtra("health", BatteryManager.BATTERY_HEALTH_UNKNOWN);
                String healthString;
                if (health == BatteryManager.BATTERY_HEALTH_GOOD) {
                    healthString = getString(R.string.battery_info_health_good);
                } else if (health == BatteryManager.BATTERY_HEALTH_OVERHEAT) {
                    healthString = getString(R.string.battery_info_health_overheat);
                } else if (health == BatteryManager.BATTERY_HEALTH_DEAD) {
                    healthString = getString(R.string.battery_info_health_dead);
                } else if (health == BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE) {
                    healthString = getString(R.string.battery_info_health_over_voltage);
                } else if (health == BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE) {
                    healthString = getString(R.string.battery_info_health_unspecified_failure);
                } else {
                    healthString = getString(R.string.unknown);
                }
                mHealth.setText(healthString);

                int dockStatus = intent.getIntExtra("dock_status", Constants.DOCK_STATE_UNKNOWN);
                String dockStatusString;
                if (dockStatus == Constants.DOCK_STATE_UNDOCKED) {
                    dockStatusString = getString(R.string.battery_info_dock_status_undocked);
                } else if (dockStatus == Constants.DOCK_STATE_DOCKED) {
                    dockStatusString = getString(R.string.battery_info_dock_status_docked);
                } else if (dockStatus == Constants.DOCK_STATE_CHARGING) {
                    dockStatusString = getString(R.string.battery_info_dock_status_charging);
                } else if (dockStatus == Constants.DOCK_STATE_DISCHARGING) {
                    dockStatusString = getString(R.string.battery_info_dock_status_discharging);
                } else {
                    dockStatusString = getString(R.string.unknown);
                }
                mDockStatus.setText(dockStatusString);

                mDockLevel.setText("" + intent.getIntExtra("dock_level", 0));

                String dockLastConnected;
                if (dockStatus >= Constants.DOCK_STATE_CHARGING) {
                    dockLastConnected = "--";
                } else if (BatteryMonitorService.dockLastConnected == null) {
                    dockLastConnected = getString(R.string.unknown);
                } else {
                    dockLastConnected = DateFormat.getDateTimeInstance().format(BatteryMonitorService.dockLastConnected);
                }
                mDockLastConnected.setText(dockLastConnected);

                String lastCharged;
                if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
                    lastCharged = "--";
                } else if (BatteryMonitorService.lastCharged == null) {
                    lastCharged = getString(R.string.unknown);
                } else {
                    lastCharged = DateFormat.getDateTimeInstance().format(BatteryMonitorService.lastCharged);
                }
                mLastCharged.setText(lastCharged);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        tempUnitsC = ((WidgetPropertiesActivity)getActivity()).tempUnitsC;
        mHandler.sendEmptyMessageDelayed(EVENT_TICK, 1000);

        getActivity().registerReceiver(mIntentReceiver, mIntentFilter);

        if (mChartView == null) {
            mChartView = ChartFactory.getTimeChartView(getActivity(), mDataset, mRenderer, null);
            mChartContainer.addView(mChartView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                  ViewGroup.LayoutParams.FILL_PARENT));
        } else {
            mChartView.repaint();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.battery_info_table, null);
        mStatus = (TextView)view.findViewById(R.id.status);
        mLevel = (TextView)view.findViewById(R.id.level);
        mScale = (TextView)view.findViewById(R.id.scale);
        mHealth = (TextView)view.findViewById(R.id.health);
        mTechnology = (TextView)view.findViewById(R.id.technology);
        mVoltage = (TextView)view.findViewById(R.id.voltage);
        mTemperature = (TextView)view.findViewById(R.id.temperature);
        mUptime = (TextView) view.findViewById(R.id.uptime);
        mDockStatus = (TextView) view.findViewById(R.id.dock_status);
        mDockLevel = (TextView) view.findViewById(R.id.dock_level);
        mDockLastConnected = (TextView) view.findViewById(R.id.dock_last_connected);
        mLastCharged = (TextView) view.findViewById(R.id.last_charged);
        mChartContainer = (LinearLayout) view.findViewById(R.id.chart);

        view.findViewById(R.id.batteryInfo).setOnClickListener(batteryInfoListener);
        view.findViewById(R.id.batterySummary).setOnClickListener(batterySummaryListener);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);

        mRenderer.setAxisTitleTextSize(16);
        mRenderer.setChartTitleTextSize(20);
        mRenderer.setLabelsTextSize(15);
        mRenderer.setLegendTextSize(15);
        mRenderer.setMargins(new int[]{20, 30, 15, 0});
        mRenderer.setYAxisMin(0);
        mRenderer.setYAxisMax(100);
        mRenderer.setPanEnabled(true, false);
        mRenderer.setZoomEnabled(true, false);
        mRenderer.setShowGrid(true);
        mRenderer.setZoomButtonsVisible(false);

        mMainSeries = new XYSeries(getString(R.string.battery_main));
        mDataset.addSeries(mMainSeries);
        mMainRenderer = new XYSeriesRenderer();
        mMainRenderer.setColor(Color.GREEN);
        mRenderer.addSeriesRenderer(mMainRenderer);

        mDockSeries = new XYSeries(getString(R.string.battery_dock));
        mDataset.addSeries(mDockSeries);
        mDockRenderer = new XYSeriesRenderer();
        mDockRenderer.setColor(Color.CYAN);
        mRenderer.addSeriesRenderer(mDockRenderer);

        BatteryLevelAdapter adapter = new BatteryLevelAdapter(getActivity());
        adapter.open();
        Cursor c = adapter.getRecentEntries();

        if (c.moveToFirst())
            do {
                long time = c.getLong(BatteryLevelAdapter.ORD_TIME);
                int level = c.getInt(BatteryLevelAdapter.ORD_LEVEL);
                int dock_status = c.getInt(BatteryLevelAdapter.ORD_DOCK_STATUS);
                int dock_level = c.getInt(BatteryLevelAdapter.ORD_DOCK_LEVEL);

                mMainSeries.add(time, level);
                if (dock_status > 1)
                    mDockSeries.add(time, dock_level);

            } while (c.moveToNext());

        if (mChartView != null)
            mChartView.repaint();

        return view;
    }

    private final View.OnClickListener batteryInfoListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.setClassName("com.android.settings", "com.android.settings.BatteryInfo");
            startActivity(i);
        }
    };

    private final View.OnClickListener batterySummaryListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent i = new Intent("android.intent.action.POWER_USAGE_SUMMARY");
            startActivity(i);
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeMessages(EVENT_TICK);

        // we are no longer on the screen stop the observers
        getActivity().unregisterReceiver(mIntentReceiver);
    }

    private void updateBatteryStats() {
        long uptime = SystemClock.elapsedRealtime();
        mUptime.setText(DateUtils.formatElapsedTime(uptime / 1000));
    }
}
