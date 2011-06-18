package org.flexlabs.widgets.dualbattery.settings;

import android.app.AlertDialog;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import org.flexlabs.widgets.dualbattery.R;

import java.lang.annotation.Retention;

/**
 * Created by IntelliJ IDEA.
 * User: Artiom Chilaru
 * Date: 18/06/11
 * Time: 15:30
 */
public class WidgetOtherFragment extends PreferenceFragment {
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addPreferencesFromResource(R.xml.widget_properties_other);
    }



    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (((WidgetPropertiesActivity)getActivity()).onPreferenceClicked(preference.getKey()))
            return true;
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
