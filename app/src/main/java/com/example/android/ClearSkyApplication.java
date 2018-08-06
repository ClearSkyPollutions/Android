package com.example.android;

import android.app.Application;

import com.example.android.network.RequestQueueSingleton;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class ClearSkyApplication extends Application {
    @Override
    public void onCreate() {
        RequestQueueSingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        super.onCreate();
        Realm.init(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .name("clearSky.realm")
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfig);
    }
}
