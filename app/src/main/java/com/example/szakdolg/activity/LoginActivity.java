package com.example.szakdolg.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.szakdolg.R;
import com.example.szakdolg.activity.login.LoginActivityHelper;
import com.example.szakdolg.model.user.api.UserApiHelper;
import com.example.szakdolg.util.HashUtils;

public class LoginActivity extends AppCompatActivity {

   private EditText editMail;
   private EditText editPass;
   private TextView txtForgot;
   private TextView btnReg;
   private Button btnLog;

   private TextView txtError;

   private LoginActivityHelper loginActivityHelper;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_login);

      loginActivityHelper = new LoginActivityHelper(this);

      _initView();
      _setOnClickListeners();
   }

   @Override
   protected void onStart() {
      super.onStart();
   }

   public void _initView() {
      editMail = findViewById(R.id.edtLgnEmail);
      editPass = findViewById(R.id.edtLgnPass);
      btnReg = findViewById(R.id.btnLgnReg);
      btnLog = findViewById(R.id.btnLgnLogin);
      txtForgot = findViewById(R.id.txtLgnForgot);
      txtError = findViewById(R.id.txtLgnError);
   }

   private void _setOnClickListeners() {
      btnReg.setOnClickListener(
         new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(
                  LoginActivity.this,
                  RegisterActivity.class
               );
               startActivity(intent);
            }
         }
      );
      txtForgot.setOnClickListener(
         new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(
                  LoginActivity.this,
                  ForgotPasswordActivity.class
               );
               startActivity(intent);
            }
         }
      );
      btnLog.setOnClickListener(
         new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String email = editMail.getText().toString();
               String password = editPass.getText().toString();

               if (!email.isEmpty() && !password.isEmpty()) {
                  loginActivityHelper.loginUser(email, password);

                  txtError.setVisibility(View.GONE);
               } else {
                  txtError.setVisibility(View.VISIBLE);
                  txtError.setText("Email and password are required.");
               }
            }
         }
      );
   }
}
