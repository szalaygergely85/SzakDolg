package com.zen_vy.chat.activity.base;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;

import com.zen_vy.chat.activity.login.LoginActivity;
import com.zen_vy.chat.constans.AppConstants;
import com.zen_vy.chat.constans.SharedPreferencesConstants;
import com.zen_vy.chat.models.user.dbutil.UserDatabaseUtil;
import com.zen_vy.chat.models.user.entity.User;
import com.zen_vy.chat.util.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

public class BaseActivity extends AppCompatActivity {


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      preparePermissions();
      requestNextPermission();

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
         navigateToLogin();
      } else {
         currentUser = _getCurrentUser(token, userId);
         if (currentUser == null) {
            _deletePref();
            navigateToLogin();
         }
      }
   }

   protected void navigateToLogin() {
      Log.d(AppConstants.LOG_TAG, "Navigating to login...");
      Intent intent = new Intent(this, LoginActivity.class);
      intent.setFlags(
         Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
      );
      startActivity(intent);
      finish();
   }

   private User _getCurrentUser(String token, String userId) {
      UserDatabaseUtil userDatabaseUtil = new UserDatabaseUtil(this, userId);

      return userDatabaseUtil.getCurrentUserByToken(token);
   }

   private void _deletePref() {
      SharedPreferencesUtil.deletePreference(
         this,
         SharedPreferencesConstants.USERTOKEN
      );

      SharedPreferencesUtil.deletePreference(
         this,
         SharedPreferencesConstants.USER_ID
      );
   }

   private void preparePermissions() {
      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
         permissionsToRequest.add(Manifest.permission.READ_MEDIA_IMAGES);
         permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS);
      } else {
         permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE);
      }

      permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
      permissionsToRequest.add(Manifest.permission.CAMERA);
      permissionsToRequest.add(Manifest.permission.RECORD_AUDIO);
   }
   private void requestNextPermission() {
      if (currentPermissionIndex < permissionsToRequest.size()) {
         String permission = permissionsToRequest.get(currentPermissionIndex);
         if (ContextCompat.checkSelfPermission(this, permission)
                 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{permission},
                    PERMISSION_REQUEST_CODE
            );
         } else {
            currentPermissionIndex++;
            requestNextPermission(); // Skip already-granted
         }
      }
   }

   @Override
   public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                          @NonNull int[] grantResults) {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);

      if (requestCode == PERMISSION_REQUEST_CODE) {
         currentPermissionIndex++;
         requestNextPermission();
      }
   }


   protected String token;
   protected String userId;
   protected User currentUser;


   private final List<String> permissionsToRequest = new ArrayList<>();
   private int currentPermissionIndex = 0;
   private static final int PERMISSION_REQUEST_CODE = 999;
}
