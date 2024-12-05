package com.example.szakdolg.activity.call;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.szakdolg.R;
import com.example.szakdolg.signaling.SignalServiceManager;
import com.example.szakdolg.webrtc.WebRtcManager;

public class StartCall extends AppCompatActivity {

   private static final String TAG = "StartCallActivity";

   private static final String username = "user1";
   private static final String remoteuser = "user2";
   private SignalServiceManager signalServiceManager;

   private WebRtcManager webRtcManager;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      EdgeToEdge.enable(this);
      setContentView(R.layout.activity_start_call);
      /*
	webRtcManager = new WebRtcManager(this, username, remoteuser);

	// Initialize SignalServiceManager
	signalServiceManager = SignalServiceManager.getInstance(this);

	// Set listener after ensuring the service is bound
	signalServiceManager.setServiceConnectionListener(
		new SignalServiceManager.ServiceConnectionListener() {
			@Override
			public void onServiceConnected() {
			signalServiceManager.setSignalListener(
				new SignalService.SignalListener() {
					@Override
					public void onMessageReceived(String message) {
						handleIncomingMessage(message);
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
			signalServiceManager.connect(AppConstants.WS_URL, username); // Replace with actual user ID if needed
			}

			@Override
			public void onServiceConnectionFailed(String error) {
			Log.e(TAG, "Failed to bind to SignalService: " + error);
			}
		}
	);

	// Initialize buttons for voice and video calls
	Button voiceCallButton = findViewById(R.id.voice_button);
	Button videoCallButton = findViewById(R.id.video_button);

	voiceCallButton.setOnClickListener(v -> {
		// Send message for voice call (for example, initiating a call)
		try {
			JSONObject message = new JSONObject();
			message.put("from", username);
			message.put("type", "offer");
			message.put("callType", "voice");
			message.put("to", remoteuser);
			signalServiceManager.sendMessage(message);
		} catch (Exception e) {
			Log.e(TAG, "Error sending message for voice call", e);
		}
	});

	videoCallButton.setOnClickListener(v -> {
		if (webRtcManager != null) {
			webRtcManager.createOffer(
			new SdpObserver() {
				@Override
				public void onCreateSuccess(SessionDescription sdp) {
					try {
						JSONObject message = new JSONObject();
						message.put("from", username);
						message.put("type", "offer");
						message.put("callType", "video");
						message.put("to", remoteuser);
						message.put("sdp", sdp.description); // Use the correct property (it's `.description`)

						signalServiceManager.sendMessage(message);
						Log.d(
						TAG,
						"Video call offer sent: " + message.toString()
						);
					} catch (Exception e) {
						Log.e(TAG, "Error sending WebRTC offer", e);
					}
				}

				@Override
				public void onSetSuccess() {}

				@Override
				public void onCreateFailure(String error) {
					Log.e(TAG, "Failed to create WebRTC offer: " + error);
				}

				@Override
				public void onSetFailure(String error) {}
			}
			);
		} else {
			Log.e(TAG, "WebRtcManager is null. Cannot create offer.");
		}
	});
}

@Override
protected void onDestroy() {
	super.onDestroy();
	// Properly clean up the service when the activity is destroyed
	if (signalServiceManager != null) {
		signalServiceManager.disconnect();
		signalServiceManager.unbind();
	}
}

private void handleIncomingMessage(String message) {
	try {
		JSONObject messageJson = new JSONObject(message);
		String remoteUser = messageJson.getString("from");
		String type = messageJson.getString("type");

		switch (type) {
			case "offer":
			webRtcManager.handleOffer(messageJson);
			break;
			case "answer":
			webRtcManager.handleAnswer(messageJson);
			break;
			case "candidate":
			webRtcManager.handleCandidate(messageJson);
			break;
			default:
			Log.e(TAG, "Unknown message type: " + type);
			break;
		}
	} catch (Exception e) {
		Log.e(TAG, "Error parsing incoming message: " + e.getMessage());
	}*/
   }
}
