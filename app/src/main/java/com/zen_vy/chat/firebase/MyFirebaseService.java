package com.zen_vy.chat.firebase;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.zen_vy.chat.R;
import com.zen_vy.chat.activity.main.MainActivity;
import com.zen_vy.chat.constans.SharedPreferencesConstants;
import com.zen_vy.chat.util.SharedPreferencesUtil;

import java.util.List;
import java.util.Map;
import timber.log.Timber;

public class MyFirebaseService extends FirebaseMessagingService {

   @Override
   public void onNewToken(@NonNull String token) {
      super.onNewToken(token);
      //TODO I should fix this:
      /*
		Device device = new Device(currentUser.getUserId(), fcmToken);

		deviceService.addDevice(
				device,
				new DeviceService.DeviceCallback<Device>() {
					@Override
					public void onSuccess(Device data) {
						Timber.i("Fetching FCM registration token is success");
					}

					@Override
					public void onError(Throwable t) {}
				}
		);*/
   }

   @Override
   public void onMessageReceived(RemoteMessage remoteMessage) {
      super.onMessageReceived(remoteMessage);

      if (!remoteMessage.getData().isEmpty()) {
         Map<String, String> data = remoteMessage.getData();

         String title = data.get("title");
         String text = data.get("content");

         Timber.i("Message from %s: %s", title, text);
         if(SharedPreferencesUtil.getBooleanPreferences(this, SharedPreferencesConstants.NOTIFICATIONS)) {
            showNotification(title, text);
         }
      }
   }

   private void showNotification(String title, String message) {
      String channelId = "chat_messages_channel";
      String channelName = "Chat Messages";

      NotificationManager notificationManager =
         (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

      // For Android 8+ (Oreo), we need a channel
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
         NotificationChannel channel = new NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
         );
         notificationManager.createNotificationChannel(channel);
      }

      if(!isAppInForeground()){


      // Create notification
      NotificationCompat.Builder builder = new NotificationCompat.Builder(
         this,
         channelId
      )
         .setSmallIcon(R.drawable.icon_zenvy) // put an icon in res/drawable
         .setContentTitle(title)
         .setContentText(message)
         .setAutoCancel(true)
         .setPriority(NotificationCompat.PRIORITY_HIGH);

      //TODO open convenrsation
      /*
		Intent intent = new Intent(context, ChatActivity.class);
		intent.putExtra(IntentConstants.CONVERSATION_DTO, data);
		intent.setFlags(
				Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP
		);

		// 2. Create the pending intent
		PendingIntent pendingIntent = PendingIntent.getActivity(
				context,
				RandomUtil.getSecureRandomInt(),
				intent,
				PendingIntent.FLAG_UPDATE_CURRENT |
						PendingIntent.FLAG_IMMUTABLE // FLAG_IMMUTABLE for Android 12+
		);
*/

      // Optional: open MainActivity when clicked
      Intent intent = new Intent(this, MainActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      PendingIntent pendingIntent = PendingIntent.getActivity(
         this,
         0,
         intent,
         PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
      );
      builder.setContentIntent(pendingIntent);

      // Show it
      notificationManager.notify(
         (int) System.currentTimeMillis(),
         builder.build()
      );
      }
   }

   private boolean isAppInForeground() {
      ActivityManager activityManager = (ActivityManager) getSystemService(
              Context.ACTIVITY_SERVICE
      );
      List<ActivityManager.RunningAppProcessInfo> appProcesses =
              activityManager.getRunningAppProcesses();
      if (appProcesses == null) {
         return false;
      }
      final String packageName = getPackageName();
      for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
         if (
                 appProcess.importance ==
                         ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                         appProcess.processName.equals(packageName)
         ) {
            return true;
         }
      }
      return false;
   }
}
