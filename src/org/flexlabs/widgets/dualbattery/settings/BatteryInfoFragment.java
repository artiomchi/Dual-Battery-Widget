package org.flexlabs.widgets.dualbattery.settings;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.*;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.flexlabs.widgets.dualbattery.R;

/**
 * Created by IntelliJ IDEA.
 * User: Flexer
 * Date: 17/06/11
 * Time: 18:53
 * Based on: http://android.git.kernel.org/?p=platform/packages/apps/Settings.git;a=blob;f=src/com/android/settings/BatteryInfo.java
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
    private TextView mAwakeBattery;
    private TextView mAwakePlugged;
    private TextView mScreenOn;
    //private IBatteryStats mBatteryStats;
    //private IPowerManager mScreenStats;

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
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        /*mStatus = (TextView)findViewById(R.id.status);
        mLevel = (TextView)findViewById(R.id.level);
        mScale = (TextView)findViewById(R.id.scale);
        mHealth = (TextView)findViewById(R.id.health);
        mTechnology = (TextView)findViewById(R.id.technology);
        mVoltage = (TextView)findViewById(R.id.voltage);
        mTemperature = (TextView)findViewById(R.id.temperature);
        mUptime = (TextView) findViewById(R.id.uptime);
        mAwakeBattery = (TextView) findViewById(R.id.awakeBattery);
        mAwakePlugged = (TextView) findViewById(R.id.awakePlugged);
        mScreenOn = (TextView) findViewById(R.id.screenOn); */

        // Get awake time plugged in and on battery
        //mBatteryStats = IBatteryStats.Stub.asInterface(ServiceManager.getService("batteryinfo"));
        //mScreenStats = IPowerManager.Stub.asInterface(ServiceManager.getService(POWER_SERVICE));
        mHandler.sendEmptyMessageDelayed(EVENT_TICK, 1000);

        getActivity().registerReceiver(mIntentReceiver, mIntentFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.battery_info, null);
        mStatus = (TextView)view.findViewById(R.id.status);
        mLevel = (TextView)view.findViewById(R.id.level);
        mScale = (TextView)view.findViewById(R.id.scale);
        mHealth = (TextView)view.findViewById(R.id.health);
        mTechnology = (TextView)view.findViewById(R.id.technology);
        mVoltage = (TextView)view.findViewById(R.id.voltage);
        mTemperature = (TextView)view.findViewById(R.id.temperature);
        mUptime = (TextView) view.findViewById(R.id.uptime);
        mAwakeBattery = (TextView) view.findViewById(R.id.awakeBattery);
        mAwakePlugged = (TextView) view.findViewById(R.id.awakePlugged);
        mScreenOn = (TextView) view.findViewById(R.id.screenOn);

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

        /*if (mBatteryStats != null) {
            try {
                long awakeTimeBattery = mBatteryStats.getAwakeTimeBattery();
                long awakeTimePluggedIn = mBatteryStats.getAwakeTimePlugged();
                mAwakeBattery.setText(DateUtils.formatElapsedTime(awakeTimeBattery / 1000)
                        + " (" + (100 * awakeTimeBattery / uptime) + "%)");
                mAwakePlugged.setText(DateUtils.formatElapsedTime(awakeTimePluggedIn / 1000)
                        + " (" + (100 * awakeTimePluggedIn / uptime) + "%)");
            } catch (RemoteException re) {
                mAwakeBattery.setText("Unknown");
                mAwakePlugged.setText("Unknown");
            }
        }
        if (mScreenStats != null) {
            try {
                long screenOnTime = mScreenStats.getScreenOnTime();
                mScreenOn.setText(DateUtils.formatElapsedTime(screenOnTime / 1000));
            } catch (RemoteException re) {
                mScreenOn.setText("Unknown");
            }
        }*/
    }
}
