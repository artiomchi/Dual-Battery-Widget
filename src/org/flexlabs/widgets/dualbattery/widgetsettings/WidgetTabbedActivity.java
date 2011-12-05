/*
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

package org.flexlabs.widgets.dualbattery.widgetsettings;

import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import org.flexlabs.widgets.dualbattery.BatteryLevel;
import org.flexlabs.widgets.dualbattery.BatteryWidgetUpdater;
import org.flexlabs.widgets.dualbattery.Constants;
import org.flexlabs.widgets.dualbattery.R;

import java.util.List;

public class WidgetTabbedActivity extends PreferenceActivity {
    public int appWidgetId;
    public BatteryInfoViewManager batteryInfoViewManager = new BatteryInfoViewManager();

    @Override
    public void onBuildHeaders(List<Header> target) {
        super.onBuildHeaders(target);
        Bundle extras = getIntent().getExtras();
        appWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            loadHeadersFromResource(R.xml.widget_properties_headers, target);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        batteryInfoViewManager.onDestroy();
    }

    public static class PropertiesFragment extends PreferenceFragment {
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            WidgetTabbedActivity activity = (WidgetTabbedActivity)getActivity();
            int appWidgetId = activity.appWidgetId;

            getPreferenceManager().setSharedPreferencesName(Constants.SETTINGS_PREFIX + appWidgetId);
            addPreferencesFromResource(R.xml.widget_properties_general);
            if (BatteryLevel.getCurrent().is_dockFriendly()) {
                addPreferencesFromResource(R.xml.widget_properties_dock);
            }
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        return batteryInfoViewManager.onCreateDialog(this, id);
    }

    @Override
    protected void onStop() {
        super.onStop();
        BatteryWidgetUpdater.updateWidget(this, appWidgetId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
            finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.widget, menu);
        if (!Constants.HAS_MARKET_BILLING)
            menu.findItem(R.id.donate_market).setEnabled(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return batteryInfoViewManager.onMenuItemSelected(item) ||
               super.onOptionsItemSelected(item);
    }
}
