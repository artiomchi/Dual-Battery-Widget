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

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TabPageIndicator;
import org.flexlabs.widgets.dualbattery.BatteryWidgetUpdater;
import org.flexlabs.widgets.dualbattery.Constants;
import org.flexlabs.widgets.dualbattery.R;
import org.flexlabs.widgets.dualbattery.ui.PreferenceListFragment;

public class WidgetActivity extends SherlockFragmentActivity implements PreferenceListFragment.OnPreferenceAttachedListener, Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    public int appWidgetId;
    public BatteryInfoViewManager batteryInfoViewManager = new BatteryInfoViewManager();

    TabAdapter mAdapter;
    ViewPager mPager;
    PageIndicator mIndicator;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        appWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        int screenLayout = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB &&
                screenLayout > Configuration.SCREENLAYOUT_SIZE_LARGE) {
            Intent newIntent = new Intent(this, WidgetTabbedActivity.class);
            newIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            startActivity(newIntent);
            finish();
            return;
        }

        setContentView(R.layout.widgetsettings);
        mAdapter = new TabAdapter(getSupportFragmentManager());
        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        mIndicator = (TabPageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
    }

    private Preference filterPref;

    @Override
    public void onPreferenceAttached(PreferenceScreen root, int xmlId){
        if(root == null)
            return; //for whatever reason in very rare cases this is null
        if(xmlId == R.xml.widget_properties_general){
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            //getPreferenceManager().setSharedPreferencesName(Constants.SETTINGS_PREFIX + appWidgetId);
            String value = prefs.getString("display_filter", "1");
            //String[] strings = getResources().getStringArray(R.array.display_filter_array);
            try{
                Preference filterPref = root.findPreference("display_filter");
                //filterPref.setSummary(strings[Integer.parseInt(value)]);
                filterPref.setOnPreferenceChangeListener(this);
                this.filterPref = filterPref;
            }catch(NumberFormatException e){}
            root.findPreference("a").setOnPreferenceClickListener(this);
        }//else if(xmlId == R.xml.s_widget_settings){
        //    root.findPreference("example").setOnPreferenceClickListener(this);
        //}
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        //String[] strings = getResources().getStringArray(R.array.display_filter_array);
        try{
            //filterPref.setSummary(strings[Integer.parseInt((String)newValue)]);
        }catch(NumberFormatException e){}
        BatteryWidgetUpdater.updateWidget(this, appWidgetId);
        return true;
    }

    @Override
    public boolean onPreferenceClick(Preference pref){
        if(pref.getKey().equals("whatever")){
            mPager.setCurrentItem(3, true);
        }
        return true;
    }

    class TabAdapter extends FragmentPagerAdapter {
        public TabAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return new SherlockBatteryInfoFragment();
                case 1:
                    return new PropertiesFragment();
                case 2:
                    return new SherlockFeedbackFragment();
                case 3:
                    return new SherlockDonateFragment();
                case 4:
                    return new SherlockAboutFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch(position) {
                case 0:
                    return getString(R.string.propTitle_General);
                case 1:
                    return getString(R.string.propHeader_Main);
                case 2:
                    return getString(R.string.propTitle_Feedback);
                case 3:
                    return getString(R.string.propTitle_Donate);
                case 4:
                    return getString(R.string.propTitle_About);
                default:
                    return null;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        batteryInfoViewManager.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.widget, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return batteryInfoViewManager.onMenuItemSelected(item) ||
               super.onOptionsItemSelected(item);
    }
}
