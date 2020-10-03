package com.robod.attendancesystem;

import android.app.Application;
import android.content.Context;

import org.litepal.LitePal;
import org.xutils.x;

/**
 * @author Robod
 * @date 2020/9/30 11:30
 */
public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        x.Ext.init(this);
        LitePal.initialize(this);
    }

    public static Context getContext() {
        return context;
    }

}
