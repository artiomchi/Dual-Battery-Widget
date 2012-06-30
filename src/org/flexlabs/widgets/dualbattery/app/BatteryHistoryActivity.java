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

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TabPageIndicator;
import org.flexlabs.widgets.dualbattery.BatteryLevel;
import org.flexlabs.widgets.dualbattery.R;

public class BatteryHistoryActivity extends SherlockFragmentActivity {
    private ListView mList;
    private int mCurrentTab = -1;
    private Fragment[] fragments;
    private String[] titles;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragments = new Fragment[4];
        fragments[0] = new BatteryHistoryFragment();
        fragments[1] = new FeedbackFragment();
        fragments[2] = new DonateFragment();
        fragments[3] = new AboutFragment();
        titles = new String[4];
        titles[0] = getString(R.string.propHeader_BatteryInfo);
        titles[1] = getString(R.string.propHeader_Feedback);
        titles[2] = getString(R.string.propHeader_Donate);
        titles[3] = getString(R.string.propHeader_About);

        int screenLayout = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB &&
                screenLayout > Configuration.SCREENLAYOUT_SIZE_LARGE) {
            setContentView(R.layout.preference_list_large);

            SideTabAdapter mSideAdapter = new SideTabAdapter(this, titles);

            mList = (ListView)findViewById(android.R.id.list);
            mList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            mList.setOnItemClickListener(sideTabListener);
            mList.setAdapter(mSideAdapter);
            mList.setItemChecked(0, true);
            mList.performClick();
            getSupportFragmentManager().beginTransaction().add(R.id.prefs, fragments[0]).commit();
        } else {
            setContentView(R.layout.widgetsettings);
            PagerTabAdapter mPagerAdapter = new PagerTabAdapter(getSupportFragmentManager());
            ViewPager mPager = (ViewPager) findViewById(R.id.pager);
            mPager.setAdapter(mPagerAdapter);

            PageIndicator mIndicator = (TabPageIndicator) findViewById(R.id.indicator);
            mIndicator.setViewPager(mPager);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (BatteryLevel.getCurrent().is_dockFriendly()) {
            getSupportMenuInflater().inflate(R.menu.main, menu);
        }
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

    private static class SideTabAdapter extends ArrayAdapter<String> {
        private LayoutInflater mInflater;

        public SideTabAdapter(Context context, String[] objects) {
            super(context, 0, objects);
            mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            TextView textView;

            if (convertView == null) {
                view = mInflater.inflate(android.R.layout.simple_list_item_activated_1, parent, false);
                textView = (TextView)view.findViewById(android.R.id.text1);
                view.setTag(textView);
            } else {
                view = convertView;
                textView = (TextView)view.getTag();
            }

            String title = getItem(position);
            textView.setText(title);

            return view;
        }
    }

    private final AdapterView.OnItemClickListener sideTabListener = new AdapterView.OnItemClickListener() {
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
    };
}