package org.flexlabs.widgets.dualbattery;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import org.flexlabs.widgets.dualbattery.settings.WidgetPropertiesActivity;

/**
 * Created by IntelliJ IDEA.
 * User: Artiom Chilaru
 * Date: 17/06/11
 * Time: 00:23
 */
public class DummyConfigurationActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int appWidgetId = getIntent().getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        Intent data = new Intent();
        data.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_OK, data);

        Intent intent = new Intent(this, WidgetPropertiesActivity.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        startActivity(intent);
        finish();
    }
}