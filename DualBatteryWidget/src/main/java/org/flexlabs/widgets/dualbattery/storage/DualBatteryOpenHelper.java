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
import android.database.sqlite.SQLiteDatabase;

public class DualBatteryOpenHelper extends DaoMaster.OpenHelper {
    public DualBatteryOpenHelper(Context context) {
        super(context, "BatteryLevels.db", null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 2) {
            db.execSQL("ALTER TABLE 'BatteryLevels' RENAME TO 'BatteryLevels_Old'");

            DaoMaster.createAllTables(db, true);

            db.execSQL(
                "INSERT INTO " + BatteryLevelsDao.TABLENAME + "(" +
                    BatteryLevelsDao.Properties.Time.columnName + "," +
                    BatteryLevelsDao.Properties.Status.columnName + "," +
                    BatteryLevelsDao.Properties.Level.columnName + "," +
                    BatteryLevelsDao.Properties.DockStatus.columnName + "," +
                    BatteryLevelsDao.Properties.DockLevel.columnName + "," +
                    BatteryLevelsDao.Properties.ScreenOn.columnName + ")" +
                "SELECT " +
                    "Time, " +
                    "Status, " +
                    "Level, " +
                    "DockStatus, " +
                    "DockLevel, " +
                    "ScreenState" +
                "FROM BatteryLevels_Old");

            db.execSQL("DROP TABLE 'BatteryLevels_Old'");
            oldVersion = 3;
        }
        //if (oldVersion != newVersion) {
        //    DaoMaster.dropAllTables(db, true);
        //    onCreate(db);
        //}
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        super.onCreate(db);
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession session = daoMaster.newSession();
    }
}
