package io.github.trojan_gfw.igniter;

import android.app.Application;

import io.github.trojan_gfw.igniter.initializer.InitializerHelper;

public class IgniterApplication extends Application {
    private static final String TAG = "IgniterApplication";
    @Override
    public void onCreate() {
        LogHelper.i(TAG, "Application onCreate");
        super.onCreate();
        InitializerHelper.runInit(this);
    }
}
