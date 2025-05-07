package com.zen_vy.chat.signaling;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import com.zen_vy.chat.constans.AppConstants;
import org.json.JSONObject;

public class SignalServiceManager {

   private static final String TAG = "SignalServiceManager";
   private static SignalServiceManager instance;

   private SignalService signalService;
   private boolean isBound = false;
   private Context context;
   private ServiceConnectionListener serviceConnectionListener;

   // Interface to notify when the service is connected or failed to connect
   public interface ServiceConnectionListener {
      void onServiceConnected();
      void onServiceConnectionFailed(String error);
   }

   private SignalServiceManager(Context context) {
      this.context = context.getApplicationContext();
      bindToService();
   }

   public static synchronized SignalServiceManager getInstance(
      Context context
   ) {
      if (instance == null) {
         instance = new SignalServiceManager(context);
      }
      return instance;
   }

   private final ServiceConnection connection = new ServiceConnection() {
      @Override
      public void onServiceConnected(ComponentName name, IBinder service) {
         SignalService.LocalBinder binder = (SignalService.LocalBinder) service;
         signalService = binder.getService();
         isBound = true;
         Log.d(TAG, "SignalService connected");

         if (serviceConnectionListener != null) {
            serviceConnectionListener.onServiceConnected();
         }
      }

      @Override
      public void onServiceDisconnected(ComponentName name) {
         isBound = false;
         signalService = null;
         Log.d(TAG, "SignalService disconnected");

         if (serviceConnectionListener != null) {
            serviceConnectionListener.onServiceConnectionFailed(
               "Service disconnected unexpectedly"
            );
         }
      }
   };

   private void bindToService() {
      Intent serviceIntent = new Intent(context, SignalService.class);
      context.bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
   }

   public void unbind() {
      if (isBound) {
         context.unbindService(connection);
         isBound = false;
         Log.d(TAG, "SignalService unbound");
      }
   }

   public boolean isServiceBound() {
      return isBound;
   }

   public void setServiceConnectionListener(
      ServiceConnectionListener listener
   ) {
      this.serviceConnectionListener = listener;
   }

   public void connect(String serverUri, String userId) {
      if (signalService != null) {
         Log.e(
            AppConstants.LOG_TAG,
            "SignalService is not bound yet. Cannot connect."
         );
         signalService.connect(serverUri, userId);
      } else {
         Log.e(
            AppConstants.LOG_TAG,
            "SignalService is not bound yet. Cannot connect."
         );
      }
   }

   public void sendMessage(JSONObject message) {
      if (signalService != null) {
         signalService.sendMessage(message);
      } else {
         Log.e(
            AppConstants.LOG_TAG,
            "SignalService is not bound yet. Cannot send message."
         );
      }
   }

   public void setSignalListener(SignalService.SignalListener listener) {
      if (signalService != null) {
         signalService.setSignalListener(listener);
      } else {
         Log.e(
            AppConstants.LOG_TAG,
            "SignalService is not bound yet. Cannot set listener."
         );
      }
   }

   public void disconnect() {
      if (signalService != null) {
         signalService.disconnect();
      } else {
         Log.e(TAG, "SignalService is not bound yet. Cannot disconnect.");
      }
   }
}
