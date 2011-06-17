package org.flexlabs.widgets.dualbattery;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Flexer
 * Date: 17/06/11
 * Time: 18:35
 * To change this template use File | Settings | File Templates.
 */
public class WidgetPropertiesHCActivity extends PreferenceActivity {
    public int appWidgetId;

    @Override
    public void onBuildHeaders(List<Header> target) {
        super.onBuildHeaders(target);
        appWidgetId = getIntent().getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        loadHeadersFromResource(R.xml.widget_properties_headers, target);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sendBroadcast(new Intent(BatteryApplication.ACTION_WIDGET_UPDATE));
        finish();
    }
}