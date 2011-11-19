package org.flexlabs.widgets.dualbattery.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import org.flexlabs.widgets.dualbattery.BatteryLevel;
import org.flexlabs.widgets.dualbattery.Constants;
import org.flexlabs.widgets.dualbattery.BatteryWidgetUpdater;
import org.flexlabs.widgets.dualbattery.storage.BatteryLevelAdapter;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: ArtiomChi
 * Date: 17/11/11
 * Time: 21:45
 */
public class IntentReceiver extends BroadcastReceiver {
    private NotificationManager mNotificationManager;
    private boolean screenOff = false;

    public IntentReceiver(Context context) {
        mNotificationManager = new NotificationManager(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean newData = false;
        BatteryLevel level = BatteryLevel.getCurrent();

        if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
            if (!screenOff)
                newData = true;
            screenOff = true;

        } else if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
            if (screenOff)
                newData = true;
            screenOff = false;

        } else if (Intent.ACTION_DOCK_EVENT.equals(intent.getAction())) {
            if (level != null && level.is_dockFriendly()) {
                int dockState = intent.getIntExtra(Intent.EXTRA_DOCK_STATE, -1);
                if (dockState == Intent.EXTRA_DOCK_STATE_UNDOCKED && level.is_dockConnected()) {
                    level.undock();
                }
                if (dockState == 10 && !level.is_dockConnected() && BatteryLevel.lastDockLevel != null) {// 10 = Asus Transformer Dock
                    level.dock(BatteryLevel.lastDockLevel);
                }
            }

        } else if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
            BatteryLevel newLevel = BatteryLevel.parse(intent.getExtras());
            if (newLevel == null)
                return;

            if (newLevel.isDifferent(level))
                newData = true;

            if ((level == null || level.get_status() == BatteryManager.BATTERY_STATUS_CHARGING) &&
                newLevel.get_status() != BatteryManager.BATTERY_STATUS_CHARGING)
                BatteryLevel.lastCharged = new Date();

            if (newLevel.is_dockFriendly() && level != null && level.get_dock_status() >= Constants.DOCK_STATE_CHARGING &&
                newLevel.get_dock_status() < Constants.DOCK_STATE_CHARGING)
                BatteryLevel.dockLastConnected = new Date();

            if (newLevel.get_dock_level() != null)
                BatteryLevel.lastDockLevel = newLevel.get_dock_level();

            BatteryLevel.update(newLevel);
            level = newLevel;
        }

        if (!newData && screenOff)
            return;

        // Running more expensive operations away from the UI thread
        Runnable runnable = new Runnable() {
            private Context _context;
            private BatteryLevel _level;
            private boolean _newData;
            public Runnable setData(Context context, BatteryLevel level, boolean newData) {
                _context = context;
                _level = level;
                _newData = newData;
                return this;
            }

            @Override
            public void run() {
                if (_newData) {
                    BatteryLevelAdapter.Entry entry = new BatteryLevelAdapter.Entry(
                        _level.get_status(),
                        _level.get_level(),
                        _level.get_dock_status(),
                        _level.get_dock_level(),
                        screenOff);

                    BatteryLevelAdapter adapter = new BatteryLevelAdapter(_context);
                    adapter.open();
                    adapter.insertEntry(entry);
                    adapter.close();
                }

                if (!screenOff) {
                    if (_level.is_dockConnected())
                        mNotificationManager.update(
                                _level.get_dock_level(),
                                _level.get_dock_status() == Constants.DOCK_STATE_CHARGING);
                    else
                        mNotificationManager.hide();
                    BatteryWidgetUpdater.updateAllWidgets(_context, _level, null);
                }
            }
        }.setData(context, level, newData);
        new Thread(runnable).start();
    }
}
