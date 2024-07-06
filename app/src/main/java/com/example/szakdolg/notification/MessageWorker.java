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

import com.example.szakdolg.DTO.MessageBoard;
import com.example.szakdolg.R;
import com.example.szakdolg.constans.SharedPreferencesConstans;
import com.example.szakdolg.conversation.ConversationApiHelper;
import com.example.szakdolg.conversation.ConversationApiService;
import com.example.szakdolg.retrofit.RetrofitClient;
import com.example.szakdolg.user.UserUtil;
import com.example.szakdolg.user.entity.User;
import com.example.szakdolg.util.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageWorker extends Worker {

    private static final String CHANNEL_ID = "default_channel_id";
    private static final String CHANNEL_NAME = "Default Channel";

    private ConversationApiService conversationApiService = RetrofitClient
            .getRetrofitInstance()
            .create(ConversationApiService.class);
    String userToken;


    public MessageWorker(@NonNull Context context, @NonNull WorkerParameters workerParams
) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        userToken = SharedPreferencesUtil.getStringPreference(getApplicationContext(), SharedPreferencesConstans.USERTOKEN);

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

        Call<ArrayList<MessageBoard>> call =
                conversationApiService.getConversationWithNewMessage(
                        userToken
                );
        call.enqueue(
                new Callback<ArrayList<MessageBoard>>(){
                    @Override
                    public void onResponse(Call<ArrayList<MessageBoard>> call, Response<ArrayList<MessageBoard>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<MessageBoard> messageBoardEntries = response.body();
                            if (messageBoardEntries!= null) {
                                for (MessageBoard messageBoardEntry : messageBoardEntries) {
                                    Log.e("Hello message", "Message from" + messageBoardEntry.getMessage().getContent());
                                    showNotification(getApplicationContext(), "New Message: ", "" + messageBoardEntry.getMessage().getSenderId());
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<MessageBoard>> call, Throwable t) {

                    };
                });

    }

    public void showNotification(Context context, String title, String message) {
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
