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
import com.googlecode.androidannotations.annotations.EActivity;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TabPageIndicator;
import org.flexlabs.widgets.dualbattery.R;

@EActivity
public class SettingsActivity extends SherlockFragmentActivity implements AdapterView.OnItemClickListener {
    private ListView mList;
    private int mCurrentTab = -1;
    private Fragment[] fragments;
    private String[] titles;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragments = new Fragment[4];
        fragments[0] = new SettingsFragment_();
        fragments[1] = new FeedbackFragment_();
        fragments[2] = new DonateFragment_();
        fragments[3] = new AboutFragment_();
        titles = new String[4];
        titles[0] = getString(R.string.settings);
        titles[1] = getString(R.string.propHeader_Feedback);
        titles[2] = getString(R.string.propHeader_Donate);
        titles[3] = getString(R.string.propHeader_About);

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
}