/*
 * Copyright 2013 Artiom Chilaru (http://flexlabs.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.flexlabs.widgets.dualbattery.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import com.actionbarsherlock.app.SherlockFragment;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import org.flexlabs.widgets.dualbattery.BatteryLevel;
import org.flexlabs.widgets.dualbattery.Constants;
import org.flexlabs.widgets.dualbattery.R;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@EFragment(R.layout.feedback_form)
public class FeedbackFragment extends SherlockFragment {
    @ViewById(R.id.feedbackEditor) EditText feedbackEditor;
    private boolean resetFeedback = false;

    @AfterViews
    public void feedbackFocus() {
        feedbackEditor.requestFocus();
    }

    @Click(R.id.sendFeedback)
    public void sendFeedbackClick() {
        CharSequence feedbackText = feedbackEditor.getText();

        if (!isFeedbackValid(feedbackText)) {
            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.app_name)
                    .setMessage(R.string.feedback_hint)
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }
        sendFeedback(getActivity(), feedbackText);
        resetFeedback = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (resetFeedback)
            feedbackEditor.setText(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        resetFeedback = false;
    }

    private static boolean isFeedbackValid(CharSequence source) {
        if (source == null || source.length() < 15)
            return false;

        int spaces = 0;
        for(int i = 0; i < source.length(); i++) {
            if(source.charAt(i) == ' ')
                spaces++;
            if (spaces > 3)
                return true;
        }
        return false;
    }

    public static void sendFeedback(Context context, CharSequence feedback) {
        Intent intent = new Intent(Intent.ACTION_SEND, Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { Constants.FeedbackDestination });
        intent.putExtra(Intent.EXTRA_SUBJECT, "Dual Battery Widget Feedback (v" + Constants.VERSION + ")");
        intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(getDeviceDetails(context, feedback)));
        intent.setType("message/rfc822");
        context.startActivity(Intent.createChooser(intent, "Email"));
    }

    private static String getDeviceDetails(Context context, CharSequence feedback) {
        StringBuilder sb = new StringBuilder(feedback);
        sb.append("<br />\n<h4>Device details:</h4>");
        sb.append("<br />\n<b>App version:</b> v").append(Constants.VERSION);
        sb.append("<br />\n<b>Brand:</b> ").append(Build.MANUFACTURER);
        sb.append("<br />\n<b>Model:</b> ").append(Build.MODEL);
        sb.append("<br />\n<b>Device:</b> ").append(Build.DEVICE);
        sb.append("<br />\n<b>Android version:</b> ").append(Build.VERSION.RELEASE);
        sb.append("<br />\n<b>Version details:</b> ").append(Build.VERSION.INCREMENTAL);

        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        Bundle extras = intent.getExtras();
        String allKeys = TextUtils.join(", ", extras.keySet());
        sb.append("<br />\n<b>Battery intent keys:</b> ").append(allKeys);
        sb.append("<br />\n<b>Is Dock supported:</b> ").append(BatteryLevel.getCurrent().is_dockFriendly());
        int dockStatus = extras.getInt("dock_status", -1);
        String dockStatusStr = "";
        switch (dockStatus) {
            case 0: dockStatusStr = " (Unknown)"; break;
            case 1: dockStatusStr = " (Undocked)"; break;
            case 2: dockStatusStr = " (Charging)"; break;
            case 3: dockStatusStr = " (Docked)"; break;
            case 4: dockStatusStr = " (Discharging)"; break;

        }
        sb.append("<br />\n<b>Battery dock status</b> ").append(extras.get("dock_status")).append(dockStatusStr);
        sb.append("<br />\n<b>Battery dock level</b> ").append(extras.get("dock_level"));

        sb.append("<br />\n<b>Kernel:</b> ").append(getFormattedKernelVersion().replace("\n", "<br />\n"));
        return sb.toString();
    }

    private static String getFormattedKernelVersion() {
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
                Log.e(Constants.LOG, "Regex did not match on /proc/version: " + procVersionStr);
                return "Unavailable";
            } else if (m.groupCount() < 4) {
                Log.e(Constants.LOG, "Regex match on /proc/version only returned " + m.groupCount()
                        + " groups");
                return "Unavailable";
            } else {
                return (new StringBuilder(m.group(1)).append("\n").append(
                        m.group(2)).append(" ").append(m.group(3)).append("\n")
                        .append(m.group(4))).toString();
            }
        } catch (IOException e) {
            Log.e(Constants.LOG, "IO Exception when getting kernel version for Device Info screen", e);

            return "Unavailable";
        }
    }
}
