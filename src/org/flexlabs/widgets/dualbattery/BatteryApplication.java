package org.flexlabs.widgets.dualbattery;

import android.app.Application;

/**
 * Created by IntelliJ IDEA.
 * User: Artiom Chilaru
 * Date: 13/06/11
 * Time: 21:56
 */
public class BatteryApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //Useful for debugging builds
        Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(getFilesDir().getAbsolutePath()));
    }
}