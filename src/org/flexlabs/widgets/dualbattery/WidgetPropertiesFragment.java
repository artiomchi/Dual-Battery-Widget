package org.flexlabs.widgets.dualbattery;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by IntelliJ IDEA.
 * User: Flexer
 * Date: 17/06/11
 * Time: 18:44
 * To change this template use File | Settings | File Templates.
 */
public class WidgetPropertiesFragment extends PreferenceFragment {
    private int appWidgetId;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        WidgetPropertiesHCActivity activity = (WidgetPropertiesHCActivity)getActivity();
        appWidgetId = activity.appWidgetId;

        getPreferenceManager().setSharedPreferencesName(Constants.SETTINGS_PREFIX + appWidgetId);
        addPreferencesFromResource(R.xml.widget_properties);
        addPreferencesFromResource(R.xml.widget_properties_dock);
    }
}
