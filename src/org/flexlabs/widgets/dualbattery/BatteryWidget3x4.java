package org.flexlabs.widgets.dualbattery;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by IntelliJ IDEA.
 * User: Flexer
 * Date: 08/10/11
 * Time: 12:12
 *
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
public class BatteryWidget3x4 extends BatteryWidget {
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        Log.d(LOG, "onReceive: " + intent.getAction());
        if (Constants.ACTION_BATTERY_UPDATE.equals(intent.getAction()) ||
            Constants.ACTION_SETTINGS_UPDATE.equals(intent.getAction())) {
            updateWidget(context);
        }
    }

    private void updateWidget(Context context) {
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = widgetManager.getAppWidgetIds(new ComponentName(context, BatteryWidget3x4.class));
        onUpdate(context, widgetManager, appWidgetIds);
    }
}
