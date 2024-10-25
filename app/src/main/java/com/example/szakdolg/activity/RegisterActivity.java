package com.example.szakdolg.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.szakdolg.R;
import com.example.szakdolg.main.activity.MainActivity;
import com.example.szakdolg.model.user.api.UserApiHelper;
import com.example.szakdolg.model.user.model.User;
import com.example.szakdolg.model.user.service.UserService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

   private EditText editEmail;
   private EditText editPass;
   private EditText editFullName;
   private EditText editPass2;
   private EditText editDisplayName;
   private EditText editPhone;
   private TextView textSignIn;
   private Button btnReg;

   private TextView txtRegError;

   private static final String TAG = "RegisterActivity";
   UserApiHelper userApiHelper = new UserApiHelper();
   private String email;
   private String pass;
   private String pass2;
   private String displayName;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_register);
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

   public static boolean _isEmailValid(String email) {
      String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
      Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
      Matcher matcher = pattern.matcher(email);
      return matcher.matches();
   }

   private boolean _isPasswordStrong(String pass) {
      return pass.length() > 5;
   }

   private void _registerUser(
      String email,
      String pass,
      String pass2,
      String displayName
   ) {
      if (
         email.isEmpty() ||
         pass.isEmpty() ||
         pass2.isEmpty() ||
         displayName.isEmpty()
      ) {
         txtRegError.setVisibility(View.VISIBLE);
         txtRegError.setText("Please fill in all fields.");
         return;
      }

      if (!pass.equals(pass2)) {
         txtRegError.setVisibility(View.VISIBLE);
         txtRegError.setText("Passwords do not match. Please try again.");
         return;
      }

      if (!_isPasswordStrong(pass)) {
         txtRegError.setVisibility(View.VISIBLE);
         txtRegError.setText("Password must be at least 6 characters long.");
         return;
      }

      if (!_isEmailValid(email)) {
         txtRegError.setVisibility(View.VISIBLE);
         txtRegError.setText("Please enter a valid email address.");
         return;
      }
      User user = new User(displayName, email, pass);

      UserService userService = new UserService();
      userService.addUser(user, RegisterActivity.this);


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
               _registerUser(email, pass, pass2, displayName);
            }
         }
      );

      textSignIn.setOnClickListener(
         new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(
                  RegisterActivity.this,
                  MainActivity.class
               );
               startActivity(intent);
            }
         }
      );
   }
}
