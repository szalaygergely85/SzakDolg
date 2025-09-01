package com.zen_vy.chat.activity.passwords;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.MaterialToolbar;
import com.zen_vy.chat.R;
import com.zen_vy.chat.activity.login.LoginActivity;
import com.zen_vy.chat.models.user.service.UserService;

public class ForgotPasswordActivity extends AppCompatActivity {
   private EditText email;
   private Button send;
   TextView tvBackToLogin;

   private UserService userService;
   public void initView() {
      email = findViewById(R.id.edtFrgtEmail);
      send = findViewById(R.id.btnForgetSend);
     tvBackToLogin = findViewById(R.id.tvBackToLogin);
   }

   @Override
   public boolean onSupportNavigateUp() {

      return super.onSupportNavigateUp();
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_forgot_password);

      initView();

      TextView tvBackToLogin = findViewById(R.id.tvBackToLogin);
      tvBackToLogin.setOnClickListener(v -> {
         Intent intent = new Intent(
                 ForgotPasswordActivity.this,
                 LoginActivity.class
         );
         startActivity(intent);
         finish();

      });



      send.setOnClickListener(
         new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               Intent intent = new Intent(
                       ForgotPasswordActivity.this,
                       LoginActivity.class
               );
               startActivity(intent);
               finish();
            }
         }
      );
   }
}
