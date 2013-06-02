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
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.SherlockFragment;
import net.robotmedia.billing.BillingController;
import net.robotmedia.billing.helper.AbstractBillingObserver;
import org.flexlabs.widgets.dualbattery.BillingObserver;
import org.flexlabs.widgets.dualbattery.Constants;
import org.flexlabs.widgets.dualbattery.R;
import org.json.JSONException;
import org.json.JSONObject;

public class DonateFragment extends SherlockFragment {
    private AbstractBillingObserver mBillingObserver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.donate_summary, null);
        if (!Constants.HAS_GPLAY_BILLING) {
            view.findViewById(R.id.donate_play).setVisibility(View.GONE);
            view.findViewById(R.id.donate_play_options).setVisibility(View.GONE);
        }

        view.findViewById(R.id.donate_play_1).setOnClickListener(playDonateListener);
        view.findViewById(R.id.donate_play_3).setOnClickListener(playDonateListener);
        view.findViewById(R.id.donate_play_7).setOnClickListener(playDonateListener);
        view.findViewById(R.id.donate_paypal).setOnClickListener(payPalDonateListener);

        if (mBillingObserver == null) {
            mBillingObserver = new BillingObserver(getActivity());
            BillingController.registerObserver(mBillingObserver);
        }
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBillingObserver != null) {
            BillingController.unregisterObserver(mBillingObserver);
            mBillingObserver = null;
        }
    }

    private final View.OnClickListener playDonateListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String playItem = "donation.amount.0.99";
            switch (view.getId()) {
                case R.id.donate_play_3:
                    playItem = "donation.amount.3.00";
                    break;
                case R.id.donate_play_7 :
                    playItem = "donation.amount.7.77";
                    break;
            }
            JSONObject properties = new JSONObject();
            try {
                properties.put("item", playItem);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            BillingController.requestPurchase(getActivity(), playItem, true);
        }
    };

    private final View.OnClickListener payPalDonateListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.URI_PAYPAL)));
        }
    };
}
