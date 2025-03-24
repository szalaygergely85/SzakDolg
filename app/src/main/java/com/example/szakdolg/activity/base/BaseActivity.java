package com.example.szakdolg.activity.base;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import com.example.szakdolg.activity.login.LoginActivity;
import com.example.szakdolg.constans.AppConstants;
import com.example.szakdolg.constans.SharedPreferencesConstants;
import com.example.szakdolg.models.user.dbutil.UserDatabaseUtil;
import com.example.szakdolg.models.user.entity.User;
import com.example.szakdolg.util.SharedPreferencesUtil;

public class BaseActivity extends AppCompatActivity {

   protected String token;
   protected String userId;
   protected User currentUser;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      // Retrieve the token and user ID
      token =
      SharedPreferencesUtil.getStringPreference(
         this,
         SharedPreferencesConstants.USERTOKEN
      );

      userId =
      SharedPreferencesUtil.getStringPreference(
         this,
         SharedPreferencesConstants.USER_ID
      );

      // Optionally handle cases where token or userId is missing
      if (token == null || userId == null) {
         _deletePref();
         _navigateToLogin();
      } else {
         currentUser = _getCurrentUser(token, userId);
         if (currentUser == null) {
            _deletePref();
            _navigateToLogin();
         }
      }
   }

   private void _navigateToLogin() {
      Log.d(AppConstants.LOG_TAG, "Navigating to login...");
      Intent intent = new Intent(this, LoginActivity.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
      startActivity(intent);
      finish();
   }

   private User _getCurrentUser(String token, String userId) {
      UserDatabaseUtil userDatabaseUtil = new UserDatabaseUtil(this, userId);

      return userDatabaseUtil.getCurrentUserByToken(token);
   }

   private void _deletePref(){
      SharedPreferencesUtil.deletePreference(
              this,
              SharedPreferencesConstants.USERTOKEN
      );

      SharedPreferencesUtil.deletePreference(
              this,
              SharedPreferencesConstants.USER_ID
      );
   }
}
