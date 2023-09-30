package com.vvautotest;

import android.app.Application;

import com.androidnetworking.AndroidNetworking;

public class VVApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidNetworking.initialize(getApplicationContext());
    }
}
