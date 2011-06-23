package org.flexlabs.widgets.dualbattery.settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.widget.TextView;
import org.flexlabs.widgets.dualbattery.BatteryMonitorService;
import org.flexlabs.widgets.dualbattery.Constants;
import org.flexlabs.widgets.dualbattery.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
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
    public static final String KEY_FEEDBACK = "feedback";
    public static final String KEY_REPORT = "crashReport";
    public static final String KEY_ABOUT = "about";
    private static final String PREF_FILE = "global";
    private static final String PREF_KERNEL_NO_NOTIFY = "IKnowAboutMyKernel";
    public static final int DIALOG_ABOUT = 0;
    public static final int DIALOG_KERNEL_PROB = 1;

    public int appWidgetId;
    public boolean widgetIsOld;

    private void ensureIntentSettings() {
        Bundle extras = getIntent().getExtras();
        widgetIsOld = extras.getBoolean(Constants.EXTRA_WIDGET_OLD, false);
        appWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ensureIntentSettings();
        tryCheckKernelCompatibility();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            getPreferenceManager().setSharedPreferencesName(Constants.SETTINGS_PREFIX + appWidgetId);
            addPreferencesFromResource(R.xml.widget_properties_general);
            if (BatteryMonitorService.isDockSupported(this)) {
                addPreferencesFromResource(R.xml.widget_properties_dock);
            }
            addPreferencesFromResource(R.xml.widget_properties_other);

            File crashReport = new File(getFilesDir(), Constants.STACKTRACE_FILENAME);
            Preference pref = findPreference(KEY_REPORT);
            if (crashReport == null || !crashReport.exists()) {
                pref.setEnabled(false);
            } else {
                pref.setSummary(
                        getString(R.string.propTitle_SendCrashReport_summaryPrefix) + " " +
                        new Date(crashReport.lastModified()).toString());
            }
        }
    }

    private void tryCheckKernelCompatibility() {
        if (Constants.SUPPORTED_DOCK_DEVICE.equals(Build.DEVICE) && !BatteryMonitorService.isDockSupported(this)) {
            SharedPreferences pref = getSharedPreferences(PREF_FILE, MODE_PRIVATE);
            if (!pref.getBoolean(PREF_KERNEL_NO_NOTIFY, false)) {
                showDialog(DIALOG_KERNEL_PROB);
            }
        }
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        super.onBuildHeaders(target);
        ensureIntentSettings();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (widgetIsOld) {
                loadHeadersFromResource(R.xml.widget_properties_header_batteryinfo, target);
            }
            loadHeadersFromResource(R.xml.widget_properties_headers, target);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_ABOUT :
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
                return result;

            case DIALOG_KERNEL_PROB :
                return new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.kernelAlert_title)
                        .setMessage(R.string.kernelAlert_message)
                        .setPositiveButton("OK", null)
                        .setNegativeButton(R.string.kernelAlert_forgetIt, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                getSharedPreferences(PREF_FILE, MODE_PRIVATE)
                                        .edit()
                                        .putBoolean(PREF_KERNEL_NO_NOTIFY, true)
                                        .commit();
                            }
                        })
                        .show();

            default: return null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        sendBroadcast(new Intent(Constants.ACTION_SETTINGS_UPDATE));
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (onPreferenceClicked(preference.getKey()))
            return true;
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    public boolean onPreferenceClicked(String key) {
        if (KEY_ABOUT.equals(key)) {
            this.showDialog(DIALOG_ABOUT);
            return true;
        }
        if (KEY_REPORT.equals(key)) {
            File stacktrace = new File(getFilesDir(), Constants.STACKTRACE_FILENAME);
            if (stacktrace != null && stacktrace.exists()) {
                stacktrace.setReadable(true, false);
                Intent intent = new Intent(Intent.ACTION_SEND, Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] { Constants.FeedbackDestination });
                intent.putExtra(Intent.EXTRA_SUBJECT, "Dual Battery Widget Feedback");
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.report_detailrequest) + "\n");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///mnt/sdcard/../.." + stacktrace.getAbsolutePath()));
                intent.setType("message/rfc822");
                startActivity(Intent.createChooser(intent, "Email"));
            }
            return true;
        }
        if (KEY_FEEDBACK.equals(key)) {
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

        Intent intent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        Bundle extras = intent.getExtras();
        String allKeys = TextUtils.join(", ", extras.keySet());
        sb.append("<br />\n<b>Battery intent keys:</b> " + allKeys);
        sb.append("<br />\n<b>Is Dock supported:</b> " + BatteryMonitorService.isDockSupported(this));
        sb.append("<br />\n<b>Battery dock status</b> " + extras.get("dock_status"));

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