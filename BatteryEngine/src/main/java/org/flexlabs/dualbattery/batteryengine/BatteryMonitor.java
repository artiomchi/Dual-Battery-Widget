package org.flexlabs.dualbattery.batteryengine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import org.flexlabs.dualbattery.batteryengine.parsers.BasicDockParser;
import org.flexlabs.dualbattery.batteryengine.parsers.BasicMainParser;
import org.flexlabs.dualbattery.batteryengine.parsers.BasicPadParser;

import java.util.ArrayList;
import java.util.List;

public class BatteryMonitor {
    private Context mContext;
    private boolean isMonitoring = false, useRoot = false;

    public BatteryMonitor(Context context) {
        mContext = context;
    }

    public interface OnBatteryStatusUpdatedListener {
        void batteryLevelsUpdated(List<BatteryLevel> batteryLevels);
    }

    private OnBatteryStatusUpdatedListener listener;
    public void setBatteryUpdatedListener(OnBatteryStatusUpdatedListener listener) {
        this.listener = listener;
    }

    public void startMonitoring(boolean useRoot) {
        if (isMonitoring) {
            throw new IllegalStateException("Already monitoring!");
        }
        this.useRoot = useRoot;

        mContext.registerReceiver(receiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        //mContext.registerReceiver(receiver, new IntentFilter(Intent.ACTION_DOCK_EVENT));
        isMonitoring = true;
    }

    public void stopMonitoring() {
        isMonitoring = false;
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BatteryLevel level = new BasicMainParser().parseBatteryLevel(intent);
            BatteryLevel dockLevel = new BasicDockParser().parseBatteryLevel(intent);
            BatteryLevel padLevel = new BasicPadParser().parseBatteryLevel(intent);

            ArrayList<BatteryLevel> results = new ArrayList<BatteryLevel>();
            results.add(level);
            if (padLevel != null)
                results.add(padLevel);
            if (dockLevel != null)
                results.add(dockLevel);

            listener.batteryLevelsUpdated(results);
        }
    };
}
