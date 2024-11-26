package com.example.szakdolg.activity.call;

import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.szakdolg.R;
import com.example.szakdolg.webrtc.DataModelType;
import com.example.szakdolg.webrtc.MainRepository;

import org.webrtc.SurfaceViewRenderer;


public class CallActivity extends AppCompatActivity implements MainRepository.Listener {

   private static final String TAG = "CallActivity";
   private TextView incomingNameTV;
   private View incomingCallLayout;
   private EditText targetUserNameEt;
   private AppCompatButton callBtn;
   private ImageView acceptButton;
   private ImageView rejectButton;
   private ImageView switchCameraButton;
   private ImageView micButton;
   private ImageView videoButton;
   private ImageView endCallButton;
   private
   SurfaceViewRenderer localView;
   private
   SurfaceViewRenderer remoteView;
   private View callLayout;
   private View whoToCallLayout;


   private MainRepository mainRepository;
   private Boolean isCameraMuted = false;
   private Boolean isMicrophoneMuted = false;

   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_call);
      mainRepository = MainRepository.getInstance();
      mainRepository.login("Gege", this, ()->{});
      init();
   }

   private void init(){
      incomingCallLayout = findViewById(R.id.incomingCallLayout);
      incomingNameTV = findViewById(R.id.incomingNameTV);
      acceptButton = findViewById(R.id.acceptButton);
      rejectButton = findViewById(R.id.rejectButton);
      targetUserNameEt = findViewById(R.id.targetUserNameEt);
      callBtn = findViewById(R.id.callBtn);
      switchCameraButton = findViewById(R.id.switch_camera_button);
      micButton = findViewById(R.id.mic_button);
      videoButton = findViewById(R.id.video_button);
      endCallButton = findViewById(R.id.end_call_button);
      localView = findViewById(R.id.local_view);
      remoteView = findViewById(R.id.remote_view);
      callLayout = findViewById(R.id.callLayout);
      whoToCallLayout = findViewById(R.id.whoToCallLayout);




      callBtn.setOnClickListener(v->{
         //start a call request here
         mainRepository.sendCallRequest(targetUserNameEt.getText().toString());

      });
      mainRepository.initLocalView(localView);
      mainRepository.initRemoteView(remoteView);
      mainRepository.listener = this;

      mainRepository.subscribeForLatestEvent(data->{
         if (data.getType()== DataModelType.StartCall){
            runOnUiThread(()->{
               incomingNameTV.setText(data.getSender()+" is Calling you");
               incomingCallLayout.setVisibility(View.VISIBLE);
               acceptButton.setOnClickListener(v->{
                  //star the call here
                  mainRepository.startCall(data.getSender());
                  incomingCallLayout.setVisibility(View.GONE);
               });
               rejectButton.setOnClickListener(v->{
                  incomingCallLayout.setVisibility(View.GONE);
               });
            });
         }
      });

      switchCameraButton.setOnClickListener(v->{
         mainRepository.switchCamera();
      });

      micButton.setOnClickListener(v->{
         if (isMicrophoneMuted){
            micButton.setImageResource(R.drawable.ic_baseline_mic_off_24);
         }else {
            micButton.setImageResource(R.drawable.ic_baseline_mic_24);
         }
         mainRepository.toggleAudio(isMicrophoneMuted);
         isMicrophoneMuted=!isMicrophoneMuted;
      });

      videoButton.setOnClickListener(v->{
         if (isCameraMuted){
            videoButton.setImageResource(R.drawable.ic_baseline_videocam_off_24);
         }else {
            videoButton.setImageResource(R.drawable.ic_baseline_videocam_24);
         }
         mainRepository.toggleVideo(isCameraMuted);
         isCameraMuted=!isCameraMuted;
      });

     endCallButton.setOnClickListener(v->{
         mainRepository.endCall();
         finish();
      });
   }

   @Override
   public void webrtcConnected() {
      runOnUiThread(()->{
         incomingCallLayout.setVisibility(View.GONE);
         whoToCallLayout.setVisibility(View.GONE);
         callLayout.setVisibility(View.VISIBLE);
      });
   }

   @Override
   public void webrtcClosed() {
      runOnUiThread(this::finish);
   }
}