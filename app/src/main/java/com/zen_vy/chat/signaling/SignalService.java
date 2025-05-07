package com.zen_vy.chat.signaling;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import java.net.URI;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

public class SignalService extends Service {

   private static final String TAG = "SignalService";
   private WebSocketClient webSocketClient;
   private final IBinder binder = new LocalBinder();
   private SignalListener listener;

   // Callback interface for WebSocket events
   public interface SignalListener {
      void onMessageReceived(String message);
      void onError(String error);
      void onDisconnected();
   }

   // Binder for clients to bind to the service
   public class LocalBinder extends Binder {

      public SignalService getService() {
         return SignalService.this;
      }
   }

   @Override
   public IBinder onBind(Intent intent) {
      return binder;
   }

   // Set the listener for WebSocket events
   public void setSignalListener(SignalListener listener) {
      this.listener = listener;
   }

   // Connect to the WebSocket server
   public void connect(String serverUri, String userId) {
      if (webSocketClient != null && webSocketClient.isOpen()) {
         Log.w(TAG, "Already connected to WebSocket.");
         return;
      }

      try {
         URI uri = new URI(serverUri);
         webSocketClient = createWebSocketClient(uri, userId);
         webSocketClient.connect();
      } catch (Exception e) {
         Log.e(TAG, "Error initializing WebSocket connection", e);
         notifyError(e.getMessage());
      }
   }

   // Create the WebSocket client with proper event handling
   private WebSocketClient createWebSocketClient(URI uri, final String userId) {
      return new WebSocketClient(uri) {
         @Override
         public void onOpen(ServerHandshake handshake) {
            Log.d(TAG, "WebSocket connected");
            registerUser(userId);
         }

         @Override
         public void onMessage(String message) {
            if (listener != null) {
               listener.onMessageReceived(message);
            }
         }

         @Override
         public void onClose(int code, String reason, boolean remote) {
            Log.d(TAG, "WebSocket closed: " + reason);
            notifyDisconnected();
         }

         @Override
         public void onError(Exception ex) {
            Log.e(TAG, "WebSocket error", ex);
            notifyError(ex.getMessage());
         }
      };
   }

   private void registerUser(String userId) {
      try {
         JSONObject registerMessage = new JSONObject();
         registerMessage.put("type", "register");
         registerMessage.put("userId", userId);
         sendMessage(registerMessage);
      } catch (Exception e) {
         Log.e(TAG, "Error registering user", e);
         notifyError("Failed to register user.");
      }
   }

   // Send a message through the WebSocket
   public void sendMessage(JSONObject message) {
      if (webSocketClient != null && webSocketClient.isOpen()) {
         webSocketClient.send(message.toString());
      } else {
         Log.e(TAG, "WebSocket is not open. Cannot send message.");
         notifyError("WebSocket is not open.");
      }
   }

   public void disconnect() {
      if (webSocketClient != null) {
         webSocketClient.close();
         webSocketClient = null;
      }
   }

   private void notifyError(String error) {
      if (listener != null) {
         listener.onError(error);
      }
   }

   private void notifyDisconnected() {
      if (listener != null) {
         listener.onDisconnected();
      }
   }
}
