package com.zen_vy.chat.websocket;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.zen_vy.chat.R;
import com.zen_vy.chat.constans.AppConstants;
import com.zen_vy.chat.constans.IntentConstants;
import com.zen_vy.chat.models.message.MessageDatabaseUtil;
import com.zen_vy.chat.models.message.constants.MessageTypeConstants;
import com.zen_vy.chat.models.message.entity.MessageEntry;
import com.zen_vy.chat.models.user.entity.User;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import timber.log.Timber;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class WebSocketService extends Service {
   private Handler pongTimeoutHandler;
   private Runnable pongTimeoutRunnable;
   private final IBinder binder = new LocalBinder();
   private OkHttpClient client;
   private WebSocket webSocket;

   private String userToken;

   private User currentUser;

   private Context context;

   private static WebSocketService instance;

    private boolean isConnected= false;

   private Handler pingHandler;
   private Runnable pingRunnable;



   private boolean isConnecting = false;


   private static final int PONG_TIMEOUT_MS = 40000;

   private long currentPingIntervalMs = 30000; // Start with 30 seconds
   private static final long MAX_PING_INTERVAL_MS = 120000; // Max 2 minutes


   public class LocalBinder extends Binder {

      WebSocketService getService() {
         return WebSocketService.this;
      }
   }

   public static WebSocketService getInstance() {
      return instance;
   }

   @Override
   public void onCreate() {
      super.onCreate();
      instance = this;
      context = getApplicationContext();
      Timber.i("Starting WebSocketService");
   }

   @Override
   public void onTaskRemoved(Intent rootIntent) {
      super.onTaskRemoved(rootIntent);

      // Restart the service
      Intent restartServiceIntent = new Intent(getApplicationContext(), WebSocketService.class);
      restartServiceIntent.setPackage(getPackageName());
      startForegroundService(restartServiceIntent);


   }

   @Override
   public IBinder onBind(Intent intent) {
      return binder;
   }

   @Override
   public int onStartCommand(Intent intent, int flags, int startId) {
      startForegroundNotification();

      userToken = intent.getStringExtra(IntentConstants.USER_TOKEN);
      currentUser =
              (User) intent.getSerializableExtra(IntentConstants.CURRENT_USER);


      connectToWebSocket();
      return START_STICKY;
   }

   public void sendMessage(String message) {
      webSocket.send(message);
   }

   private void startForegroundNotification() {
      createNotificationChannel(); // <-- Good, creates SERVICE_CHANNEL
      Notification notification = new NotificationCompat.Builder(this, "SERVICE_CHANNEL") // <-- FIXED HERE
              .setContentTitle("WebSocket Service")
              .setContentText("Maintaining WebSocket connection")
              .setSmallIcon(R.drawable.ic_chat)
              .build();
      startForeground(1, notification);
   }

   private void createNotificationChannel() {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
         NotificationChannel serviceChannel = new NotificationChannel(
                 "SERVICE_CHANNEL",
                 "WebSocket Service",
                 NotificationManager.IMPORTANCE_LOW
         );
         serviceChannel.setDescription("Notification for WebSocket Service");

         NotificationChannel messageChannel = new NotificationChannel(
                 "MESSAGE_CHANNEL",
                 "Message Notifications",
                 NotificationManager.IMPORTANCE_HIGH
         );
         messageChannel.setDescription("Notifications for incoming chat messages");

         NotificationManager manager = getSystemService(NotificationManager.class);
         if (manager != null) {
            manager.createNotificationChannel(serviceChannel);
            manager.createNotificationChannel(messageChannel);
         }
      }
   }


   private void connectToWebSocket() {
      if (isConnecting) {
         Log.e("WebSocket", "Already trying to connect, skipping...");
         return;
      }

      isConnecting = true;


      client = new OkHttpClient();

      Request request = new Request.Builder()
         .url(AppConstants.WS_URL)
         .addHeader("token", userToken)
         .build();

      webSocket =
      client.newWebSocket(
         request,
         new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
               Log.e("WebSocket", response.message());
               isConnected = true;
               isConnecting = false;
               startPingPong();
               // Connection established

            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
               try {
                  Log.e(AppConstants.LOG_TAG, text);
                  JSONObject jsonObject = new JSONObject(text);

                  int type = jsonObject.getInt("type");

                  switch (type) {
                     case MessageTypeConstants.PING:
                        handlePong();
                        break;
                     case MessageTypeConstants.MESSAGE:
                        handleMessage(jsonObject);
                        break;
                     case MessageTypeConstants.IMAGE:
                        // Handle image message
                        break;
                     // Add more cases for other message types as needed
                     default:
                        Log.e("WebSocket", "Unknown message type: " + type);
                        break;
                  }
               } catch (JSONException e) {
                  Log.e(
                     "WebSocket",
                     "Error parsing message: " + e.getMessage()
                  );
               }
            }

            @Override
            public void onFailure(
               WebSocket webSocket,
               Throwable t,
               Response response
            ) {
               // Connection failure, handle reconnection
                isConnected = false;
               isConnecting = false;
               reconnectToWebSocket();
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {

                isConnected = false;
               isConnecting = false;
               // Connection closed
               reconnectToWebSocket();
            }
         }
      );

   }

    public boolean isConnected() {
        return isConnected;
    }

   private void startPingPong() {
      if (pingHandler != null && pingRunnable != null) {
         pingHandler.removeCallbacks(pingRunnable);
      }

      pingHandler = new Handler(Looper.getMainLooper());
      pingRunnable = new Runnable() {
         @Override
         public void run() {
            if (webSocket != null && isConnected) {
               String pingMessage = "{\"type\": " + MessageTypeConstants.PING + "}";
               webSocket.send(pingMessage);

               startPongTimeout();

               Timber.i("Sending ping message");
               pingHandler.postDelayed(this, 60000);
            }
         }
      };

      pingHandler.post(pingRunnable);
   }


   private void reconnectToWebSocket() {
      if (pingHandler != null && pingRunnable != null) {
         pingHandler.removeCallbacks(pingRunnable);
      }

      Handler handler = new Handler(Looper.getMainLooper());

      currentPingIntervalMs = Math.min(currentPingIntervalMs + 10000, MAX_PING_INTERVAL_MS); // +10 seconds each time

      handler.postDelayed(this::connectToWebSocket, currentPingIntervalMs);

      Timber.w( "Reconnecting");
   }

   @Override
   public void onDestroy() {
      super.onDestroy();
      if (webSocket != null) {
         webSocket.close(1000, null);
      }

      if (pingHandler != null && pingRunnable != null) {
         pingHandler.removeCallbacks(pingRunnable);
      }

      if (pongTimeoutHandler != null && pongTimeoutRunnable != null) {
         pongTimeoutHandler.removeCallbacks(pongTimeoutRunnable);
      }
   }

   private void handlePong() {
      if (pongTimeoutHandler != null && pongTimeoutRunnable != null) {
         pongTimeoutHandler.removeCallbacks(pongTimeoutRunnable);
         Timber.i("Pong received, timeout cleared");
      }
   }

   private void startPongTimeout() {
      if (pongTimeoutHandler != null && pongTimeoutRunnable != null) {
         pongTimeoutHandler.removeCallbacks(pongTimeoutRunnable);
      }

      pongTimeoutHandler = new Handler(Looper.getMainLooper());
      pongTimeoutRunnable = new Runnable() {
         @Override
         public void run() {
            // Pong was not received in time
            Timber.w("Pong timeout, reconnecting...");
            isConnected = false;
            reconnectToWebSocket();
         }
      };
      pongTimeoutHandler.postDelayed(pongTimeoutRunnable, PONG_TIMEOUT_MS);
   }
   private void handleMessage(JSONObject jsonObject) throws JSONException {
      Log.e("WebSocket", "Message received: " + jsonObject);

      Long senderId = jsonObject.has("senderId")
         ? jsonObject.getLong("senderId")
         : null;
      Long conversationId = jsonObject.has("conversationId")
         ? jsonObject.getLong("conversationId")
         : null;
      String uuid = jsonObject.has("uuid")
         ? jsonObject.getString("uuid")
         : null;
      Long timestamp = jsonObject.has("timestamp")
         ? jsonObject.getLong("timestamp")
         : null;
      String contentEncrypted = jsonObject.has("contentEncrypted")
         ? jsonObject.getString("contentEncrypted")
         : null;
      MessageDatabaseUtil messageDatabaseUtil = new MessageDatabaseUtil(
         context,
         currentUser
      );
      MessageEntry messageEntry = new MessageEntry(
         conversationId,
         senderId,
         timestamp,
         contentEncrypted,
         MessageTypeConstants.MESSAGE,
         uuid
      );


      //TODO encryption
      messageEntry.setContent(contentEncrypted);


      messageDatabaseUtil.insertMessageEntry(messageEntry);
      if(isAppInForeground()) {
         sendMessageBroadcast(messageEntry);
         Log.e(AppConstants.LOG_TAG, messageEntry.toString());

      }else {
         showIncomingMessageNotification(messageEntry.getContent());
      }
      if (messageEntry != null) {
         String message =
            "{\"type\": " +
            MessageTypeConstants.ARRIVAL_CONFIRMATION +
            ", \"uuid\": \"" +
            uuid +
            "\", \"userId\": \"" +
            currentUser.getUserId() +
            "\"}";
         Log.e(AppConstants.LOG_TAG, message);
         sendMessage(message);
      }
   }

   private void handleNotification(String notification) {
      Log.e("WebSocket", "Notification received: " + notification);
      // Process the notification
   }

   private void sendMessageBroadcast(MessageEntry message) {
      Intent intent = new Intent(
         "com.example.szakdolg.models.message.entity.MessageBroadCast"
      );
      intent.putExtra("message", message);
      LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
   }
   private boolean isAppInForeground() {
      ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
      List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
      if (appProcesses == null) {
         return false;
      }
      final String packageName = getPackageName();
      for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
         if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                 appProcess.processName.equals(packageName)) {
            return true;
         }
      }
      return false;
   }

   private void showIncomingMessageNotification(String messageText) {
      NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "MESSAGE_CHANNEL")
              .setSmallIcon(R.drawable.ic_chat)
              .setContentTitle("New Message")
              .setContentText(messageText)
              .setPriority(NotificationCompat.PRIORITY_HIGH)
              .setAutoCancel(true);

      NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
      notificationManager.notify(2, builder.build());
   }


}
