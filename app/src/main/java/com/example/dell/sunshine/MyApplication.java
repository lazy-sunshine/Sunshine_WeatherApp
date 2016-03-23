package com.example.dell.sunshine;

import android.app.Application;
import android.content.Context;

/**
 * Created by DeLL on 12/18/2015.
 */
public class MyApplication extends Application {

    public static MyApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance=this;
    }
    public static MyApplication getInstance(){
        return sInstance;
    }
    public static Context getAppContext(){
        return sInstance.getApplicationContext();

    }
}
