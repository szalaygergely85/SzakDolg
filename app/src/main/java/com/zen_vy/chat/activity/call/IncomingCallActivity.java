package com.zen_vy.chat.activity.call;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.zen_vy.chat.R;

public class IncomingCallActivity extends AppCompatActivity {

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_incoming_call);

      String roomName = getIntent().getStringExtra("ROOM_NAME");
      TextView incomingCallText = findViewById(R.id.incomingCallText);
      incomingCallText.setText("Incoming Call...");

      Button acceptButton = findViewById(R.id.acceptButton);
      Button rejectButton = findViewById(R.id.rejectButton);

      acceptButton.setOnClickListener(v -> {
         Intent intent = new Intent(
            IncomingCallActivity.this,
            CallActivity.class
         );
         intent.putExtra("ROOM_NAME", roomName);
         startActivity(intent);
         finish();
      });

      rejectButton.setOnClickListener(v -> {
         // Stop ringing sound or perform rejection logic
         finish();
      });

      // Start ringing sound
      startRinging();
   }

   private void startRinging() {}
}
