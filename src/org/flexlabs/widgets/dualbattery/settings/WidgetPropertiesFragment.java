package org.flexlabs.widgets.dualbattery.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import org.flexlabs.widgets.dualbattery.Constants;
import org.flexlabs.widgets.dualbattery.R;

import java.io.NotSerializableException;

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
        WidgetPropertiesActivity activity = (WidgetPropertiesActivity)getActivity();
        appWidgetId = activity.appWidgetId;

        getPreferenceManager().setSharedPreferencesName(Constants.SETTINGS_PREFIX + appWidgetId);
        addPreferencesFromResource(R.xml.widget_properties_general);
        addPreferencesFromResource(R.xml.widget_properties_dock);
    }
}
