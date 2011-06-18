package org.flexlabs.widgets.dualbattery.settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.widget.TextView;
import org.flexlabs.widgets.dualbattery.BatteryApplication;
import org.flexlabs.widgets.dualbattery.Constants;
import org.flexlabs.widgets.dualbattery.R;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Artiom Chilaru
 * Date: 14/06/11
 * Time: 19:22
 */
public class WidgetPropertiesActivity extends PreferenceActivity {
    public static final String TAG = "FlexLabs.WidgetProperties";
    public static final int DIALOG_ABOUT = 0;

    public int appWidgetId;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appWidgetId = getIntent().getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            getPreferenceManager().setSharedPreferencesName(Constants.SETTINGS_PREFIX + appWidgetId);
            addPreferencesFromResource(R.xml.widget_properties_general);
            addPreferencesFromResource(R.xml.widget_properties_dock);
            addPreferencesFromResource(R.xml.widget_properties_other);
        }
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        super.onBuildHeaders(target);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            loadHeadersFromResource(R.xml.widget_properties_headers, target);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_ABOUT:
                final SpannableString s = new SpannableString(getText(R.string.about_full));
                Linkify.addLinks(s, Linkify.ALL);

                Dialog result = new AlertDialog.Builder(this)
                        .setTitle(R.string.propTitle_About)
                        .setMessage(s)
                        .setPositiveButton("OK", null)
                        .create();
                result.show();
                ((TextView)result.findViewById(android.R.id.message))
                        .setMovementMethod(LinkMovementMethod.getInstance());
            default: return null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        sendBroadcast(new Intent(BatteryApplication.ACTION_WIDGET_UPDATE));
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (onPreferenceClicked(preference.getKey()))
            return true;
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    public boolean onPreferenceClicked(String key) {
        if ("about".equals(key)) {
            this.showDialog(DIALOG_ABOUT);
            return true;
        }
        if ("crashReport".equals(key)) {
            return true;
        }
        if ("feedback".equals(key)) {
            Intent i = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

            Intent intent = new Intent(Intent.ACTION_SEND, Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_EMAIL, new String[] { Constants.FeedbackDestination });
            intent.putExtra(Intent.EXTRA_SUBJECT, "Dual Battery Widget Feedback");
            intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(getDeviceDetails()));
            intent.setType("message/rfc822");
            startActivity(Intent.createChooser(intent, "Email"));
        }

        return false;
    }

    private String getDeviceDetails() {
        StringBuilder sb = new StringBuilder("<br />\n<h4>Device details:</h4>");
        sb.append("<br />\n<b>Brand:</b> " + Build.MANUFACTURER);
        sb.append("<br />\n<b>Model:</b> " + Build.MODEL);
        sb.append("<br />\n<b>Device:</b> " + Build.DEVICE);
        sb.append("<br />\n<b>Android version:</b> " + Build.VERSION.RELEASE);
        sb.append("<br />\n<b>Version details:</b> " + Build.VERSION.INCREMENTAL);
        sb.append("<br />\n<b>Kernel:</b> " + getFormattedKernelVersion().replace("\n", "<br />\n"));
        return sb.toString();
    }

    private String getFormattedKernelVersion() {
        String procVersionStr;

        try {
            BufferedReader reader = new BufferedReader(new FileReader("/proc/version"), 256);
            try {
                procVersionStr = reader.readLine();
            } finally {
                reader.close();
            }

            final String PROC_VERSION_REGEX =
                "\\w+\\s+" + /* ignore: Linux */
                "\\w+\\s+" + /* ignore: version */
                "([^\\s]+)\\s+" + /* group 1: 2.6.22-omap1 */
                "\\(([^\\s@]+(?:@[^\\s.]+)?)[^)]*\\)\\s+" + /* group 2: (xxxxxx@xxxxx.constant) */
                "\\(.*?(?:\\(.*?\\)).*?\\)\\s+" + /* ignore: (gcc ..) */
                "([^\\s]+)\\s+" + /* group 3: #26 */
                "(?:PREEMPT\\s+)?" + /* ignore: PREEMPT (optional) */
                "(.+)"; /* group 4: date */

            Pattern p = Pattern.compile(PROC_VERSION_REGEX);
            Matcher m = p.matcher(procVersionStr);

            if (!m.matches()) {
                Log.e(TAG, "Regex did not match on /proc/version: " + procVersionStr);
                return "Unavailable";
            } else if (m.groupCount() < 4) {
                Log.e(TAG, "Regex match on /proc/version only returned " + m.groupCount()
                        + " groups");
                return "Unavailable";
            } else {
                return (new StringBuilder(m.group(1)).append("\n").append(
                        m.group(2)).append(" ").append(m.group(3)).append("\n")
                        .append(m.group(4))).toString();
            }
        } catch (IOException e) {
            Log.e(TAG, "IO Exception when getting kernel version for Device Info screen", e);

            return "Unavailable";
        }
    }
}