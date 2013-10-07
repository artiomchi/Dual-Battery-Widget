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

import org.androidannotations.annotations.EBean;

@EBean(scope = EBean.Scope.Singleton)
public class DaoSessionWrapper {
    private Context mContext;
    private DaoMaster mDaoMaster;

    public DaoSessionWrapper(Context context) {
        mContext = context;
    }

    public DaoSession getSession() {
        if (mDaoMaster == null) {
            DualBatteryOpenHelper helper = new DualBatteryOpenHelper(mContext);
            mDaoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return mDaoMaster.newSession();
    }
}
