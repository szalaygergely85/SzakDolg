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
import com.example.szakdolg.constans.IntentConstants;
import com.example.szakdolg.constans.SharedPreferencesConstants;
import com.example.szakdolg.retrofit.RetrofitClient;
import com.example.szakdolg.models.user.entity.User;
import com.example.szakdolg.util.EncryptionHelper;
import com.example.szakdolg.util.KeyStoreUtil;
import com.example.szakdolg.util.SharedPreferencesUtil;
import com.google.gson.Gson;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageWorker extends Worker {

   private static final String CHANNEL_ID = "default_channel_id";
   private static final String CHANNEL_NAME = "Default Channel";

   private Gson gson = new Gson();

   private User currentUser;
   private NotificationApiService notificationApiService = RetrofitClient
      .getRetrofitInstance()
      .create(NotificationApiService.class);
   String userToken;

   public MessageWorker(
      @NonNull Context context,
      @NonNull WorkerParameters workerParams
   ) {
      super(context, workerParams);
   }

   @NonNull
   @Override
   public Result doWork() {
      userToken =
      SharedPreferencesUtil.getStringPreference(
         getApplicationContext(),
         SharedPreferencesConstants.USERTOKEN
      );

      String currentUserJson = getInputData()
         .getString(IntentConstants.CURRENT_USER);
      if (currentUserJson != null) {
         currentUser = gson.fromJson(currentUserJson, User.class);
      }

      Log.e("MessageWorker", "doWork: Starting background work");

      try {
         fetchMessages();
         Log.e(
            "MessageWorker",
            "doWork: Background work completed successfully"
         );
         return Result.success();
      } catch (Throwable throwable) {
         Log.e("MessageWorker", "doWork: Error occurred", throwable);
         return Result.failure();
      }
   }

   private void fetchMessages() {
      Log.e("MessageWorker", "Hellooooooo");

      Call<List<Notification>> call =
         notificationApiService.getActiveNotifications(userToken);
      call.enqueue(
         new Callback<List<Notification>>() {
            @Override
            public void onResponse(
               Call<List<Notification>> call,
               Response<List<Notification>> response
            ) {
               if (response.isSuccessful() && response.body() != null) {
                  List<Notification> notifications = response.body();
                  if (notifications != null) {
                     for (Notification notification : notifications) {
                        Log.e("Hello message", notification.getTitle());
                        String decryptContent = null;
                        if (currentUser != null) {
                           decryptContent =
                           EncryptionHelper.decrypt(
                              notification.getContent(),
                              KeyStoreUtil.getPrivateKeyFromFile(
                                 getApplicationContext(),
                                 currentUser
                              )
                           );
                        }
                        showNotification(
                           getApplicationContext(),
                           notification.getTitle(),
                           decryptContent
                        );
                     }
                  }
               }
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {}
         }
      );
   }

   public void showNotification(Context context, String title, String message) {
      NotificationManager notificationManager =
         (NotificationManager) context.getSystemService(
            Context.NOTIFICATION_SERVICE
         );

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
         NotificationChannel channel = new NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
         );
         notificationManager.createNotificationChannel(channel);
      }

      NotificationCompat.Builder notificationBuilder =
         new NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_menu_notifications)
            .setAutoCancel(true);

      notificationManager.notify(0, notificationBuilder.build());
   }
}
