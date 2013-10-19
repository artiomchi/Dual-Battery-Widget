package org.flexlabs.dualbattery.batteryengine;

import java.util.Date;

public class BatteryLevel {
    public BatteryLevel(BatteryType type, BatteryStatus status, int level) {
        this.type = type;
        this.status = status;
        this.level = level;
        this.time = System.currentTimeMillis();
    }

    private BatteryType type;
    private BatteryStatus status;
    private int level;
    private long time;

    public BatteryType getType() {
        return type;
    }

    public int getTypeName() {
        return type.getString();
    }

    public BatteryStatus getStatus() {
        return status;
    }

    public int getStatusName() {
        return status.getString();
    }

    public int getLevel() {
        return level;
    }

    public Date getTime() {
        return new Date(time);
    }
}
