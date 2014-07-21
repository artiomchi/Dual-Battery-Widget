package org.flexlabs.widgets.dualbattery.storage;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.flexlabs.dualbattery.batteryengine.BatteryStatus;
import org.flexlabs.dualbattery.batteryengine.BatteryType;

import java.util.Date;
import java.util.List;

@EBean
public class DualBatteryDao {
    @Bean DaoSessionWrapper mSessionWrapper;

    public BatteryLevels getLatestActiveBatteryLevel(BatteryType batteryType) {
        BatteryLevelsDao dao = mSessionWrapper.getSession().getBatteryLevelsDao();
        List<BatteryLevels> lastStates = dao.queryBuilder()
                .where(BatteryLevelsDao.Properties.TypeId.eq(batteryType.getIntValue()))
                .where(BatteryLevelsDao.Properties.Status.in(BatteryStatus.activeValues()))
                .orderDesc(BatteryLevelsDao.Properties.Time)
                .limit(1)
                .list();

        if (lastStates.size() > 0)
            return lastStates.get(0);

        return null;
    }

    public BatteryLevels getLatestBatteryLevel(BatteryType batteryType) {
        BatteryLevelsDao dao = mSessionWrapper.getSession().getBatteryLevelsDao();
        List<BatteryLevels> lastStates = dao.queryBuilder()
                .where(BatteryLevelsDao.Properties.TypeId.eq(batteryType.getIntValue()))
                .orderDesc(BatteryLevelsDao.Properties.Time)
                .limit(1)
                .list();

        if (lastStates.size() > 0)
            return lastStates.get(0);

        return null;
    }

    @Background
    public void addBatteryLevel(BatteryType batteryType, int status, int level) {
        BatteryLevels lastLevel = getLatestBatteryLevel(batteryType);
        if (lastLevel != null) {
            if (lastLevel.getStatus() == status && lastLevel.getLevel() == level)
                return;
        }

        BatteryLevels batteryLevels = new BatteryLevels(null, new Date(), batteryType.getIntValue(), status, level);
        mSessionWrapper.getSession().getBatteryLevelsDao().insert(batteryLevels);
    }

    @Background
    public void addScreenStatus(boolean screenOn) {
        ScreenStatesDao dao = mSessionWrapper.getSession().getScreenStatesDao();
        List<ScreenStates> lastStates = dao.queryBuilder()
                .orderDesc(ScreenStatesDao.Properties.Time)
                .limit(1)
                .list();

        if (lastStates.size() > 0) {
            ScreenStates lastState = lastStates.get(0);
            if (lastState.getScreenOn() == screenOn)
                return;
        }

        ScreenStates screenStates = new ScreenStates(null, new Date(), screenOn);
        mSessionWrapper.getSession().getScreenStatesDao().insert(screenStates);
    }
}
