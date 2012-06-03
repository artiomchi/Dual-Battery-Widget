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

package org.flexlabs.widgets.dualbattery.widgetsettings;

import android.app.FragmentBreadCrumbs;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListActivity;
import org.flexlabs.widgets.dualbattery.R;

import java.util.ArrayList;
import java.util.List;

public class WidgetHoneyActivity extends SherlockFragmentActivity {
    private ListAdapter mAdapter;
    private ListView mList;
    private TabType mCurrentTab;

    private static class TabAdapter extends ArrayAdapter<Tab> {
        private LayoutInflater mInflater;

        public TabAdapter(Context context, List<Tab> objects) {
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

            Tab tab = getItem(position);
            textView.setText(tab.getTitle(getContext().getResources()));

            return view;
        }
    }

    private enum TabType { Summary, Settings, Feedback, Donate, About }
    private static final class Tab {
        public int titleRes;
        public TabType type;
        public Fragment fragment;

        public Tab(TabType type, int title) {
            this.titleRes = title;
            this.type = type;
            switch (type) {
                case Summary:
                    fragment = new SherlockBatteryInfoFragment(); break;
                case Settings:
                    fragment = new PropertiesFragment(); break;
                case Feedback:
                    fragment = new SherlockFeedbackFragment(); break;
                case Donate:
                    fragment = new SherlockDonateFragment(); break;
                case About:
                default:
                    fragment = new SherlockAboutFragment(); break;
            }
        }

        public CharSequence getTitle(Resources res) {
            return res.getText(titleRes);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.preference_list_content);


        ArrayList<Tab> tabs = new ArrayList<Tab>();
        tabs.add(new Tab(TabType.Summary, R.string.propHeader_BatteryInfo));
        tabs.add(new Tab(TabType.Settings, R.string.propHeader_Main));
        tabs.add(new Tab(TabType.Feedback, R.string.propTitle_Feedback));
        tabs.add(new Tab(TabType.Donate, R.string.propTitle_Donate));
        tabs.add(new Tab(TabType.About, R.string.propTitle_About));
        mAdapter = new TabAdapter(this, tabs);

        mList = (ListView)findViewById(android.R.id.list);
        mList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mAdapter != null) {
                    Object item = mAdapter.getItem(i);
                    if (item instanceof Tab) onHeaderClick((Tab) item, i);
                }
            }
        });
        mList.setAdapter(mAdapter);
    }

    public void onHeaderClick(Tab tab, int position) {
        if (mCurrentTab == tab.type)
            return;

        mCurrentTab = tab.type;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.prefs, tab.fragment);
        transaction.commit();
        mList.setItemChecked(position, true);
    }
}
