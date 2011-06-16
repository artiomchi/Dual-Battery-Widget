package org.flexlabs.widgets.dualbattery;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.view.View;
import android.widget.ListView;

/**
 * Created by IntelliJ IDEA.
 * User: Artiom Chilaru
 * Date: 14/06/11
 * Time: 19:22
 */
public class WidgetPropertiesActivity extends PreferenceActivity {
    private int appWidgetId;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appWidgetId = getIntent().getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        getPreferenceManager().setSharedPreferencesName(Constants.SETTINGS_PREFIX + appWidgetId);
        addPreferencesFromResource(R.xml.widget_properties);

        setWidgetResult(RESULT_CANCELED);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference.getKey().equals("save")) {
            setWidgetResult(RESULT_OK);
            finish();
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    private void setWidgetResult(int result) {
        Intent data = new Intent();
        data.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(result, data);
    }

    @Override
    protected void onStop() {
        super.onStop();
        sendBroadcast(new Intent(BatteryApplication.ACTION_WIDGET_UPDATE));
    }
}