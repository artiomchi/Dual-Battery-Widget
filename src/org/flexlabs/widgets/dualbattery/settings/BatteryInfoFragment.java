package org.flexlabs.widgets.dualbattery.settings;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.*;
import android.text.format.DateUtils;
import android.util.TimeUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.flexlabs.widgets.dualbattery.BatteryApplication;
import org.flexlabs.widgets.dualbattery.BatteryMonitorService;
import org.flexlabs.widgets.dualbattery.Constants;
import org.flexlabs.widgets.dualbattery.R;

import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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
    private final String tenthsToFixedString(int x) {
        int tens = x / 10;
        return new String("" + tens + "." + (x - 10*tens));
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
                mVoltage.setText("" + intent.getIntExtra("voltage", 0) + " "
                        + getString(R.string.battery_info_voltage_units));
                mTemperature.setText("" + tenthsToFixedString(intent.getIntExtra("temperature", 0))
                        + getString(R.string.battery_info_temperature_units));
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
                    statusString = getString(R.string.battery_info_status_unknown);
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
                    healthString = getString(R.string.battery_info_health_unknown);
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
                    dockStatusString = getString(R.string.battery_info_dock_status_unknown);
                }
                mDockStatus.setText(dockStatusString);

                mDockLevel.setText("" + intent.getIntExtra("dock_level", 0));

                String dockLastConnected;
                if (dockStatus >= Constants.DOCK_STATE_CHARGING) {
                    dockLastConnected = "--";
                } else if (BatteryMonitorService.dockLastConnected == null) {
                    dockLastConnected = getString(R.string.battery_info_dock_last_connected_unknown);
                } else {
                    dockLastConnected = DateFormat.getDateTimeInstance().format(BatteryMonitorService.dockLastConnected);
                }
                mDockLastConnected.setText(dockLastConnected);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        mHandler.sendEmptyMessageDelayed(EVENT_TICK, 1000);

        getActivity().registerReceiver(mIntentReceiver, mIntentFilter);
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

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        return view;
    }

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
