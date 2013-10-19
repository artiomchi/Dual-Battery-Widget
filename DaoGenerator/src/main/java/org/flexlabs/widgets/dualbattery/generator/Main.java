package org.flexlabs.widgets.dualbattery.generator;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class Main {
    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(4, "org.flexlabs.widgets.dualbattery.storage");
        schema.enableKeepSectionsByDefault();

        addBatteryLevel(schema);
        addScreenState(schema);

        new DaoGenerator().generateAll(schema, "..\\DualBatteryWidget\\src\\main\\java");
    }

    private static void addRelationship(Entity parent, Entity children, String foreignKey, Boolean optional) {
        Property.PropertyBuilder propertyBuilder = children.addLongProperty(foreignKey);
        if (!optional) propertyBuilder.notNull();
        Property property = propertyBuilder.getProperty();

        parent.addToMany(children, property);
        children.addToOne(parent, property);
    }

    private static Entity addBatteryLevel(Schema schema) {
        Entity batteryLevel = schema.addEntity("BatteryLevels");
        batteryLevel.addIdProperty().autoincrement();
        batteryLevel.addDateProperty("time").notNull();
        batteryLevel.addIntProperty("typeId").notNull();
        batteryLevel.addIntProperty("status").notNull();
        batteryLevel.addIntProperty("level").notNull();
        return batteryLevel;
    }

    private static Entity addScreenState(Schema schema) {
        Entity screenState = schema.addEntity("ScreenStates");
        screenState.addIdProperty().autoincrement();
        screenState.addDateProperty("time").notNull();
        screenState.addBooleanProperty("screenOn").notNull();
        return screenState;
    }
}
