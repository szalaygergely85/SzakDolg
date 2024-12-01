package com.example.szakdolg.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.szakdolg.R;
import com.example.szakdolg.activity.ForgotPasswordActivity;
import com.example.szakdolg.activity.register.RegisterActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

   private TextInputLayout editMailLayout;
   private EditText editMail;
   private TextInputLayout editPassLayout;
   private EditText editPass;
   private TextView txtForgot;
   private TextView btnReg;
   private Button btnLog;
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
      editMailLayout = findViewById(R.id.edtLgnEmail);
      editMail = editMailLayout.getEditText();
      editPassLayout = findViewById(R.id.edtLgnPass);
      editPass = editPassLayout.getEditText();
      btnReg = findViewById(R.id.btnLgnReg);
      btnLog = findViewById(R.id.btnLgnLogin);
      txtForgot = findViewById(R.id.txtLgnForgot);
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

               boolean isValid = true;

               if (email.isEmpty()) {
                  editMailLayout.setError(getString(R.string.email_is_required));
                  isValid = false;
               } else {
                  editMailLayout.setError(null); // Clear error when valid
               }

               if (password.isEmpty()) {
                  editPassLayout.setError(getString(R.string.password_is_required));
                  isValid = false;
               } else {
                  editPassLayout.setError(null); // Clear error when valid
               }

               if (isValid) {
                  loginActivityHelper.loginUser(email, password);
               }
            }
         }
      );
   }
}

