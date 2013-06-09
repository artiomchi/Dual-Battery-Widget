/*
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

package org.flexlabs.widgets.dualbattery;

import android.app.Application;
import android.app.PendingIntent;
import android.content.pm.PackageManager;

import com.googlecode.androidannotations.annotations.EApplication;

import net.robotmedia.billing.BillingController;
import net.robotmedia.billing.BillingRequest;
import net.robotmedia.billing.IBillingObserver;
import net.robotmedia.billing.model.Transaction;

@EApplication
public class BatteryApplication extends Application {
    private static BatteryApplication _instance;
    public static BatteryApplication getInstance() { return _instance; }
    
    @Override
    public void onCreate() {
        super.onCreate();
        _instance = this;
        try {
            String pkg = getPackageName();
            Constants.VERSION = getPackageManager().getPackageInfo(pkg, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Constants.VERSION = "?";
        }
        

        BillingController.setDebug(Constants.DEBUG);
        BillingController.setConfiguration(new BillingController.IConfiguration() {
            @Override
            public byte[] getObfuscationSalt() {
                return new byte[] {41, -90, -116, -41, 77, -53, 127, -110, -127, -96, -88, 77, 127, 117, 1, 73, 57, 110, 48, -116};
            }

            @Override
            public String getPublicKey() {
                return "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmRLx8FRLePsmwi0uXID5uUzf6oWe8KFmUtLaApbiNIG+qDrPAVScKjE1KEdNWBAlzyc70Ohihh73e7/BBuzLECZFjZo7Xbks6JdZ2zxii8OCclDdYq5MZQkkuLUCrNd2B97+JwKaYjdDdjkIVcUP0jWyGWEXnFo6pjZK0VRLEFITDbt4vq/NfJpxWrnI8j95GWJUTlZ26TdY/1tjUaXr6l3GuWj71RlvRQuCPnjneLwZjdLjxYfZknGRhHTCXlIVfdGhcbuaOem1IL+R5xFbJftAXJfM2kgoNIb/FhbNLWjM1jdjemWaWyhhcz3AhEk92Fbc5ZLxhsh4oYcVB0uLRwIDAQAB";
            }
        });
        BillingController.registerObserver(new IBillingObserver() {
            @Override
            public void onBillingChecked(boolean supported) {
                Constants.HAS_GPLAY_BILLING = supported;
                BillingController.unregisterObserver(this);
            }

            @Override public void onPurchaseIntent(String itemId, PendingIntent purchaseIntent) { }
            @Override public void onPurchaseStateChanged(String itemId, Transaction.PurchaseState state) { }
            @Override public void onRequestPurchaseResponse(String itemId, BillingRequest.ResponseCode response) { }
            @Override public void onTransactionsRestored() { }
			@Override public void onSubscriptionChecked(boolean supported) { }
        });
        BillingController.checkBillingSupported(this);
    }
}