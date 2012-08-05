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

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.flexlabs.widgets.dualbattery.BatteryLevel;
import org.flexlabs.widgets.dualbattery.R;
import org.flexlabs.widgets.dualbattery.service.MonitorService;
import org.flexlabs.widgets.dualbattery.storage.BatteryLevelAdapter;

public class BatteryHistoryActivity extends SherlockActivity implements ActionBar.TabListener {
    private XYSeries mMainSeries, mDockSeries;
    private GraphicalView mChartView;
    private LinearLayout mChartContainer;
    private int days = 3, defaultDays;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this, MonitorService.class));

        setContentView(R.layout.battery_history);
        if (mChartContainer != null)
            mChartContainer.removeAllViews();
        mChartContainer = (LinearLayout)findViewById(R.id.chart);

        defaultDays = new SettingsContainer(this).getDefaultDaysTab();
        initChart();
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        addTab(3, R.string.sett_defaultDaysTab_3);
        addTab(7, R.string.sett_defaultDaysTab_7);
        addTab(30, R.string.sett_defaultDaysTab_30);
    }

    private ActionBar.Tab addTab(int days, int label) {
        ActionBar.Tab tab = getSupportActionBar().newTab();
        tab.setText(label);
        tab.setTag(days);
        tab.setTabListener(this);
        getSupportActionBar().addTab(tab);

        if (days == defaultDays)
            getSupportActionBar().selectTab(tab);
        return tab;
    }

    @Override
    protected void onResume() {
        super.onResume();
        buildChart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction transaction) {
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction transaction) {
        int newDays = (Integer)tab.getTag();
        if (newDays != days) {
            days = newDays;
            mMainSeries.clear();
            if (mDockSeries != null)
                mDockSeries.clear();
            chartPopulated = false;
        }

        repopulateChart();
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction transaction) {
    }

    public void initChart() {
        if (mChartView == null) {
            XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
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

            XYMultipleSeriesDataset mDataSet = new XYMultipleSeriesDataset();
            mMainSeries = new XYSeries(getString(R.string.battery_main));
            mDataSet.addSeries(mMainSeries);
            XYSeriesRenderer mMainRenderer = new XYSeriesRenderer();
            mMainRenderer.setColor(Color.GREEN);
            mRenderer.addSeriesRenderer(mMainRenderer);

            if (BatteryLevel.getCurrent().is_dockFriendly()) {
                mDockSeries = new XYSeries(getString(R.string.battery_dock));
                mDataSet.addSeries(mDockSeries);
                XYSeriesRenderer mDockRenderer = new XYSeriesRenderer();
                mDockRenderer.setColor(Color.CYAN);
                mRenderer.addSeriesRenderer(mDockRenderer);
            }

            mChartView = ChartFactory.getTimeChartView(this, mDataSet, mRenderer, null);
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
             repopulateChart();
        }
    }

    public void repopulateChart() {
        // populate chart
        chartPopulated = true;
        // TODO: fix this, seriously
        //new Thread(new Runnable() {
        //    @Override
        //    public void run() {
        BatteryLevelAdapter adapter = new BatteryLevelAdapter(BatteryHistoryActivity.this);
        adapter.openRead();
        Cursor c = adapter.getRecentEntries(days);
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
        c.close();
        adapter.close();
        if (mainSkipped)
            mMainSeries.add(time, oldLevel);
        if (dockSkipped)
            mDockSeries.add(time, oldDockLevel);

        mChartView.repaint();
        //    }
        //}).start();
    }
}