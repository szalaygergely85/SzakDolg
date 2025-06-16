package com.zen_vy.chat.activity.register;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.zen_vy.chat.R;
import com.zen_vy.chat.activity.login.LoginActivity;
import com.zen_vy.chat.activity.profile.ProfileActivity;
import com.zen_vy.chat.activity.profile.ProfileConstants;
import com.zen_vy.chat.constans.IntentConstants;
import com.zen_vy.chat.constans.SharedPreferencesConstants;
import com.zen_vy.chat.models.user.constans.UserConstans;
import com.zen_vy.chat.models.user.entity.User;
import com.zen_vy.chat.models.user.service.UserService;
import com.zen_vy.chat.util.HashUtils;
import com.zen_vy.chat.util.KeyStoreUtil;
import com.zen_vy.chat.util.SharedPreferencesUtil;
import com.zen_vy.chat.util.UUIDUtil;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

   private UserService userService;
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

      userService = new UserService(this);

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
                  registerUser(email, pass, pass2, displayName);
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
      } else if (!isEmailValid(email)) {
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

   public void registerUser(
      String email,
      String pass,
      String pass2,
      String displayName
   ) {
      String hashPass = HashUtils.hashPassword(pass);
      HashMap<String, String> keyPair = KeyStoreUtil.generateKeyPair();

      String uuid = UserConstans.UUID_PREFIX + UUIDUtil.UUIDGenerator();

      User user = new User(
         displayName,
         email,
         hashPass,
         keyPair.get("Public"),
         UserConstans.STATUS_ACTIVE,
         UserConstans.TAG_PENDING,
         null,
              uuid
      );

      userService.addUser(
         user,
         new UserService.LoginCallback<User>() {
            @Override
            public void onSuccess(User data) {
               KeyStoreUtil.writePrivateKeysToFile(
                  RegisterActivity.this,
                  keyPair.get("Private"),
                  data
               );

               SharedPreferencesUtil.setStringPreference(
                  RegisterActivity.this,
                  SharedPreferencesConstants.USERTOKEN,
                  data.getToken()
               );
               SharedPreferencesUtil.setStringPreference(
                  RegisterActivity.this,
                  SharedPreferencesConstants.UUID,
                  data.getUuid()
               );
               Intent intent = new Intent(
                  RegisterActivity.this,
                  ProfileActivity.class
               );
               intent.putExtra(
                  IntentConstants.PROFILE_ACTION,
                  ProfileConstants.ACCEPT_PROFILE
               );
               startActivity(intent);
            }

            @Override
            public void onUserNotFound() {
               runOnUiThread(() -> {
                  editEmailLayout.setError(getText(R.string.email_is_already_registered));
               });
            }

            @Override
            public void onError(Throwable t) {}
         }
      );
   }

   public boolean isEmailValid(String email) {
      String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
      Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
      Matcher matcher = pattern.matcher(email);
      return matcher.matches();
   }
}
