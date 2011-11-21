package org.flexlabs.widgets.dualbattery;

import android.app.Application;

/**
 * Created by IntelliJ IDEA.
 * User: Artiom Chilaru
 * Date: 13/06/11
 * Time: 21:56
 *
 * Copyright 2011 Artiom Chilaru (http://flexlabs.org)
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
public class BatteryApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //Useful for debugging builds
        Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(getFilesDir().getAbsolutePath()));
    }
}