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

package org.flexlabs.widgets.dualbattery;

import android.app.Activity;
import android.widget.Toast;
import net.robotmedia.billing.BillingRequest;
import net.robotmedia.billing.helper.AbstractBillingObserver;
import net.robotmedia.billing.model.Transaction;
import org.json.JSONException;
import org.json.JSONObject;

public class BillingObserver extends AbstractBillingObserver {
    public BillingObserver(Activity activity) {
        super(activity);
    }

    @Override
    public void onBillingChecked(boolean supported) { }

    @Override
    public void onRequestPurchaseResponse(String itemId, BillingRequest.ResponseCode response) { }

    @Override
    public void onPurchaseStateChanged(String itemId, Transaction.PurchaseState state) {
        if (state == Transaction.PurchaseState.PURCHASED) {
            JSONObject properties = new JSONObject();
            try {
                properties.put("item", itemId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Toast.makeText(activity, "Thanks a lot for supporting me!", Toast.LENGTH_LONG).show();
        }
    }
}
