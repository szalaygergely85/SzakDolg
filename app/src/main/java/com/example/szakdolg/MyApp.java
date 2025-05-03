package com.example.szakdolg;

import android.app.Application;

import com.example.szakdolg.logging.CrashHandler;

import timber.log.Timber;


public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Set the global crash handler
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(this));

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }


        /*
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree()); // Logs to Logcat
        } else {
            // Optionally plant a tree for remote logging in release
            Timber.plant(new ReleaseTree());
        }*/
    }
}
