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
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.*;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TabPageIndicator;
import org.flexlabs.widgets.dualbattery.BatteryWidgetUpdater;
import org.flexlabs.widgets.dualbattery.R;
import org.flexlabs.widgets.dualbattery.app.AboutFragment;
import org.flexlabs.widgets.dualbattery.app.DonateFragment;
import org.flexlabs.widgets.dualbattery.app.FeedbackFragment;

public class WidgetActivity extends SherlockFragmentActivity implements AdapterView.OnItemClickListener {
    public int appWidgetId;

    private ListView mList;
    private int mCurrentTab = -1;
    private Fragment[] fragments;
    private String[] titles;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        appWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        fragments = new Fragment[5];
        fragments[0] = new BatteryInfoFragment();
        fragments[1] = new PropertiesFragment();
        fragments[2] = new FeedbackFragment();
        fragments[3] = new DonateFragment();
        fragments[4] = new AboutFragment();
        titles = new String[5];
        titles[0] = getString(R.string.propHeader_BatteryInfo);
        titles[1] = getString(R.string.propHeader_Properties);
        titles[2] = getString(R.string.propHeader_Feedback);
        titles[3] = getString(R.string.propHeader_Donate);
        titles[4] = getString(R.string.propHeader_About);

        int screenLayout = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB &&
                screenLayout > Configuration.SCREENLAYOUT_SIZE_LARGE) {
            setContentView(R.layout.preference_list_large);

            ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, titles);

            mList = (ListView)findViewById(android.R.id.list);
            mList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            mList.setOnItemClickListener(this);
            mList.setAdapter(adapter);
            mList.setItemChecked(0, true);
            mList.performClick();
            getSupportFragmentManager().beginTransaction().replace(R.id.prefs, fragments[0]).commit();
        } else {
            setContentView(R.layout.widgetsettings);
            PagerTabAdapter mPagerAdapter = new PagerTabAdapter(getSupportFragmentManager());
            ViewPager mPager = (ViewPager) findViewById(R.id.pager);
            mPager.setAdapter(mPagerAdapter);

            PageIndicator mIndicator = (TabPageIndicator) findViewById(R.id.indicator);
            mIndicator.setViewPager(mPager);
        }
    }

    private class PagerTabAdapter extends FragmentPagerAdapter {
        public PagerTabAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return fragments[i];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (mCurrentTab == position)
            return;

        mCurrentTab = position;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.prefs, fragments[position]);
        transaction.commit();
        mList.setItemChecked(position, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BatteryWidgetUpdater.updateWidget(this, appWidgetId);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return ((BatteryInfoFragment)fragments[0]).onMenuItemSelected(item) ||
               super.onOptionsItemSelected(item);
    }
}
