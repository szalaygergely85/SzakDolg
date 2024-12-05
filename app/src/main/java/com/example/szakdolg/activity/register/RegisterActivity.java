package com.example.szakdolg.activity.register;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.szakdolg.R;
import com.example.szakdolg.activity.login.LoginActivity;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {

   private TextInputLayout editEmailLayout;
   private EditText editEmail;
   private TextInputLayout editPassLayout;
   private EditText editPass;
   private TextInputLayout editPass2Layout;
   private EditText editPass2;
   private TextInputLayout editDisplayNameLayout;
   private EditText editDisplayName;

   private TextView textSignIn;
   private Button btnReg;

   private String email;
   private String pass;
   private String pass2;
   private String displayName;

   private RegisterActivityHelper registerActivityHelper;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_register);

      registerActivityHelper = new RegisterActivityHelper(this);
      _initView();
      _setOnClickListeners();
   }

   protected void onStart() {
      super.onStart();
   }

   private void _initView() {
      editEmailLayout = findViewById(R.id.edtRegEmail);
      editEmail = editEmailLayout.getEditText();
      editPassLayout = findViewById(R.id.edtRegPass1);
      editPass = editPassLayout.getEditText();
      editPass2Layout = findViewById(R.id.edtRegPass2);
      editPass2 = editPass2Layout.getEditText();
      editDisplayNameLayout = findViewById(R.id.edtRegName);
      editDisplayName = editDisplayNameLayout.getEditText();
      btnReg = findViewById(R.id.btnRegReg);
      textSignIn = findViewById(R.id.textSignIn);
   }

   private void _setOnClickListeners() {
      btnReg.setOnClickListener(
         new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               email = editEmail.getText().toString();
               pass = editPass.getText().toString();
               pass2 = editPass2.getText().toString();
               displayName = editDisplayName.getText().toString();
               if (_validateValues(email, pass, pass2, displayName)) {
                  registerActivityHelper.registerUser(
                     email,
                     pass,
                     pass2,
                     displayName
                  );
               }
            }
         }
      );
      textSignIn.setOnClickListener(
         new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(
                  RegisterActivity.this,
                  LoginActivity.class
               );
               startActivity(intent);
            }
         }
      );
   }

   private boolean _validateValues(
      String email,
      String pass,
      String pass2,
      String displayName
   ) {
      editEmailLayout.setError(null);
      editPassLayout.setError(null);
      editPass2Layout.setError(null);
      editDisplayNameLayout.setError(null);
      boolean valid = true;
      if (email.isEmpty()) {
         editEmailLayout.setError(getString(R.string.email_is_required));
         valid = false;
      } else if (!registerActivityHelper.isEmailValid(email)) {
         editEmailLayout.setError(getString(R.string.email_invalid));
         valid = false;
      }
      if (displayName.isEmpty()) {
         editDisplayNameLayout.setError(
            getString(R.string.display_name_is_required)
         );
         valid = false;
      }

      if (pass.isEmpty()) {
         editPassLayout.setError(getString(R.string.password_is_required));
         valid = false;
      } else if (pass.length() < 6) {
         editPassLayout.setError(getString(R.string.password_too_short));
         valid = false;
      }

      if (pass2.isEmpty()) {
         editPass2Layout.setError(
            getString(R.string.confirm_password_is_required)
         );
         valid = false;
      } else if (!pass.equals(pass2)) {
         editPass2Layout.setError(getString(R.string.passwords_dont_match));
         valid = false;
      }

      return valid;
   }
}
