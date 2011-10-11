package org.flexlabs.widgets.dualbattery;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.text.TextUtils;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Flexer
 * Date: 03/07/11
 * Time: 22:08
 * To change this template use File | Settings | File Templates.
 */
public class BatteryLevelAdapter {
    private static final String DB_NAME = "BatteryLevels.db";
    private static final String DB_TABLE = "BatteryLevels";
    private static final int DB_VERSION = 2;

    public static final String KEY_ID = "_id";
    public static final int ORD_ID = 0;
    public static final String KEY_TIME = "Time";
    public static final int ORD_TIME = 1;
    public static final String KEY_STATUS = "Status";
    public static final int ORD_STATUS = 2;
    public static final String KEY_LEVEL = "Level";
    public static final int ORD_LEVEL = 3;
    public static final String KEY_DOCK_STATUS = "DockStatus";
    public static final int ORD_DOCK_STATUS = 4;
    public static final String KEY_DOCK_LEVEL = "DockLevel";
    public static final int ORD_DOCK_LEVEL = 5;
    public static final String KEY_SCREEN_STATE = "ScreenState";
    public static final int ORD_SCREEN_STATE = 6;

    private static final String DB_CREATE = "CREATE TABLE " + DB_TABLE + " (" +
            KEY_ID + " integer PRIMARY KEY AUTOINCREMENT, " +
            KEY_TIME + " LONG NOT NULL, " +
            KEY_STATUS + " INT NOT NULL, " +
            KEY_LEVEL + " INT NOT NULL, " +
            KEY_DOCK_STATUS + " INT NOT NULL, " +
            KEY_DOCK_LEVEL + " INT, " +
            KEY_SCREEN_STATE + " INT NOT NULL);";

    private SQLiteDatabase db;
    private final Context context;
    private DBHelper dbHelper;

    public BatteryLevelAdapter(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
    }

    public BatteryLevelAdapter open() {
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        db.close();
    }

    public long insertEntry(Entry entry) {
        ContentValues values = new ContentValues();
        values.put(KEY_TIME, entry.date.getTime());
        values.put(KEY_STATUS, entry.status);
        values.put(KEY_LEVEL, entry.level);
        values.put(KEY_DOCK_STATUS, entry.dock_status);
        values.put(KEY_DOCK_LEVEL, entry.dock_level);
        values.put(KEY_SCREEN_STATE, entry.screenOff ? 0 : 1);

        return db.insert(DB_TABLE, null, values);
    }

    public boolean removeEntry(long index) {
        return db.delete(DB_TABLE, KEY_ID + " = " + index, null) > 0;
    }

    public Cursor getAllEntries() {
        return db.query(
                DB_TABLE,
                new String[] { KEY_ID, KEY_TIME, KEY_STATUS, KEY_LEVEL, KEY_DOCK_STATUS, KEY_DOCK_LEVEL, KEY_SCREEN_STATE },
                null, null, null, null, null);
    }

    public Cursor getRecentEntries() {
        return db.query(
                DB_TABLE,
                new String[] { KEY_ID, KEY_TIME, KEY_STATUS, KEY_LEVEL, KEY_DOCK_STATUS, KEY_DOCK_LEVEL, KEY_SCREEN_STATE },
                KEY_TIME + " > " + (new Date().getTime() - 1000 * 60 * 60 * 24 * 7), null, null, null, null);
    }

    public Cursor query(String[] projection, String selection, String[] selectionArgs, String sort) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(DB_TABLE);

        String orderBy = TextUtils.isEmpty(sort) ? KEY_TIME : sort;

        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);
        return c;
    }

    public Entry getEntry(long index) {
        return null;
    }

    public static class Entry {
        public Date date;
        public int status, level, dock_status;
        public Integer dock_level;
        public boolean screenOff;

        public Entry(int status, int level, int dock_status, Integer dock_level, boolean screenOff) {
            this.status = status;
            this.level = level;
            this.dock_status = dock_status;
            this.dock_level = dock_level;
            this.date = new Date();
            this.screenOff = screenOff;
        }
    }

    private static class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(DB_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
            onCreate(sqLiteDatabase);
        }
    }
}
