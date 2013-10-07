package org.flexlabs.widgets.dualbattery.generator;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class Main {
    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(3, "org.flexlabs.widgets.dualbattery.storage");
        schema.enableKeepSectionsByDefault();

        Entity batteryLevel = addBatteryLevel(schema);

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
        Entity player = schema.addEntity("BatteryLevels");
        player.addIdProperty().autoincrement();
        player.addDateProperty("time").notNull();
        player.addIntProperty("status").notNull();
        player.addIntProperty("level").notNull();
        player.addIntProperty("dockStatus").notNull();
        player.addIntProperty("dockLevel");
        player.addBooleanProperty("screenOn").notNull();
        return player;
    }
}
