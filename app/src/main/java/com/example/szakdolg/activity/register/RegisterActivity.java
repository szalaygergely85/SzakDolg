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

public class RegisterActivity extends AppCompatActivity {

   private EditText editEmail;
   private EditText editPass;
   private EditText editPass2;
   private EditText editDisplayName;

   private TextView textSignIn;
   private Button btnReg;

   private TextView txtRegError;

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
      editEmail = findViewById(R.id.edtRegEmail);
      editPass = findViewById(R.id.edtRegPass1);
      editPass2 = findViewById(R.id.edtRegPass2);
      editDisplayName = findViewById(R.id.edtRegName);
      btnReg = findViewById(R.id.btnRegReg);
      textSignIn = findViewById(R.id.textSignIn);
      txtRegError = findViewById(R.id.txtRegError);
      txtRegError.setVisibility(View.GONE);
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
               registerActivityHelper.registerUser(
                  email,
                  pass,
                  pass2,
                  displayName,
                  txtRegError
               );
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
}
