package org.flexlabs.widgets.dualbattery;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.*;
import org.flexlabs.widgets.dualbattery.service.MonitorService;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: ArtiomChi
 * Date: 13/06/11
 * Time: 20:13
 */
public class BatteryWidget extends AppWidgetProvider {
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        context.startService(new Intent(context, MonitorService.class));
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        for (int i : appWidgetIds) {
            String file = Constants.SETTINGS_PREFIX + i;
            context.getSharedPreferences(file, Context.MODE_PRIVATE).edit().clear().commit();
            new File(context.getFilesDir() + "/../shared_prefs/" + file + ".xml").delete();
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // ensuring the service is still running, even if it was killed, and updating the widget
        Intent intent = new Intent(context, MonitorService.class);
        intent.putExtra(Constants.EXTRA_WIDGET_IDS, appWidgetIds);
        context.startService(intent);
    }
}
