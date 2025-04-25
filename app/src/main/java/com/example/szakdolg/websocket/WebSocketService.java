package com.example.szakdolg.websocket;

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
import com.example.szakdolg.R;
import com.example.szakdolg.constans.AppConstants;
import com.example.szakdolg.constans.IntentConstants;
import com.example.szakdolg.models.message.MessageDatabaseUtil;
import com.example.szakdolg.models.message.constants.MessageTypeConstants;
import com.example.szakdolg.models.message.entity.MessageEntry;
import com.example.szakdolg.models.user.entity.User;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.json.JSONException;
import org.json.JSONObject;

public class WebSocketService extends Service {

   private final IBinder binder = new LocalBinder();
   private OkHttpClient client;
   private WebSocket webSocket;

   private String userToken;

   private User currentUser;

   private Context context;

   private static WebSocketService instance;

    private boolean isConnected= false;

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
   }

   @Override
   public IBinder onBind(Intent intent) {
      return binder;
   }

   @Override
   public int onStartCommand(Intent intent, int flags, int startId) {
      userToken = intent.getStringExtra(IntentConstants.USER_TOKEN);
      currentUser =
      (User) intent.getSerializableExtra(IntentConstants.CURRENT_USER);

      startForegroundNotification();
      connectToWebSocket();
      return START_STICKY;
   }

   public void sendMessage(String message) {
      webSocket.send(message);
   }

   private void startForegroundNotification() {
      createNotificationChannel();
      Notification notification = new NotificationCompat.Builder(
         this,
         "CHANNEL_ID"
      )
         .setContentTitle("WebSocket Service")
         .setContentText("Maintaining WebSocket connection")
         .setSmallIcon(R.drawable.ic_chat)
         .build();
      startForeground(1, notification);
   }

   private void createNotificationChannel() {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
         NotificationChannel channel = new NotificationChannel(
            "CHANNEL_ID",
            "WebSocket Service",
            NotificationManager.IMPORTANCE_LOW
         );
         channel.setDescription("Notification for WebSocket Service");
         NotificationManager manager = getSystemService(
            NotificationManager.class
         );
         if (manager != null) {
            manager.createNotificationChannel(channel);
         }
      }
   }

   private void connectToWebSocket() {
      Log.e("WebSocket", "response.message()");
      client = new OkHttpClient();

      Request request = new Request.Builder()
         .url("ws://10.0.2.2:8081/ws")
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
               reconnectToWebSocket();
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {

                isConnected = false;
               // Connection closed
               reconnectToWebSocket();
            }
         }
      );
       startPingPong();
   }

    public boolean isConnected() {
        return isConnected;
    }

    private void startPingPong() {
      Handler handler = new Handler(Looper.getMainLooper());
      Runnable pingRunnable = new Runnable() {
         @Override
         public void run() {
            if (webSocket != null) {
               String pingMessage =
                  "{\"type\": " + MessageTypeConstants.PING + "}";
               webSocket.send(pingMessage);
               Log.e("WebSocket", "Sending ping message");
               handler.postDelayed(this, 30000); // Send ping every 30 seconds
            }
         }
      };
      handler.post(pingRunnable);
   }

   private void reconnectToWebSocket() {
      Handler handler = new Handler(Looper.getMainLooper());
      handler.postDelayed(this::connectToWebSocket, 10000); // Retry after 5 seconds

      Log.e("WebSocket", "Reconnecting");
   }

   @Override
   public void onDestroy() {
      super.onDestroy();
      if (webSocket != null) {
         webSocket.close(1000, null);
      }
   }

   private void handlePong() {
      Log.e("WebSocket", "Pong received from server");
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
      sendMessageBroadcast(messageEntry);
      Log.e(AppConstants.LOG_TAG, messageEntry.toString());

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
}
