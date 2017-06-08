package com.example.leakcanary;

import android.app.Application;
import android.os.StrictMode;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * 注册内存泄漏框架
 */
public class APP extends Application {

    private static APP instance;

    private RefWatcher mRefWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        //注册内存框架
        instance = this;
//
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        enabledStrictMode();
        mRefWatcher = LeakCanary.install(this);
    }


    public static APP getAppIntance() {
        return instance;
    }


    private static void enabledStrictMode() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder() //
                .detectAll() //
                .penaltyLog() //
                .penaltyDeath() //
                .build());
    }

    public RefWatcher getRefWatcher() {
        return mRefWatcher;
    }


}
