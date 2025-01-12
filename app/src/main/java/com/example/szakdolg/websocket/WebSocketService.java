package com.example.szakdolg.websocket;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;


import androidx.core.app.NotificationCompat;

import com.example.szakdolg.R;
import com.example.szakdolg.models.message.constans.MessageTypeConstants;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketService extends Service {

    private final IBinder binder = new LocalBinder();
    private OkHttpClient client;
    private WebSocket webSocket;

    private static WebSocketService instance;

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
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForegroundNotification();
        connectToWebSocket();
        return START_STICKY;
    }

    public void sendMessage(String message){
        webSocket.send(message);
    }

    private void startForegroundNotification() {
        createNotificationChannel();
        Notification notification = new NotificationCompat.Builder(this, "CHANNEL_ID")
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
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void connectToWebSocket() {
        Log.e("test", "response.message()");
        client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("ws://10.0.2.2:8081/ws")
                .addHeader("token", "test")
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.e("test", response.message());
                // Connection established
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                try {
                    JSONObject jsonObject = new JSONObject(text);
                    String type = jsonObject.getString("type");

                    switch (type) {
                        case "pong":
                            handlePong();
                            break;

                        case "message":
                            String content = jsonObject.getString("content");
                            handleMessage(content);
                            break;

                        case "notification":
                            String notification = jsonObject.getString("notification");
                            handleNotification(notification);
                            break;

                        // Add more cases for other message types as needed
                        default:
                            Log.e("WebSocket", "Unknown message type: " + type);
                            break;
                    }
                } catch (JSONException e) {
                    Log.e("WebSocket", "Error parsing message: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                // Connection failure, handle reconnection
                Log.e("test", t.getMessage());
                reconnectToWebSocket();
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                // Connection closed
                reconnectToWebSocket();
            }
        });

    //    startPingPong();
    }

    private void startPingPong() {
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable pingRunnable = new Runnable() {
            @Override
            public void run() {
                if (webSocket != null) {
                    String pingMessage = "{\"type\": "+ MessageTypeConstants.PING +"}" ;
                    webSocket.send(pingMessage);
                    Log.e("WebSocket", "Sending ping message");
                    handler.postDelayed(this, 30000);  // Send ping every 30 seconds
                }
            }
        };
        handler.post(pingRunnable);
    }

    private void reconnectToWebSocket() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(this::connectToWebSocket, 5000); // Retry after 5 seconds

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

    private void handleMessage(String content) {
        Log.e("WebSocket", "Message received: " + content);
        // Process the message content
    }

    private void handleNotification(String notification) {
        Log.e("WebSocket", "Notification received: " + notification);
        // Process the notification
    }
}
