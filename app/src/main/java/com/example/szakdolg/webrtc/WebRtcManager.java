package com.example.szakdolg.webrtc;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import com.example.szakdolg.R;
import com.example.szakdolg.signaling.SignalServiceManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.json.JSONObject;
import org.webrtc.*;
import org.webrtc.SdpObserver;

public class WebRtcManager {

   private static final String TAG = "WebRtcManager";
   private PeerConnectionFactory peerConnectionFactory;
   private PeerConnection peerConnection;
   private EglBase eglBase;
   private MediaStream localStream;
   private VideoTrack localVideoTrack;
   private AudioTrack localAudioTrack;
   private final Context context;
   private SurfaceViewRenderer remoteVideoRenderer;
   private SurfaceViewRenderer localVideoRenderer;
   private String username;
   private String remoteUser;

   private VideoTrack remoteVideoTrack;

   public WebRtcManager(Context context, String username, String remoteUser) {
      this.context = context;
      this.username = username;
      this.remoteUser = remoteUser;

      eglBase = EglBase.create();
      initializePeerConnectionFactory();
   }

   // Initialize the PeerConnectionFactory
   private void initializePeerConnectionFactory() {
      // Initialize PeerConnectionFactory globals (this needs to be done once)
      PeerConnectionFactory.initialize(
         PeerConnectionFactory.InitializationOptions
            .builder(context)
            .setEnableInternalTracer(true) // Enable tracing if needed
            .createInitializationOptions()
      );

      // Enable hardware encoding for video if possible (H264)
      VideoEncoderFactory encoderFactory;
      VideoDecoderFactory decoderFactory;

      Log.d(TAG, "EglBase initialized: " + (eglBase != null));
      Log.d(
         TAG,
         "PeerConnectionFactory initialized: " + (peerConnectionFactory != null)
      );

      encoderFactory =
      new DefaultVideoEncoderFactory(eglBase.getEglBaseContext(), true, true);
      decoderFactory =
      new DefaultVideoDecoderFactory(eglBase.getEglBaseContext());

      // Create the PeerConnectionFactory with the specified encoder and decoder
      peerConnectionFactory =
      PeerConnectionFactory
         .builder()
         .setVideoEncoderFactory(encoderFactory)
         .setVideoDecoderFactory(decoderFactory)
         .createPeerConnectionFactory();

      // Create local MediaStream (Video and Audio tracks)
      localStream = createLocalMediaStream();
   }

   // Create a local MediaStream (containing video and audio)
   private MediaStream createLocalMediaStream() {
      MediaStream mediaStream = peerConnectionFactory.createLocalMediaStream(
         "localStream"
      );

      // Create video track from camera or surface
      VideoCapturer videoCapturer = createCameraCapturer();
      Log.d(TAG, "Camera Capturer created: " + (videoCapturer != null));
      VideoSource videoSource = peerConnectionFactory.createVideoSource(
         videoCapturer.isScreencast()
      );
      localVideoTrack =
      peerConnectionFactory.createVideoTrack("videoTrack", videoSource);
      if (localVideoTrack != null) {
         Log.d(
            "WebRtc",
            "Local video track state: " + localVideoTrack.enabled()
         );
      }
      mediaStream.addTrack(localVideoTrack);

      // Create audio track
      AudioSource audioSource = peerConnectionFactory.createAudioSource(
         new MediaConstraints()
      );
      localAudioTrack =
      peerConnectionFactory.createAudioTrack("audioTrack", audioSource);
      mediaStream.addTrack(localAudioTrack);

      return mediaStream;
   }

   private VideoCapturer createCameraCapturer() {
      // Create a Camera2Enumerator to enumerate the cameras
      Camera2Enumerator enumerator = new Camera2Enumerator(context);

      // Get all available device names (cameras)
      String[] deviceNames = enumerator.getDeviceNames();
      if (deviceNames.length == 0) {
         Log.e(TAG, "No cameras found.");
         return null; // Handle the case where no camera is found
      }

      // Optionally select the front or rear camera based on index or criteria
      String deviceName = null;
      for (String name : deviceNames) {
         if (enumerator.isFrontFacing(name)) {
            deviceName = name; // Select front-facing camera
            break;
         }
      }

      // If no front-facing camera is found, fall back to the first available camera
      if (deviceName == null) {
         deviceName = deviceNames[0]; // Fallback to the first camera
      }

      // Create a CameraEventsHandler to handle camera events (e.g., opening, errors)
      CameraVideoCapturer.CameraEventsHandler cameraEventsHandler =
         new CameraVideoCapturer.CameraEventsHandler() {
            @Override
            public void onCameraError(String errorDescription) {
               Log.e(TAG, "Camera error: " + errorDescription);
            }

            @Override
            public void onCameraDisconnected() {
               Log.d(TAG, "Camera disconnected");
            }

            @Override
            public void onCameraFreezed(String errorDescription) {
               Log.e(TAG, "Camera freezed: " + errorDescription);
            }

            @Override
            public void onCameraOpening(String cameraName) {
               Log.d(TAG, "Opening camera: " + cameraName);
            }

            @Override
            public void onFirstFrameAvailable() {
               Log.d(TAG, "First frame available");
            }

            @Override
            public void onCameraClosed() {
               Log.d(TAG, "onCameraClosed");
            }
         };

      // Create the video capturer with the camera name and events handler
      VideoCapturer capturer = enumerator.createCapturer(
         deviceName,
         cameraEventsHandler
      );

      if (capturer == null) {
         Log.e(
            TAG,
            "Failed to create video capturer for device: " + deviceName
         );
      } else {
         Log.d(
            TAG,
            "Successfully created video capturer for device: " + deviceName
         );
      }

      return capturer;
   }

   public void createOffer(SdpObserver sdpObserver) {
      List<PeerConnection.IceServer> iceServers = Arrays.asList(
         // STUN server
         PeerConnection.IceServer
            .builder("stun:stun.l.google.com:19302")
            .createIceServer(),
         // TURN server
         PeerConnection.IceServer
            .builder("turn:global.relay.metered.ca:443")
            .setUsername("0dbe6dd5853e81d310cbff23") // Replace with your TURN username
            .setPassword("zGpnunoUAmn6kpDx") // Replace with your TURN password
            .createIceServer()
      );

      PeerConnection.RTCConfiguration rtcConfig =
         new PeerConnection.RTCConfiguration(iceServers);
      peerConnection =
      peerConnectionFactory.createPeerConnection(
         rtcConfig,
         new PeerConnectionObserver()
      );

      if (peerConnection != null) {
         peerConnection.addTrack(localVideoTrack);
         peerConnection.addTrack(localAudioTrack); // Make sure the local stream is added

         MediaConstraints constraints = new MediaConstraints();
         constraints.mandatory.add(
            new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true")
         );
         constraints.mandatory.add(
            new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true")
         );

         peerConnection.createOffer(
            new SdpObserver() {
               @Override
               public void onCreateSuccess(SessionDescription sdp) {
                  Log.d(TAG, "WebRTC Offer created successfully: " + sdp); // Debugging
                  peerConnection.setLocalDescription(this, sdp);
                  sdpObserver.onCreateSuccess(sdp); // Pass the SDP to the callback
               }

               @Override
               public void onSetSuccess() {
                  Log.d(TAG, "Local SDP set successfully.");
               }

               @Override
               public void onCreateFailure(String error) {
                  Log.e(TAG, "Failed to create SDP: " + error);
               }

               @Override
               public void onSetFailure(String error) {
                  Log.e(TAG, "Failed to set SDP: " + error);
               }
            },
            constraints
         );
      } else {
         Log.e(TAG, "PeerConnection is null. Cannot create offer.");
         Log.e(TAG, "PeerConnection is null. Cannot create offer.");
      }
   }

   // Handle WebRTC offer
   public void handleOffer(JSONObject offerMessage) {
      Log.d(TAG, "Handling offer: " + offerMessage.toString());
      try {
         // Create peer connection and set local stream
         PeerConnection.RTCConfiguration rtcConfig =
            new PeerConnection.RTCConfiguration(new ArrayList<>());
         peerConnection =
         peerConnectionFactory.createPeerConnection(
            rtcConfig,
            new PeerConnectionObserver()
         );
         peerConnection.addStream(localStream); // Add local stream

         // Process the offer SDP
         SessionDescription offerSdp = new SessionDescription(
            SessionDescription.Type.OFFER,
            offerMessage.getString("sdp")
         );
         peerConnection.setRemoteDescription(
            new MySdpObserver() {
               @Override
               public void onSetSuccess() {
                  Log.d(TAG, "Remote description set successfully.");

                  // Create an answer
                  MediaConstraints constraints = new MediaConstraints();
                  peerConnection.createAnswer(
                     new MySdpObserver() {
                        @Override
                        public void onCreateSuccess(
                           SessionDescription answerSdp
                        ) {
                           Log.d(
                              TAG,
                              "Answer SDP created successfully: " + answerSdp
                           );

                           // Set local description with the answer
                           peerConnection.setLocalDescription(
                              new MySdpObserver(),
                              answerSdp
                           );

                           // Send the answer SDP to the remote peer through signaling
                           sendAnswerToRemotePeer(
                              answerSdp,
                              remoteUser,
                              username
                           );
                        }

                        @Override
                        public void onSetFailure(String error) {
                           Log.e(
                              TAG,
                              "Failed to set local description: " + error
                           );
                        }
                     },
                     constraints
                  );
               }

               @Override
               public void onSetFailure(String error) {
                  Log.e(TAG, "Failed to set remote description: " + error);
               }
            },
            offerSdp
         );
      } catch (Exception e) {
         Log.e(TAG, "Error handling offer", e);
      }
   }

   // Handle WebRTC answer
   public void handleAnswer(JSONObject answerMessage) {
      Log.d(TAG, "Handling answer: " + answerMessage.toString());
      try {
         // Set the remote description with the answer SDP
         SessionDescription answerSdp = new SessionDescription(
            SessionDescription.Type.ANSWER,
            answerMessage.getString("sdp")
         );
         peerConnection.setRemoteDescription(new MySdpObserver(), answerSdp);
      } catch (Exception e) {
         Log.e(TAG, "Error handling answer", e);
      }
   }

   // Handle ICE candidate
   public void handleCandidate(JSONObject candidateMessage) {
      Log.d(TAG, "Handling candidate: " + candidateMessage.toString());
      try {
         // Extract the ICE candidate from the message
         String candidate = candidateMessage.getString("candidate");
         IceCandidate iceCandidate = new IceCandidate(
            candidateMessage.getString("sdpMid"),
            candidateMessage.getInt("sdpMLineIndex"),
            candidate
         );

         // Add the ICE candidate to the peer connection
         peerConnection.addIceCandidate(iceCandidate);
      } catch (Exception e) {
         Log.e(TAG, "Error handling ICE candidate", e);
      }
   }

   // PeerConnection.Observer to handle various PeerConnection events
   private class PeerConnectionObserver implements PeerConnection.Observer {

      @Override
      public void onIceCandidate(IceCandidate candidate) {
         Log.d(TAG, "Received ICE candidate: " + candidate.sdp);
         try {
            // Create a JSON object for the ICE candidate
            JSONObject candidateMessage = new JSONObject();
            candidateMessage.put("type", "candidate");
            candidateMessage.put("candidate", candidate.sdp);
            candidateMessage.put("sdpMid", candidate.sdpMid);
            candidateMessage.put("sdpMLineIndex", candidate.sdpMLineIndex);

            // Send the candidate message via the signaling server
            SignalServiceManager signalServiceManager =
               SignalServiceManager.getInstance(context);
            candidateMessage.put("from", username); // Replace with the actual sender's username
            candidateMessage.put("to", remoteUser); // Replace with the recipient's username
            signalServiceManager.sendMessage(candidateMessage);

            Log.d(TAG, "ICE candidate sent: " + candidateMessage.toString());
         } catch (Exception e) {
            Log.e(TAG, "Error sending ICE candidate", e);
         }
      }

      @Override
      public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {}

      @Override
      public void onAddStream(MediaStream stream) {
         Log.d(
            TAG,
            "Remote stream received with video tracks: " +
            stream.videoTracks.size()
         );
         if (!stream.videoTracks.isEmpty()) {
            // Attach the remote video track to a renderer
            remoteVideoTrack = stream.videoTracks.get(0);

            if (remoteVideoTrack != null) {
               // Log the state of the video track
               Log.d(
                  "WebRtc",
                  "Video track state: " + remoteVideoTrack.state()
               );

               // Initialize remote renderer (only once)
               if (remoteVideoRenderer == null) {
                  initializeRemoteRenderer(context);
               }

               // Ensure SurfaceTextureHelper is created and the remote renderer is set up correctly
               if (remoteVideoTrack != null && remoteVideoRenderer != null) {
                  // If not already done, create SurfaceTextureHelper

                  SurfaceTextureHelper surfaceTextureHelper =
                     SurfaceTextureHelper.create(
                        "remoteCaptureThread",
                        eglBase.getEglBaseContext()
                     );



                  Log.d("WebRtc", "Remote video renderer added.");
               }
            }
         }
      }

      @Override
      public void onRemoveStream(MediaStream stream) {}

      @Override
      public void onDataChannel(DataChannel dataChannel) {}

      @Override
      public void onRenegotiationNeeded() {}

      @Override
      public void onSignalingChange(
         PeerConnection.SignalingState signalingState
      ) {}

      @Override
      public void onIceConnectionChange(
         PeerConnection.IceConnectionState iceConnectionState
      ) {
         Log.d(TAG, "ICE connection state: " + iceConnectionState);
      }

      @Override
      public void onIceConnectionReceivingChange(boolean receiving) {}

      @Override
      public void onIceGatheringChange(
         PeerConnection.IceGatheringState newState
      ) {}

      @Override
      public void onAddTrack(
         RtpReceiver receiver,
         MediaStream[] mediaStreams
      ) {}
   }

   private class MySdpObserver implements SdpObserver {

      @Override
      public void onCreateSuccess(SessionDescription sdp) {
         Log.d(TAG, "SDP created successfully: " + sdp.type);
         // Set the local description with the answer SDP
         peerConnection.setLocalDescription(this, sdp);
      }

      @Override
      public void onSetSuccess() {
         Log.d(TAG, "SDP set successfully");
      }

      @Override
      public void onCreateFailure(String error) {
         Log.e(TAG, "SDP creation failed: " + error);
      }

      @Override
      public void onSetFailure(String error) {
         Log.e(TAG, "SDP set failed: " + error);
      }
   }

   private void sendAnswerToRemotePeer(
      SessionDescription answerSdp,
      String remoteUser,
      String username
   ) {
      SignalServiceManager signalServiceManager =
         SignalServiceManager.getInstance(context);
      JSONObject answerMessage = new JSONObject();
      try {
         answerMessage.put("from", username);
         answerMessage.put("to", remoteUser);
         answerMessage.put("type", "answer");
         answerMessage.put("sdp", answerSdp.description);
         signalServiceManager.sendMessage(answerMessage);
      } catch (Exception e) {
         Log.e(TAG, "Error sending answer SDP to remote peer", e);
      }
   }

   public void initializeRemoteRenderer(Context context) {
      if (eglBase == null) {
         Log.e(TAG, "EglBase is null. Cannot initialize remote renderer.");
         return;
      }

      if (eglBase.getEglBaseContext() == null) {
         Log.e(
            TAG,
            "EglBase context is null. Cannot initialize remote renderer."
         );
         return;
      }

      if (context instanceof Activity) {
         remoteVideoRenderer =
         ((Activity) context).findViewById(R.id.remoteRenderer2);
         localVideoRenderer =
         ((Activity) context).findViewById(R.id.localRenderer2);
         Log.d(
            TAG,
            "remoteVideoRenderer initialized: " + (remoteVideoRenderer != null)
         );
         if (remoteVideoRenderer == null) {
            Log.e(TAG, "RemoteVideoRenderer is null. Check your layout file.");
            return;
         }
         ((Activity) context).runOnUiThread(() -> {
               remoteVideoRenderer
                  .getViewTreeObserver()
                  .addOnGlobalLayoutListener(
                     new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                           remoteVideoRenderer
                              .getViewTreeObserver()
                              .removeOnGlobalLayoutListener(this);
                           remoteVideoRenderer.init(
                              eglBase.getEglBaseContext(),
                              null
                           );
                           remoteVideoRenderer.setMirror(false);
                           remoteVideoRenderer.setZOrderMediaOverlay(true);
                           remoteVideoRenderer.setEnableHardwareScaler(true); // Enable this for better performance
                           remoteVideoRenderer.setVisibility(View.VISIBLE);
                        }
                     }
                  );
               localVideoRenderer.init(eglBase.getEglBaseContext(), null);

               // Assuming you have a method to create and get a local video track
               localVideoTrack = createLocalVideoTrack();

               if (localVideoTrack != null) {
                  localVideoTrack.addSink(localVideoRenderer);
                  Log.d("WebRtc", "Local video renderer added.");
               } else {
                  Log.e("WebRtc", "Failed to create local video track.");
               }

            if (remoteVideoTrack != null) {
               Log.d(TAG, "Adding remote video track to renderer.");
               remoteVideoTrack.addSink(remoteVideoRenderer);
            } else {
               Log.e(TAG, "Remote video track is null. Check if remote peer is sending video.");
            }
            });
         // Ensure the renderer is initialized after layout is complete

      }
   }

   private VideoTrack createLocalVideoTrack() {
      // Create a VideoCapturer to capture video from the camera
      try {
         VideoCapturer videoCapturer = createCameraCapturer();

         // Ensure that the video capturer is initialized before starting the capture
         if (videoCapturer == null) {
            Log.e(TAG, "Failed to create video capturer.");
            return null;
         }

         // Create the video source using the capturer
         VideoSource videoSource = peerConnectionFactory.createVideoSource(
            videoCapturer.isScreencast()
         );

         SurfaceTextureHelper surfaceTextureHelper =
            SurfaceTextureHelper.create(
               "CaptureThread",
               eglBase.getEglBaseContext()
            );

         videoCapturer.initialize(
            surfaceTextureHelper,
            context,
            videoSource.getCapturerObserver()
         );
         videoCapturer.startCapture(720, 1280, 30);

         // Create a video track from the source
         return peerConnectionFactory.createVideoTrack(
            "local_video_track",
            videoSource
         );
      } catch (Exception e) {
         Log.e(TAG, e.toString(), e);
         return null;
      }
   }
}
