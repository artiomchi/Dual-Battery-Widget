package org.flexlabs.widgets.dualbattery;

import android.content.Context;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.flexlabs.dualbattery.batteryengine.BatteryLevel;
import org.flexlabs.dualbattery.batteryengine.BatteryMonitor;
import org.flexlabs.dualbattery.batteryengine.BatteryType;
import org.flexlabs.widgets.dualbattery.storage.DualBatteryDao;

import java.util.List;

@EBean(scope = EBean.Scope.Singleton)
public class BatteryLevelMonitor implements BatteryMonitor.OnBatteryStatusUpdatedListener {
    private BatteryMonitor monitor;
    @Bean
    DualBatteryDao batteryDao;
    @RootContext Context mContext;

    public interface OnBatteriesUpdatedListener {
        void batteriesUpdated();
    }

    private OnBatteriesUpdatedListener listener;
    public void setOnBatteriesUpdatedListener(OnBatteriesUpdatedListener listener) {
        this.listener = listener;
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
            } else continue;

            if (oldValue == null || oldValue.getStatus() != level.getStatus() || oldValue.getLevel() != level.getLevel()) {
                batteryDao.addBatteryLevel(level.getType(), level.getStatus().getIntValue(), level.getLevel());
            }
        }
        currentBatteryLevels = batteryLevels;
        listener.batteriesUpdated();
    }
}
