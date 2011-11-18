package org.flexlabs.widgets.dualbattery.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by IntelliJ IDEA.
 * User: ArtiomChi
 * Date: 13/11/11
 * Time: 13:08
 */
public class BootUpReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, MonitorService.class));
    }
}
