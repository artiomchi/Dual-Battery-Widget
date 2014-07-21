package org.flexlabs.widgets.dualbattery.storage;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import org.flexlabs.widgets.dualbattery.storage.BatteryLevels;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table BATTERY_LEVELS.
*/
public class BatteryLevelsDao extends AbstractDao<BatteryLevels, Long> {

    public static final String TABLENAME = "BATTERY_LEVELS";

    /**
     * Properties of entity BatteryLevels.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Time = new Property(1, java.util.Date.class, "time", false, "TIME");
        public final static Property TypeId = new Property(2, int.class, "typeId", false, "TYPE_ID");
        public final static Property Status = new Property(3, int.class, "status", false, "STATUS");
        public final static Property Level = new Property(4, int.class, "level", false, "LEVEL");
    };


    public BatteryLevelsDao(DaoConfig config) {
        super(config);
    }
    
    public BatteryLevelsDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'BATTERY_LEVELS' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'TIME' INTEGER NOT NULL ," + // 1: time
                "'TYPE_ID' INTEGER NOT NULL ," + // 2: typeId
                "'STATUS' INTEGER NOT NULL ," + // 3: status
                "'LEVEL' INTEGER NOT NULL );"); // 4: level
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'BATTERY_LEVELS'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, BatteryLevels entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getTime().getTime());
        stmt.bindLong(3, entity.getTypeId());
        stmt.bindLong(4, entity.getStatus());
        stmt.bindLong(5, entity.getLevel());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public BatteryLevels readEntity(Cursor cursor, int offset) {
        BatteryLevels entity = new BatteryLevels( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            new java.util.Date(cursor.getLong(offset + 1)), // time
            cursor.getInt(offset + 2), // typeId
            cursor.getInt(offset + 3), // status
            cursor.getInt(offset + 4) // level
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, BatteryLevels entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setTime(new java.util.Date(cursor.getLong(offset + 1)));
        entity.setTypeId(cursor.getInt(offset + 2));
        entity.setStatus(cursor.getInt(offset + 3));
        entity.setLevel(cursor.getInt(offset + 4));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(BatteryLevels entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(BatteryLevels entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}