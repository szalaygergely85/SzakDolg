package com.example.szakdolg.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.szakdolg.R;

public class MessageWorker extends Worker {

    private static final String CHANNEL_ID = "default_channel_id";
    private static final String CHANNEL_NAME = "Default Channel";

    public MessageWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.e("MessageWorker", "doWork: Starting background work");

        try {
            fetchMessages();
            Log.e("MessageWorker", "doWork: Background work completed successfully");
            return Result.success();
        } catch (Throwable throwable) {
            Log.e("MessageWorker", "doWork: Error occurred", throwable);
            return Result.failure();
        }
    }

    private void fetchMessages() {

        Log.e("MessageWorker", "Hellooooooo");
        /*


        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getMessages().enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Message> messages = response.body();
                    for (Message message : messages) {
                        showNotification(getApplicationContext(), "New Message", message.getText());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                // Handle failure
            }
        });*/
    }

    private void showNotification(Context context, String title, String message) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_menu_notifications)
                .setAutoCancel(true);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
