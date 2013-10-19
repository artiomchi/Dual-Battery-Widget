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

package org.flexlabs.widgets.dualbattery.storage;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.flexlabs.dualbattery.batteryengine.BatteryType;

public class DualBatteryOpenHelper extends DaoMaster.OpenHelper {
    public DualBatteryOpenHelper(Context context) {
        super(context, "BatteryLevels.db", null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        int currentVersion = oldVersion;
        if (currentVersion == 2) {
            upgradeFrom2(db);
            currentVersion = 3;
        }
        if (currentVersion == 3) {
            upgradeFrom3(db);
            currentVersion = 4;
        }
        //if (oldVersion != newVersion) {
        //    DaoMaster.dropAllTables(db, true);
        //    onCreate(db);
        //}
    }

    private void upgradeFrom2(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE 'BatteryLevels' RENAME TO 'BatteryLevels_Old'");
        DaoMaster.createAllTables(db, true);

        db.execSQL(
                "INSERT INTO BATTERY_LEVELS (TIME, STATUS, LEVEL, DOCK_STATUS, DOCK_LEVEL, SCREEN_ON)" +
                        "SELECT Time, Status, Level, DockStatus, DockLevel, ScreenState" +
                        "FROM BatteryLevels_Old");

        db.execSQL("DROP TABLE 'BatteryLevels_Old'");
    }

    private void upgradeFrom3(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE 'BATTERY_LEVELS' RENAME TO 'BATTERY_LEVELS_OLD'");
        DaoMaster.createAllTables(db, true);

        Cursor cursor = db.rawQuery("SELECT TIME, STATUS, LEVEL, DOCK_STATUS, DOCK_LEVEL, SCREEN_ON FROM BATTERY_LEVELS_OLD", null);
        cursor.moveToFirst();

        Integer status = null, level = null, dock_status = null, dock_level = null;
        int tempStatus, tempLevel;
        Boolean screenOn = null;
        boolean tempScreenOn;
        long time;
        while (!cursor.isAfterLast()) {
            time = cursor.getLong(0);
            tempStatus = cursor.getInt(1);
            tempLevel = cursor.getInt(2);
            if (status == null || status != tempStatus || level != tempLevel) {
                status = tempStatus;
                level = tempLevel;
                upgradeFrom3_InsertBatteryLevel(db, time, BatteryType.Main, status, level);
            }

            if (!cursor.isNull(3) && !cursor.isNull(4)) {
                tempStatus = cursor.getInt(3);
                tempLevel = cursor.getInt(4);
                if (dock_status == null || dock_status != tempStatus && dock_level != tempLevel) {
                    dock_status = tempStatus;
                    dock_level = tempLevel;
                    upgradeFrom3_InsertBatteryLevel(db, time, BatteryType.AsusDock, dock_status, dock_level);
                }
            }

            tempScreenOn = cursor.getShort(5) != 0;
            if (screenOn == null || screenOn != tempScreenOn) {
                screenOn = tempScreenOn;
                upgradeFrom3_InsertScreenStatus(db, time, screenOn);
            }

            cursor.moveToNext();
        }
    }

    private void upgradeFrom3_InsertBatteryLevel(SQLiteDatabase db, long time, BatteryType type, int status, int level) {
        db.rawQuery(
                "INSERT INTO " + BatteryLevelsDao.TABLENAME + " ( " +
                        BatteryLevelsDao.Properties.Time.columnName + ", " +
                        BatteryLevelsDao.Properties.TypeId.columnName + ", " +
                        BatteryLevelsDao.Properties.Status.columnName + ", " +
                        BatteryLevelsDao.Properties.Level.columnName + ")" +
                        "VALUES (?, ?, ?, ?)", new String[] { String.valueOf(time), String.valueOf(type.getIntValue()), String.valueOf(status), String.valueOf(level) });
    }

    private void upgradeFrom3_InsertScreenStatus(SQLiteDatabase db, long time, boolean screenState) {
        db.rawQuery(
                "INSERT INTO " + ScreenStatesDao.TABLENAME + " ( " +
                        ScreenStatesDao.Properties.Time.columnName + ", " +
                        ScreenStatesDao.Properties.ScreenOn.columnName + ")" +
                        "VALUES (?, ?)", new String[] { String.valueOf(time), String.valueOf(screenState ? 1l : 0l) });
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        super.onCreate(db);
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession session = daoMaster.newSession();
    }
}
