/*
 * Copyright 2013 Artiom Chilaru (http://flexlabs.org)
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
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.SherlockFragment;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import net.robotmedia.billing.BillingController;
import net.robotmedia.billing.helper.AbstractBillingObserver;
import org.flexlabs.widgets.dualbattery.BillingObserver;
import org.flexlabs.widgets.dualbattery.Constants;
import org.flexlabs.widgets.dualbattery.R;
import org.json.JSONException;
import org.json.JSONObject;

@EFragment(R.layout.donate_summary)
public class DonateFragment extends SherlockFragment {
    private AbstractBillingObserver mBillingObserver;

    @ViewById(R.id.donate_play) View donate_play;
    @ViewById(R.id.donate_play_options) View donate_play_options;

    @AfterViews
    public void init() {
        if (!Constants.HAS_GPLAY_BILLING) {
            donate_play.setVisibility(View.GONE);
            donate_play_options.setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mBillingObserver == null) {
            mBillingObserver = new BillingObserver(getActivity());
            BillingController.registerObserver(mBillingObserver);
        }
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBillingObserver != null) {
            BillingController.unregisterObserver(mBillingObserver);
            mBillingObserver = null;
        }
    }

    @Click(R.id.donate_play_1)
    public void donatePlay1Click(View button) {
        donatePlay("donation.amount.0.99");
    }

    @Click(R.id.donate_play_3)
    public void donatePlay2Click(View button) {
        donatePlay("donation.amount.3.00");
    }

    @Click(R.id.donate_play_7)
    public void donatePlay3Click(View button) {
        donatePlay("donation.amount.7.77");
    }

    private void donatePlay(String playItem) {
        JSONObject properties = new JSONObject();
        try {
            properties.put("item", playItem);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        BillingController.requestPurchase(getActivity(), playItem, true, null);
    }

    @Click(R.id.donate_paypal)
    public void donatePayPalClick() {
        getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.URI_PAYPAL)));
    }
}
