package com.example.apple.newsapp;

import android.app.Application;
import android.content.Context;

/**
 * Created by justin on 2017/7/22.
 */

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    public static Context getContext() {
        return context;
    }
}
