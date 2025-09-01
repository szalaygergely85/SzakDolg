package com.zen_vy.chat.websocket;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.zen_vy.chat.constans.AppConstants;
import com.zen_vy.chat.constans.IntentConstants;
import com.zen_vy.chat.models.message.MessageDatabaseUtil;
import com.zen_vy.chat.models.message.constants.MessageTypeConstants;
import com.zen_vy.chat.models.message.entity.MessageEntry;
import com.zen_vy.chat.models.user.entity.User;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.json.JSONException;
import org.json.JSONObject;
import timber.log.Timber;

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

   private boolean isConnected = false;

   private Handler pingHandler;
   private Runnable pingRunnable;

   private boolean isConnecting = false;

   private static final int PONG_TIMEOUT_MS = 40000;

   private long currentPingIntervalMs = 30000; // Start with 30 seconds
   private static final long MAX_PING_INTERVAL_MS = 120000;

   private static boolean isRunning = false;

   public static boolean isServiceRunning() {
      return isRunning;
   }

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
      isRunning = true;
      Timber.i("Starting WebSocketService");
   }

   @Override
   public IBinder onBind(Intent intent) {
      return null;
   }

   @Override
   public int onStartCommand(Intent intent, int flags, int startId) {
      userToken = intent.getStringExtra(IntentConstants.USER_TOKEN);
      currentUser =
      (User) intent.getSerializableExtra(IntentConstants.CURRENT_USER);

      connectToWebSocket();

      return START_STICKY;
   }

   public void sendMessage(String message) {
      webSocket.send(message);
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
      try {
         webSocket =
         client.newWebSocket(
            request,
            new WebSocketListener() {
               @Override
               public void onOpen(WebSocket webSocket, Response response) {
                  Timber.i(response.message());
                  isConnected = true;
                  isConnecting = false;
                  startPingPong();
               }

               @Override
               public void onMessage(WebSocket webSocket, String text) {
                  try {
                     Timber.i(text);
                     JSONObject jsonObject = new JSONObject(text);

                     int type = jsonObject.getInt("type");

                     switch (type) {
                        case MessageTypeConstants.READ_CONFIRMATION:
                        case MessageTypeConstants.ARRIVAL_CONFIRMATION:
                           handleStatus(jsonObject);
                           break;
                        case MessageTypeConstants.ERROR:
                           handleError(jsonObject);
                           break;
                        case MessageTypeConstants.PING:
                           handlePong();
                           break;
                        case MessageTypeConstants.MESSAGE:
                        case MessageTypeConstants.IMAGE:
                           handleMessage(jsonObject);
                           break;
                        // Add more cases for other message types as needed
                        default:
                           Timber.e("Unknown message type: %s", type);
                           break;
                     }
                  } catch (JSONException e) {
                     Timber.e("Error parsing message: %s", e.getMessage());
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
               public void onClosed(
                  WebSocket webSocket,
                  int code,
                  String reason
               ) {
                  isConnected = false;
                  isConnecting = false;
                  // Connection closed
                  reconnectToWebSocket();
               }
            }
         );
      } catch (RuntimeException e) {
         throw new RuntimeException(e);
      }
   }

   private void handleStatus(JSONObject jsonObject) throws JSONException {
      Timber.i(jsonObject.getString("type"));
   }

   public boolean isConnected() {
      return isConnected;
   }

   private void startPingPong() {
      if (pingHandler != null && pingRunnable != null) {
         pingHandler.removeCallbacks(pingRunnable);
      }

      pingHandler = new Handler(Looper.getMainLooper());
      pingRunnable =
      new Runnable() {
         @Override
         public void run() {
            if (webSocket != null && isConnected) {
               String pingMessage = MessageFactory.pingMessage(
                  null,
                  currentUser.getUserId()
               );

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

      currentPingIntervalMs =
      Math.min(currentPingIntervalMs + 10000, MAX_PING_INTERVAL_MS); // +10 seconds each time

      handler.postDelayed(this::connectToWebSocket, currentPingIntervalMs);

      Timber.w("Reconnecting");
   }

   @Override
   public void onDestroy() {
      super.onDestroy();

      isRunning = false;

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
      pongTimeoutRunnable =
      new Runnable() {
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

   private void handleError(JSONObject jsonObject) throws JSONException {
      int errorType = jsonObject.has("error_type")
         ? jsonObject.getInt("error_type")
         : -1;

      switch (errorType) {
         //TODO finish error handling
         case MessageTypeConstants.ERROR_MISSING_AUTH_TOKEN:
            Timber.e("AUTH_TOKEN Not found on WebSocket Server");
            break;
         case MessageTypeConstants.ERROR_USER_NOT_FOUND:
            Timber.e("User Not found on WebSocket Server");
            break;
         default:
            Timber.e("Unknown message type: %s", errorType);
            break;
      }
   }

   private void handleMessage(JSONObject jsonObject) throws JSONException {
      Timber.i("Message received: %s", jsonObject);

      Long senderId = jsonObject.has("userId")
         ? jsonObject.getLong("userId")
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

      int mType = jsonObject.has("type") ? jsonObject.getInt("type") : 0;
      String content = jsonObject.has("content")
         ? jsonObject.getString("content")
         : null;

      boolean encrypted =
         jsonObject.has("encrypted") && jsonObject.getBoolean("encrypted");

      MessageDatabaseUtil messageDatabaseUtil = new MessageDatabaseUtil(
         context,
         currentUser
      );
      MessageEntry messageEntry = new MessageEntry(
         conversationId,
         senderId,
         timestamp,
         content,
         encrypted,
         mType,
         uuid
      );

      //TODO encryption
      messageEntry.setContent(content);

      messageDatabaseUtil.insertMessageEntry(messageEntry);
      if (isAppInForeground()) {
         sendMessageBroadcast(messageEntry);
         Timber.e(messageEntry.toString());
      }
      String message = MessageFactory.arrivalConfirmation(
         uuid,
         currentUser.getUserId()
      );
      Timber.e(message);
      sendMessage(message);
   }

   public void sendMessageBroadcast(MessageEntry message) {
      Intent intent = new Intent(
         "com.example.szakdolg.models.message.entity.MessageBroadCast"
      );
      intent.putExtra("message", message);
      LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
   }

   /*
private void showIncomingMessageNotification(
	String messageText,
	long conversationId
) {
	ConversationService conversationService = new ConversationService(
		context,
		currentUser
	);
	conversationService.getConversation(
		conversationId,
		new ConversationService.ConversationCallback<ConversationDTO>() {
			@Override
			public void onSuccess(ConversationDTO data) {
			// 1. Create the intent to open ChatActivity
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

			NotificationCompat.Builder builder =
				new NotificationCompat.Builder(context, "MESSAGE_CHANNEL")
					.setSmallIcon(R.drawable.ic_chat)
					.setContentTitle("New Message")
					.setContentText(messageText)
					.setPriority(NotificationCompat.PRIORITY_HIGH)
					.setAutoCancel(true)
					.setContentIntent(pendingIntent);

			NotificationManager notificationManager =
				(NotificationManager) getSystemService(
					Context.NOTIFICATION_SERVICE
				);
			notificationManager.notify(2, builder.build());
			}

			@Override
			public void onError(Throwable t) {}
		}
	);
}*/

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
