package org.flexlabs.widgets.dualbattery.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import org.flexlabs.widgets.dualbattery.service.BatteryMonitorService;
import org.flexlabs.widgets.dualbattery.Constants;
import org.flexlabs.widgets.dualbattery.R;

/**
 * Created by IntelliJ IDEA.
 * User: Flexer
 * Date: 17/06/11
 * Time: 18:44
 */
public class WidgetPropertiesFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private int appWidgetId;
    Preference mTextSize, mTextColor, mTextPosition, mMarginLocation, mTempUnits, mBatteryToDisplay;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        WidgetPropertiesActivity activity = (WidgetPropertiesActivity)getActivity();
        appWidgetId = activity.appWidgetId;

        getPreferenceManager().setSharedPreferencesName(Constants.SETTINGS_PREFIX + appWidgetId);
        addPreferencesFromResource(R.xml.widget_properties_general);
        if (BatteryMonitorService.isDockSupported(getActivity())) {
            addPreferencesFromResource(R.xml.widget_properties_dock);
        }

        mTextSize = getPreferenceScreen().findPreference(Constants.SETTING_TEXT_SIZE);
        mTextColor = getPreferenceScreen().findPreference(Constants.SETTING_TEXT_COLOR);
        mTextPosition = getPreferenceScreen().findPreference(Constants.SETTING_TEXT_POS);
        mMarginLocation = getPreferenceScreen().findPreference(Constants.SETTING_MARGIN);
        mTempUnits = getPreferenceScreen().findPreference(Constants.SETTING_TEMP_UNITS);
        mBatteryToDisplay = getPreferenceScreen().findPreference(Constants.SETTING_SHOW_SELECTION);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences pref = getPreferenceScreen().getSharedPreferences();

        setTextSize(pref);
        setTextColor(pref);
        setTextPos(pref);
        setMargin(pref);
        setTempUnits(pref);
        setBatteryToDisplay(pref);

        pref.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(Constants.SETTING_TEXT_SIZE) && mTextSize != null) {
            setTextSize(sharedPreferences);
        } else if (s.equals(Constants.SETTING_TEXT_COLOR) && mTextColor != null) {
            setTextColor(sharedPreferences);
        } else if (s.equals(Constants.SETTING_TEXT_POS) && mTextPosition != null) {
            setTextPos(sharedPreferences);
        } else if (s.equals(Constants.SETTING_MARGIN) && mMarginLocation != null) {
            setMargin(sharedPreferences);
        } else if (s.equals(Constants.SETTING_TEMP_UNITS) && mTempUnits != null) {
            setTempUnits(sharedPreferences);
        } else if (s.equals(Constants.SETTING_SHOW_SELECTION) && mBatteryToDisplay != null) {
            setBatteryToDisplay(sharedPreferences);
        }
    }

    private void setTextSize(SharedPreferences pref) {
        if (mTextSize == null)
            return;

        String s = pref.getString(Constants.SETTING_TEXT_SIZE, Constants.SETTING_TEXT_SIZE_DEFAULT);
        mTextSize.setSummary(getString(R.string.prop_currentValue) + " " + s);
    }

    private void setTextColor(SharedPreferences pref) {
        if (mTextColor == null)
            return;

        int val = Integer.valueOf(pref.getString(Constants.SETTING_TEXT_COLOR, Constants.SETTING_TEXT_COLOR_DEFAULT));
        String value;

        if (val == Constants.TEXT_COLOR_WHITE) {
            value = getString(R.string.sett_textColor_White);
        } else if (val == Constants.TEXT_COLOR_BLACK) {
            value = getString(R.string.sett_textColor_Black);
        } else {
            mTextColor.setSummary("");
            return;
        }

        mTextColor.setSummary(getString(R.string.prop_currentValue) + " " + value);
    }

    private void setTextPos(SharedPreferences pref) {
        if (mTextPosition == null)
            return;

        int val = Integer.valueOf(pref.getString(Constants.SETTING_TEXT_POS, Constants.SETTING_TEXT_POS_DEFAULT));
        String value;

        if (val == Constants.TEXT_POS_INVISIBLE) {
            value = getString(R.string.sett_textPos_Invisible);
        } else if (val == Constants.TEXT_POS_ABOVE) {
            value = getString(R.string.sett_textPos_Above);
        } else if (val == Constants.TEXT_POS_TOP) {
            value = getString(R.string.sett_textPos_Top);
        } else if (val == Constants.TEXT_POS_MIDDLE) {
            value = getString(R.string.sett_textPos_Middle);
        } else if (val == Constants.TEXT_POS_BOTTOM) {
            value = getString(R.string.sett_textPos_Bottom);
        } else if (val == Constants.TEXT_POS_BELOW) {
            value = getString(R.string.sett_textPos_Below);
        } else {
            mTextPosition.setSummary(R.string.prop_textPosition_summary);
            return;
        }

        mTextPosition.setSummary(
                getString(R.string.prop_textPosition_summary) + "\n" +
                getString(R.string.prop_currentValue) + " " + value);
    }

    private void setMargin(SharedPreferences pref) {
        if (mMarginLocation == null)
            return;

        int val = Integer.valueOf(pref.getString(Constants.SETTING_MARGIN, Constants.SETTING_MARGIN_DEFAULT));
        String value;

        if (val == Constants.MARGIN_NONE) {
            value = getString(R.string.sett_margin_None);
        } else if (val == Constants.MARGIN_TOP) {
            value = getString(R.string.sett_margin_Top);
        } else if (val == Constants.MARGIN_BOTTOM) {
            value = getString(R.string.sett_margin_Bottom);
        } else if (val == Constants.MARGIN_BOTH) {
            value = getString(R.string.sett_margin_Both);
        } else {
            mMarginLocation.setSummary(R.string.prop_margin_summary);
            return;
        }

        mMarginLocation.setSummary(
                getString(R.string.prop_margin_summary) + "\n" +
                getString(R.string.prop_currentValue) + " " + value);
    }

    private void setTempUnits(SharedPreferences pref) {
        if (mTempUnits == null)
            return;

        int val = pref.getInt(Constants.SETTING_TEMP_UNITS, Constants.SETTING_TEMP_UNITS_DEFAULT);
        String value;

        if (val == Constants.TEMP_UNIT_CELSIUS) {
            value = getString(R.string.sett_tempUnits_C);
        } else if (val == Constants.TEMP_UNIT_FAHRENHEIT) {
            value = getString(R.string.sett_tempUnits_F);
        } else {
            return;
        }

        ((WidgetPropertiesActivity)getActivity()).tempUnitsC = val != Constants.TEMP_UNIT_FAHRENHEIT;
        mTempUnits.setSummary(getString(R.string.prop_currentValue) + " " + value);
    }

    private void setBatteryToDisplay(SharedPreferences pref) {
        if (mBatteryToDisplay == null)
            return;

        int val = Integer.valueOf(pref.getString(Constants.SETTING_SHOW_SELECTION, Constants.SETTING_SHOW_SELECTION_DEFAULT));
        String value;

        if (val == Constants.BATTERY_SELECTION_BOTH) {
            value = getString(R.string.sett_battSel_Both);
        } else if (val == Constants.BATTERY_SELECTION_MAIN) {
                value = getString(R.string.sett_battSel_Main);
        } else if (val == Constants.BATTERY_SELECTION_DOCK) {
            value = getString(R.string.sett_battSel_Dock);
        } else {
            return;
        }

        mBatteryToDisplay.setSummary(getString(R.string.prop_currentValue) + " " + value);
    }
}
