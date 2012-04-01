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

package org.flexlabs.widgets.dualbattery;

import android.content.Context;
import com.mixpanel.android.mpmetrics.MPMetrics;
import org.json.JSONException;
import org.json.JSONObject;

public class MixPanelComponent {
    private static final String API_KEY = "bce7bb0bd38a73dc88daa8b0588138cb";
    private static boolean ENABLED = true;

    public static final String BATTERY_INFO = "Activity.BatteryInfo";
    public static final String BATTERY_INFO_FEEDBACK = "Activity.BatteryInfo.Feedback";
    public static final String DONATE_PAYPAL = "Donate.PayPal";
    public static final String DONATE_GPLAY_PACKAGE = "Donate.GooglePlay.PackageSelected";
    public static final String DONATE_GPLAY_CONFIRMED = "Donate.GooglePlay.Confirmed";
    
    private MPMetrics mpMetrics;

    public MixPanelComponent(Context context) {
        if (ENABLED)
            mpMetrics = new MPMetrics(context, API_KEY);
    }
    
    public void track(String eventName, JSONObject properties) {
        if (!ENABLED || mpMetrics == null) return;
        if (properties == null)
            properties = new JSONObject();
        try {
            properties.put("appVersion", Constants.VERSION);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mpMetrics.track(eventName, properties);
    }
    
    public void flush() {
        if (!ENABLED || mpMetrics == null) return;
        mpMetrics.flush();
    }
}
