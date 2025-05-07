package com.zen_vy.chat.logging;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.zen_vy.chat.retrofit.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private final Context context;

    private final LogApiService logApiService;
    private final Thread.UncaughtExceptionHandler defaultHandler;

    public CrashHandler(Context context) {
        this.context = context.getApplicationContext();
        this.defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        this.logApiService =
                RetrofitClient.getRetrofitInstance().create(LogApiService.class);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        sendCrashToServer(e);

        // Let the system handle it (crash the app)
        if (defaultHandler != null) {
            defaultHandler.uncaughtException(t, e);
        }
    }

    private void sendCrashToServer(Throwable e) {
        new Thread(() -> {
            try {


            CrashLog crashLog = new CrashLog(e.getMessage(), Log.getStackTraceString(e), Build.MODEL + " " + Build.DEVICE, Build.VERSION.RELEASE);

                Timber.w("Sending Crash Report: " + crashLog.getStackTrace());
            logApiService.sendCrashReport(crashLog).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Timber.w(call + " - Crashreport sent");
                }

                @Override
                public void onFailure(Call<Void> call, Throwable throwable) {
                    Timber.e(throwable, call.toString());
                }
            });


            } catch (Exception ex) {
                Timber.e(ex);
            }
        }).start();
    }
}
