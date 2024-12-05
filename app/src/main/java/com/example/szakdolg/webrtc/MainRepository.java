package com.example.szakdolg.webrtc;

import android.content.Context;
import android.util.Log;
import com.example.szakdolg.constans.AppConstants;
import com.example.szakdolg.retrofit.SuccessCallBack;
import com.example.szakdolg.signaling.SignalService;
import com.example.szakdolg.signaling.SignalServiceManager;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceViewRenderer;

public class MainRepository implements WebRTCClient.Listener {

   private static final String TAG = "MainRepository";
   public Listener listener;
   private final Gson gson = new Gson();
   //private final FirebaseClient firebaseClient;

   private WebRTCClient webRTCClient;

   private String currentUsername;

   private SignalServiceManager signalServiceManager;

   private SurfaceViewRenderer remoteView;

   String target;

   private void updateCurrentUsername(String username) {
      this.currentUsername = username;
   }

   private MainRepository() {}

   private static MainRepository instance;

   public static MainRepository getInstance() {
      if (instance == null) {
         instance = new MainRepository();
      }
      return instance;
   }

   public void login(
      String username,
      Context context,
      SuccessCallBack callBack
   ) {
      signalServiceManager = SignalServiceManager.getInstance(context);

      updateCurrentUsername(username);
      this.webRTCClient =
      new WebRTCClient(
         context,
         new MyPeerConnectionObserver() {
            @Override
            public void onAddStream(MediaStream mediaStream) {
               super.onAddStream(mediaStream);
               try {
                  mediaStream.videoTracks.get(0).addSink(remoteView);
                  Log.e(AppConstants.LOG_TAG, "onAddStream started");
               } catch (Exception e) {
                  e.printStackTrace();
               }
            }

            @Override
            public void onConnectionChange(
               PeerConnection.PeerConnectionState newState
            ) {
               Log.d(AppConstants.LOG_TAG, "onConnectionChange: " + newState);
               super.onConnectionChange(newState);
               if (
                  newState == PeerConnection.PeerConnectionState.CONNECTED &&
                  listener != null
               ) {
                  listener.webrtcConnected();
               }

               if (
                  newState == PeerConnection.PeerConnectionState.CLOSED ||
                  newState == PeerConnection.PeerConnectionState.DISCONNECTED
               ) {
                  if (listener != null) {
                     listener.webrtcClosed();
                  }
               }
            }

            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
               super.onIceCandidate(iceCandidate);
               Log.e(AppConstants.LOG_TAG, "onIceCandidate started");
               webRTCClient.sendIceCandidate(iceCandidate, target);
            }
         },
         username
      );
      webRTCClient.listener = this;
      callBack.onSuccess();
   }

   public void initLocalView(SurfaceViewRenderer view) {
      webRTCClient.initLocalSurfaceView(view);
   }

   public void initRemoteView(SurfaceViewRenderer view) {
      webRTCClient.initRemoteSurfaceView(view);
      this.remoteView = view;
   }

   public void startCall(String target) {
      webRTCClient.call(target);
   }

   public void switchCamera() {
      webRTCClient.switchCamera();
   }

   public void toggleAudio(Boolean shouldBeMuted) {
      webRTCClient.toggleAudio(shouldBeMuted);
   }

   public void toggleVideo(Boolean shouldBeMuted) {
      webRTCClient.toggleVideo(shouldBeMuted);
   }

   public void sendCallRequest(String target) {
      Log.e(AppConstants.LOG_TAG, "sendCallRequest");
      signalServiceManager.sendMessage(
         new DataModel(target, currentUsername, null, DataModelType.StartCall)
            .getJSON()
      );
   }

   public void endCall() {
      webRTCClient.closeConnection();
   }

   public void subscribeForLatestEvent(NewEventCallBack callBack) {
      signalServiceManager.setServiceConnectionListener(
         new SignalServiceManager.ServiceConnectionListener() {
            @Override
            public void onServiceConnected() {
               signalServiceManager.setSignalListener(
                  new SignalService.SignalListener() {
                     @Override
                     public void onMessageReceived(String message) {
                        try {
                           JSONObject messageJson = new JSONObject(message);
                           DataModel model = DataModel.fromJSON(messageJson);
                           Log.e(AppConstants.LOG_TAG, model.toString());
                           switch (model.getType()) {
                              case offer:
                                 target = model.getSender();
                                 webRTCClient.onRemoteSessionReceived(
                                    new SessionDescription(
                                       SessionDescription.Type.OFFER,
                                       model.getData()
                                    )
                                 );
                                 webRTCClient.answer(model.getSender());
                                 break;
                              case answer:
                                 target = model.getSender();
                                 webRTCClient.onRemoteSessionReceived(
                                    new SessionDescription(
                                       SessionDescription.Type.ANSWER,
                                       model.getData()
                                    )
                                 );
                                 break;
                              case candidate:
                                 try {
                                    IceCandidate candidate = gson.fromJson(
                                       model.getData(),
                                       IceCandidate.class
                                    );
                                    webRTCClient.addIceCandidate(candidate);
                                 } catch (Exception e) {
                                    e.printStackTrace();
                                 }
                                 break;
                              case StartCall:
                                 target = model.getSender();
                                 callBack.onNewEventReceived(model);
                                 break;
                           }
                        } catch (Exception e) {
                           Log.e(
                              TAG,
                              "Error parsing incoming message: " +
                              e.getMessage()
                           );
                        }
                        Log.e(TAG, "Message received: " + message);
                     }

                     @Override
                     public void onError(String error) {
                        Log.e(TAG, "Error: " + error);
                     }

                     @Override
                     public void onDisconnected() {
                        Log.e(TAG, "Disconnected from WebSocket.");
                     }
                  }
               );

               // Now you can connect
               signalServiceManager.connect(
                  AppConstants.WS_URL,
                  currentUsername
               ); // Replace with actual user ID if needed
            }

            @Override
            public void onServiceConnectionFailed(String error) {
               Log.e(TAG, "Failed to bind to SignalService: " + error);
            }
         }
      );
   }

   @Override
   public void onTransferDataToOtherPeer(DataModel model) {
      signalServiceManager.sendMessage(model.getJSON());
   }

   public interface Listener {
      void webrtcConnected();
      void webrtcClosed();
   }
}
