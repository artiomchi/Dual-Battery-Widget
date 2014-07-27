package org.flexlabs.widgets.dualbattery;

import android.content.Context;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.flexlabs.dualbattery.batteryengine.BatteryLevel;
import org.flexlabs.dualbattery.batteryengine.BatteryMonitor;
import org.flexlabs.dualbattery.batteryengine.BatteryType;
import org.flexlabs.widgets.dualbattery.storage.BatteryLevels;
import org.flexlabs.widgets.dualbattery.storage.DualBatteryDao;

import java.util.List;

@EBean(scope = EBean.Scope.Singleton)
public class BatteryLevelMonitor implements BatteryMonitor.OnBatteryStatusUpdatedListener {
    private BatteryMonitor monitor;
    @Bean DualBatteryDao batteryDao;
    @RootContext Context mContext;

    public interface OnBatteriesUpdatedListener {
        void batteriesUpdated();
    }

    private OnBatteriesUpdatedListener listener;
    public void setOnBatteriesUpdatedListener(OnBatteriesUpdatedListener listener) {
        this.listener = listener;
    }

    @AfterInject
    @Background
    public void loadOldLevels() {
        BatteryLevels level = batteryDao.getLatestActiveBatteryLevel(BatteryType.Main);
        if (level != null && lastKnownLevel_Main != null)
            lastKnownLevel_Main = level.getLevel();

        level = batteryDao.getLatestActiveBatteryLevel(BatteryType.AsusDock);
        if (level != null && lastKnownLevel_Dock != null)
            lastKnownLevel_Dock = level.getLevel();

        level = batteryDao.getLatestActiveBatteryLevel(BatteryType.AsusPad);
        if (level != null && lastKnownLevel_Pad != null)
            lastKnownLevel_Pad = level.getLevel();
    }

    public static Integer lastKnownLevel_Main = null;
    public static Integer lastKnownLevel_Dock = null;
    public static Integer lastKnownLevel_Pad = null;

    public static Integer getOldLevel(BatteryType batteryType) {
        if (batteryType == BatteryType.Main)
            return lastKnownLevel_Main;
        if (batteryType == BatteryType.AsusDock)
            return lastKnownLevel_Dock;
        if (batteryType == BatteryType.AsusPad)
            return lastKnownLevel_Pad;
        return null;
    }

    public void startMonitoring() {
        if (monitor == null) {
            monitor = new BatteryMonitor(mContext);
            monitor.setBatteryUpdatedListener(this);
        }

        monitor.startMonitoring(false);
    }

    public void stopMonitoring() {
    }

    public List<BatteryLevel> currentBatteryLevels = null;
    public BatteryLevel mainBattery = null;
    public BatteryLevel dockBattery = null;
    public BatteryLevel padBattery = null;

    private static boolean gotDock = false;
    public static boolean getGotDock() {
        return gotDock;
    }

    private static boolean gotPad = false;
    public static boolean getGotPad() {
        return gotPad;
    }

    @Override
    public void batteryLevelsUpdated(List<BatteryLevel> batteryLevels) {
        for (BatteryLevel level : batteryLevels) {
            BatteryLevel oldValue;
            if (level.getType() == BatteryType.Main) {
                oldValue = mainBattery;
                mainBattery = level;
            } else if (level.getType() == BatteryType.AsusDock) {
                oldValue = dockBattery;
                dockBattery = level;
                gotDock = true;
            } else if (level.getType() == BatteryType.AsusPad) {
                oldValue = padBattery;
                padBattery = level;
                gotPad = true;
            } else continue;

            if (oldValue == null || oldValue.getStatus() != level.getStatus() || oldValue.getLevel() != level.getLevel()) {
                batteryDao.addBatteryLevel(level.getType(), level.getStatus().getIntValue(), level.getLevel());
            }
        }
        currentBatteryLevels = batteryLevels;
        listener.batteriesUpdated();
    }
}
