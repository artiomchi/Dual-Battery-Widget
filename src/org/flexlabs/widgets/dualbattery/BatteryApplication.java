package org.flexlabs.widgets.dualbattery;

import android.app.Application;

/**
 * Created by IntelliJ IDEA.
 * User: ArtiomChi
 * Date: 13/06/11
 * Time: 21:56
 */
public class BatteryApplication extends Application {
    private static BatteryApplication _instance;
    public static BatteryApplication getInstance() { return _instance; }
    
    @Override
    public void onCreate() {
        super.onCreate();
        _instance = this;
    }
}