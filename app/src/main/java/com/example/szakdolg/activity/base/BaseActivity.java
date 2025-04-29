package com.example.szakdolg.activity.base;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;

import com.example.szakdolg.activity.login.LoginActivity;
import com.example.szakdolg.constans.AppConstants;
import com.example.szakdolg.constans.SharedPreferencesConstants;
import com.example.szakdolg.models.user.dbutil.UserDatabaseUtil;
import com.example.szakdolg.models.user.entity.User;
import com.example.szakdolg.util.SharedPreferencesUtil;

public class BaseActivity extends AppCompatActivity {


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      requestReadPermission();
      requestWritePermission();
      requestNotificationPermission();
      requestCameraPermission();
      requestRecordAudioPermission();

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

   @Override
   public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);

      switch (requestCode) {
         case READ_PERMISSION_CODE:
            if (
                    grantResults.length > 0 &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
               // Permission granted
               Toast
                       .makeText(this, "Read permission granted", Toast.LENGTH_SHORT)
                       .show();
            } else {
               // Permission denied
               Toast
                       .makeText(this, "Read permission denied", Toast.LENGTH_SHORT)
                       .show();
            }
            break;
         case WRITE_PERMISSION_CODE:
            if (
                    grantResults.length > 0 &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
               // Permission granted
               Toast
                       .makeText(
                               this,
                               "Write permission granted",
                               Toast.LENGTH_SHORT
                       )
                       .show();
            } else {
               // Permission denied
               Toast
                       .makeText(this, "Write permission denied", Toast.LENGTH_SHORT)
                       .show();
            }
            break;
      }
   }
   private void requestReadPermission() {
      String readPermission;
      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
         readPermission = Manifest.permission.READ_MEDIA_IMAGES;
      } else {
         readPermission = Manifest.permission.READ_EXTERNAL_STORAGE;
      }

      if (ContextCompat.checkSelfPermission(this, readPermission)
              != PackageManager.PERMISSION_GRANTED) {
         ActivityCompat.requestPermissions(
                 this,
                 new String[]{readPermission},
                 READ_PERMISSION_CODE
         );
      } else {
         Toast.makeText(this, "Read permission already granted", Toast.LENGTH_SHORT).show();
      }
   }


   // Example function to request write permission
   private void requestWritePermission() {
      if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
              != PackageManager.PERMISSION_GRANTED) {
         ActivityCompat.requestPermissions(
                 this,
                 new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                 WRITE_PERMISSION_CODE
         );
      } else {
         Toast.makeText(this, "Write permission already granted", Toast.LENGTH_SHORT).show();
      }
   }

   private void requestNotificationPermission() {
      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
         if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    NOTIFICATION_PERMISSION_CODE
            );
         }
      }
   }

   private void requestCameraPermission() {
      if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
              != PackageManager.PERMISSION_GRANTED) {
         ActivityCompat.requestPermissions(
                 this,
                 new String[]{Manifest.permission.CAMERA},
                 CAMERA_PERMISSION_CODE
         );
      }
   }

   private void requestRecordAudioPermission() {
      if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
              != PackageManager.PERMISSION_GRANTED) {
         ActivityCompat.requestPermissions(
                 this,
                 new String[]{Manifest.permission.RECORD_AUDIO},
                 RECORD_AUDIO_PERMISSION_CODE
         );
      }
   }


   protected String token;
   protected String userId;
   protected User currentUser;


   private static final int READ_PERMISSION_CODE = 202;
   private static final int WRITE_PERMISSION_CODE = 203;

   private static final int NOTIFICATION_PERMISSION_CODE = 204;
   private static final int CAMERA_PERMISSION_CODE = 205;
   private static final int RECORD_AUDIO_PERMISSION_CODE = 206;
}
