package org.flexlabs.widgets.dualbattery.widgetsettings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.flexlabs.widgets.dualbattery.BatteryLevel;
import org.flexlabs.widgets.dualbattery.Constants;
import org.flexlabs.widgets.dualbattery.R;
import org.flexlabs.widgets.dualbattery.storage.BatteryLevelAdapter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: ArtiomChi
 * Date: 23/11/11
 * Time: 23:00
 * 
 * Copyright 2011 Artiom Chilaru (http://flexlabs.org)
 * Some lines based on the android source files (Copyright 2006, The Android Open Source Project)
 * See: http://android.git.kernel.org/?p=platform/packages/apps/Settings.git;a=blob;f=src/com/android/widgetsettings/BatteryInfo.java
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class BatteryInfoViewManager extends BroadcastReceiver {
    public static final int DIALOG_ABOUT = 0;

    private TextView mStatus, mLevel, mScale;
    private TextView mHealth;
    private TextView mVoltage;
    private TextView mTemperature;
    private TextView mTechnology;
    private TextView mDockStatus;
    private TextView mDockLevel;
    private TextView mDockLastConnected;
    private TextView mLastCharged;
    private TableRow mRowDockLevel, mRowDockStatus, mRowDockLastConnected;
    private Activity mActivity;
    
    private boolean tempUnitsC;
    private int temperature, appWidgetId;
    
    private XYMultipleSeriesDataset mDataSet;
    private XYMultipleSeriesRenderer mRenderer;
    private XYSeries mMainSeries, mDockSeries;
    private GraphicalView mChartView;
    private LinearLayout mChartContainer;
    
    public void loadData(Activity activity, View view, int appWidgetId) {
        mActivity = activity;

        mStatus = (TextView) view.findViewById(R.id.status);
        mLevel = (TextView) view.findViewById(R.id.level);
        mScale = (TextView) view.findViewById(R.id.scale);
        mHealth = (TextView) view.findViewById(R.id.health);
        mVoltage = (TextView) view.findViewById(R.id.voltage);
        mTemperature = (TextView) view.findViewById(R.id.temperature);
        mTechnology = (TextView) view.findViewById(R.id.technology);
        mDockStatus = (TextView) view.findViewById(R.id.dock_status);
        mDockLevel = (TextView) view.findViewById(R.id.dock_level);
        mDockLastConnected = (TextView) view.findViewById(R.id.dock_last_connected);
        mLastCharged = (TextView) view.findViewById(R.id.last_charged);
        mRowDockLevel = (TableRow) view.findViewById(R.id.row_dock_level);
        mRowDockStatus = (TableRow) view.findViewById(R.id.row_dock_status);
        mRowDockLastConnected = (TableRow) view.findViewById(R.id.row_dock_lastConnected);
        if (mChartContainer != null)
            mChartContainer.removeAllViews();
        mChartContainer = (LinearLayout) view.findViewById(R.id.chart);

        this.appWidgetId = appWidgetId;
        tempUnitsC = mActivity.getSharedPreferences(Constants.SETTINGS_PREFIX + appWidgetId, Context.MODE_PRIVATE)
                .getInt(Constants.SETTING_TEMP_UNITS, Constants.SETTING_TEMP_UNITS_DEFAULT) == Constants.TEMP_UNIT_CELSIUS;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {

            mLevel.setText("" + intent.getIntExtra("level", 0));
            mScale.setText("" + intent.getIntExtra("scale", 0));
            int voltage = intent.getIntExtra("voltage", 0);
            int voltageRes = voltage > 1000
                    ? R.string.battery_info_voltage_units_mV
                    : R.string.battery_info_voltage_units_V;
            mVoltage.setText("" + voltage + " "
                    + context.getString(voltageRes));
            temperature = intent.getIntExtra("temperature", 0);
            updateTemperature();
            mTechnology.setText("" + intent.getStringExtra("technology"));

            int status = intent.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN); 
            switch (status)
            {
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    String statusString = context.getString(R.string.battery_info_status_charging);
                    int plugType = intent.getIntExtra("plugged", 0);
                    if (plugType > 0) {
                        statusString = statusString + " " + context.getString(
                                (plugType == BatteryManager.BATTERY_PLUGGED_AC)
                                        ? R.string.battery_info_status_charging_ac
                                        : R.string.battery_info_status_charging_usb);
                    }
                    mStatus.setText(statusString);
                    break;
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                    mStatus.setText(R.string.battery_info_status_discharging);
                    break;
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                    mStatus.setText(R.string.battery_info_status_not_charging);
                    break;
                case BatteryManager.BATTERY_STATUS_FULL:
                    mStatus.setText(R.string.battery_info_status_full);
                    break;
                default:
                    mStatus.setText(R.string.unknown);
                    break;
            }

            switch (intent.getIntExtra("health", BatteryManager.BATTERY_HEALTH_UNKNOWN))
            {
                case BatteryManager.BATTERY_HEALTH_GOOD:
                    mHealth.setText(R.string.battery_info_health_good);
                    break;
                case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                    mHealth.setText(R.string.battery_info_health_overheat);
                    break;
                case BatteryManager.BATTERY_HEALTH_DEAD:
                    mHealth.setText(R.string.battery_info_health_dead);
                    break;
                case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                    mHealth.setText(R.string.battery_info_health_over_voltage);
                    break;
                case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                    mHealth.setText(R.string.battery_info_health_unspecified_failure);
                    break;
                default:
                    mHealth.setText(R.string.unknown);
                    break;
            }
            
            String lastCharged;
            if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
                lastCharged = "--";
            } else if (BatteryLevel.lastCharged == null) {
                lastCharged = context.getString(R.string.unknown);
            } else {
                lastCharged = DateFormat.getDateTimeInstance().format(BatteryLevel.lastCharged);
            }
            mLastCharged.setText(lastCharged);
            
            if (intent.hasExtra("dock_status")) {
                mRowDockLevel.setVisibility(View.VISIBLE);
                mDockLevel.setText("" + intent.getIntExtra("dock_level", 0));

                mRowDockStatus.setVisibility(View.VISIBLE);
                int dockStatus = intent.getIntExtra("dock_status", Constants.DOCK_STATE_UNKNOWN); 
                switch (dockStatus)
                {
                    case Constants.DOCK_STATE_UNDOCKED:
                        mDockStatus.setText(R.string.battery_info_dock_status_undocked);
                        break;
                    case Constants.DOCK_STATE_DOCKED:
                        mDockStatus.setText(R.string.battery_info_dock_status_docked);
                        break;
                    case Constants.DOCK_STATE_CHARGING:
                        mDockStatus.setText(R.string.battery_info_dock_status_charging);
                        break;
                    case Constants.DOCK_STATE_DISCHARGING:
                        mDockStatus.setText(R.string.battery_info_dock_status_discharging);
                        break;
                    default:
                        mDockStatus.setText(R.string.unknown);
                        break;
                }
    
                mRowDockLastConnected.setVisibility(View.VISIBLE);
                String dockLastConnected;
                if (dockStatus >= Constants.DOCK_STATE_CHARGING) {
                    dockLastConnected = "--";
                } else if (BatteryLevel.dockLastConnected == null) {
                    dockLastConnected = context.getString(R.string.unknown);
                } else {
                    dockLastConnected = DateFormat.getDateTimeInstance().format(BatteryLevel.dockLastConnected);
                }
                mDockLastConnected.setText(dockLastConnected);
            }
        }
    }

    /**
     * Format a number of tenths-units as a decimal string without using a
     * conversion to float.  E.g. 347 -> "34.7"
     * @param x a whole number
     * @return x divided by 10, formatted
     */
    private String tenthsToFixedString(int x) {
        int tens = x / 10;
        return tens + "." + (x - 10 * tens);
    }
    
    public int getMenuTitle() {
        return tempUnitsC
            ? R.string.battery_info_temperature_units_c
            : R.string.battery_info_temperature_units_f;
    }

    public void updateTemperature() {
        int tempVal = temperature;
        if (!tempUnitsC)
            tempVal = tempVal * 9 / 5 + 320;
        mTemperature.setText(tenthsToFixedString(tempVal)
                + mActivity.getString(tempUnitsC
                ? R.string.battery_info_temperature_units_c
                : R.string.battery_info_temperature_units_f));
    }

    public boolean onMenuItemSelected(MenuItem item) {
        Log.d(Constants.LOG, "menu id: " + item.getItemId());
        switch (item.getItemId()) {
            case R.id.temperature :
                tempUnitsC = !tempUnitsC;
                if (Build.VERSION.SDK_INT >= 11)
                    mActivity.invalidateOptionsMenu();
                updateTemperature();
                mActivity.getSharedPreferences(Constants.SETTINGS_PREFIX + appWidgetId, Context.MODE_PRIVATE)
                        .edit()
                        .putInt(Constants.SETTING_TEMP_UNITS, tempUnitsC
                                ? Constants.TEMP_UNIT_CELSIUS
                                : Constants.TEMP_UNIT_FAHRENHEIT)
                        .commit();
                return true;

            case R.id.feedback :
                Intent intent = new Intent(Intent.ACTION_SEND, Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] { Constants.FeedbackDestination });
                intent.putExtra(Intent.EXTRA_SUBJECT, "Dual Battery Widget Feedback");
                intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(getDeviceDetails()));
                intent.setType("message/rfc822");
                mActivity.startActivity(Intent.createChooser(intent, "Email"));
                return true;

            case R.id.marketLink :
                mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.URI_MARKET)));
                return true;

            case R.id.donate_payPal :
                mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.URI_PAYPAL)));
                return true;

            case R.id.donate_market :
                break;

            case R.id.about :
            mActivity.showDialog(DIALOG_ABOUT);
            return true;
        }
        return false;
    }

    private String getDeviceDetails() {
        StringBuilder sb = new StringBuilder("<br />\n<h4>Device details:</h4>");
        sb.append("<br />\n<b>App version:</b> ").append(Constants.getVersion(mActivity));
        sb.append("<br />\n<b>Brand:</b> ").append(Build.MANUFACTURER);
        sb.append("<br />\n<b>Model:</b> ").append(Build.MODEL);
        sb.append("<br />\n<b>Device:</b> ").append(Build.DEVICE);
        sb.append("<br />\n<b>Android version:</b> ").append(Build.VERSION.RELEASE);
        sb.append("<br />\n<b>Version details:</b> ").append(Build.VERSION.INCREMENTAL);

        Intent intent = mActivity.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        Bundle extras = intent.getExtras();
        String allKeys = TextUtils.join(", ", extras.keySet());
        sb.append("<br />\n<b>Battery intent keys:</b> ").append(allKeys);
        sb.append("<br />\n<b>Is Dock supported:</b> ").append(BatteryLevel.getCurrent().is_dockFriendly());
        int dockStatus = extras.getInt("dock_status", -1);
        String dockStatusStr = "";
        switch (dockStatus) {
            case 0: dockStatusStr = " (Unknown)"; break;
            case 1: dockStatusStr = " (Undocked)"; break;
            case 2: dockStatusStr = " (Charging)"; break;
            case 3: dockStatusStr = " (Docked)"; break;
            case 4: dockStatusStr = " (Discharging)"; break;

        }
        sb.append("<br />\n<b>Battery dock status</b> ").append(extras.get("dock_status")).append(dockStatusStr);
        sb.append("<br />\n<b>Battery dock level</b> ").append(extras.get("dock_level"));

        sb.append("<br />\n<b>Kernel:</b> ").append(getFormattedKernelVersion().replace("\n", "<br />\n"));
        return sb.toString();
    }

    private String getFormattedKernelVersion() {
        String procVersionStr;

        try {
            BufferedReader reader = new BufferedReader(new FileReader("/proc/version"), 256);
            try {
                procVersionStr = reader.readLine();
            } finally {
                reader.close();
            }

            final String PROC_VERSION_REGEX =
                "\\w+\\s+" + /* ignore: Linux */
                "\\w+\\s+" + /* ignore: version */
                "([^\\s]+)\\s+" + /* group 1: 2.6.22-omap1 */
                "\\(([^\\s@]+(?:@[^\\s.]+)?)[^)]*\\)\\s+" + /* group 2: (xxxxxx@xxxxx.constant) */
                "\\(.*?(?:\\(.*?\\)).*?\\)\\s+" + /* ignore: (gcc ..) */
                "([^\\s]+)\\s+" + /* group 3: #26 */
                "(?:PREEMPT\\s+)?" + /* ignore: PREEMPT (optional) */
                "(.+)"; /* group 4: date */

            Pattern p = Pattern.compile(PROC_VERSION_REGEX);
            Matcher m = p.matcher(procVersionStr);

            if (!m.matches()) {
                Log.e(Constants.LOG, "Regex did not match on /proc/version: " + procVersionStr);
                return "Unavailable";
            } else if (m.groupCount() < 4) {
                Log.e(Constants.LOG, "Regex match on /proc/version only returned " + m.groupCount()
                        + " groups");
                return "Unavailable";
            } else {
                return (new StringBuilder(m.group(1)).append("\n").append(
                        m.group(2)).append(" ").append(m.group(3)).append("\n")
                        .append(m.group(4))).toString();
            }
        } catch (IOException e) {
            Log.e(Constants.LOG, "IO Exception when getting kernel version for Device Info screen", e);

            return "Unavailable";
        }
    }
    
    public final View.OnClickListener batterySummaryListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent i = new Intent("android.intent.action.POWER_USAGE_SUMMARY");
            mActivity.startActivity(i);
        }
    };

    public static Dialog onCreateDialog(Context context, int id) {
        switch (id) {
            case DIALOG_ABOUT :
                final SpannableString s = new SpannableString(
                        context.getString(R.string.about_full) +
                        context.getString(R.string.about_translations));
                Linkify.addLinks(s, Linkify.ALL);

                Dialog result = new AlertDialog.Builder(context)
                        .setTitle(R.string.propTitle_About)
                        .setMessage(s)
                        .setPositiveButton("OK", null)
                        .create();
                result.show();
                ((TextView)result.findViewById(android.R.id.message))
                        .setMovementMethod(LinkMovementMethod.getInstance());
                return result;

            default: return null;
        }
    }
    
    public void initChart() {
        if (mChartView == null) {
            mRenderer = new XYMultipleSeriesRenderer();
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

            mDataSet = new XYMultipleSeriesDataset();
            mMainSeries = new XYSeries(mActivity.getString(R.string.battery_main));
            mDataSet.addSeries(mMainSeries);
            XYSeriesRenderer mMainRenderer = new XYSeriesRenderer();
            mMainRenderer.setColor(Color.GREEN);
            mRenderer.addSeriesRenderer(mMainRenderer);

            if (BatteryLevel.getCurrent().is_dockFriendly()) {
                mDockSeries = new XYSeries(mActivity.getString(R.string.battery_dock));
                mDataSet.addSeries(mDockSeries);
                XYSeriesRenderer mDockRenderer = new XYSeriesRenderer();
                mDockRenderer.setColor(Color.CYAN);
                mRenderer.addSeriesRenderer(mDockRenderer);
            }

            mChartView = ChartFactory.getTimeChartView(mActivity, mDataSet, mRenderer, null);
        }
    }

    private boolean chartPopulated = false;
    public void buildChart() {
        initChart();
        if (mChartContainer.getChildCount() == 0) {
            mChartContainer.addView(mChartView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                  ViewGroup.LayoutParams.FILL_PARENT));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                mChartContainer.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        
        if (chartPopulated) {
            mChartView.repaint();
        } else {
            // populate chart
            chartPopulated = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    BatteryLevelAdapter adapter = new BatteryLevelAdapter(mActivity);
                    adapter.open();
                    Cursor c = adapter.getRecentEntries();
                    int oldLevel = -1, oldDockLevel = -1;
                    boolean dockSupported = BatteryLevel.getCurrent().is_dockFriendly();

                    long time = System.currentTimeMillis();
                    boolean mainSkipped = false, dockSkipped = false;
                    if (c.moveToFirst())
                        do {
                            time = c.getLong(BatteryLevelAdapter.ORD_TIME);
                            int level = c.getInt(BatteryLevelAdapter.ORD_LEVEL);
                            int dock_status = c.getInt(BatteryLevelAdapter.ORD_DOCK_STATUS);
                            int dock_level = c.getInt(BatteryLevelAdapter.ORD_DOCK_LEVEL);

                            mainSkipped = level == oldLevel;
                            if (!mainSkipped) {
                                mMainSeries.add(time, level);
                                oldLevel = level;
                            }
                            if (dockSupported && dock_status > 1) {
                                dockSkipped = dock_level == oldDockLevel;
                                if (!dockSkipped) {
                                    mDockSeries.add(time, dock_level);
                                    oldDockLevel = dock_level;
                                }
                            }
                        } while (c.moveToNext());
                    adapter.close();
                    if (mainSkipped)
                        mMainSeries.add(time, oldLevel);
                    if (dockSkipped)
                        mDockSeries.add(time, oldDockLevel);

                    mChartView.repaint();
                }
            }).start();
        }
    }
}
