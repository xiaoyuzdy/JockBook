package com.example.he.jockbook;

import android.app.Application;
import android.content.Context;

/**
 * Created by he on 2017/2/20.
 */

public class myApplication extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }
}
