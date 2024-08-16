package com.example.szakdolg.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.example.szakdolg.R;
import com.example.szakdolg.constans.SharedPreferencesConstans;
import com.example.szakdolg.contacts.helper.ContactsApiHelper;
import com.example.szakdolg.conversation.ConversationApiHelper;
import com.example.szakdolg.db.util.ProfileDatabaseUtil;
import com.example.szakdolg.message.MessageApiHelper;
import com.example.szakdolg.messageboard.activity.MessageBoardActivity;
import com.example.szakdolg.user.api.UserApiHelper;
import com.example.szakdolg.user.entity.User;
import com.example.szakdolg.util.SharedPreferencesUtil;

public class MainActivity extends AppCompatActivity {

   private static final String TAG = "MainActivity";
   private static final int READ_PERMISSION_CODE = 202;
   private static final int WRITE_PERMISSION_CODE = 203;
   private String _token;
   private UserApiHelper _userApiHelper = new UserApiHelper();

   private MessageApiHelper _messageApiHelper = new MessageApiHelper();
   private ContactsApiHelper _contactsApiHelper = new ContactsApiHelper();
   private ConversationApiHelper _conversationApiHelper =
      new ConversationApiHelper();

   private User currentUser;

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.nav_menu, menu);
      return super.onCreateOptionsMenu(menu);
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      _setPermissions();
   }

   @Override
   protected void onStart() {
      super.onStart();

      _token =
      SharedPreferencesUtil.getStringPreference(
         this,
         SharedPreferencesConstans.USERTOKEN
      );

      String userId = SharedPreferencesUtil.getStringPreference(
         this,
         SharedPreferencesConstans.USER_ID
      );

      if (_token != null && userId != null) {
         long cacheExpireTimeMillis = SharedPreferencesUtil.getLongPreference(
            this,
            SharedPreferencesConstans.CACHE_EXPIRE
         );

         ProfileDatabaseUtil profileDatabaseUtil = new ProfileDatabaseUtil(
            this,
            userId
         );
         currentUser = profileDatabaseUtil.getCurrentUserByToken(_token);
         if (currentUser != null) {
            if (isCacheExpired(cacheExpireTimeMillis)) {
               new Thread(
                  new Runnable() {
                     @Override
                     public void run() {
                        _refreshDatabaseTask();
                     }
                  }
               )
                  .start();
            }

            Intent intent = new Intent(
               MainActivity.this,
               MessageBoardActivity.class
            );
            Toast.makeText(this, "A user signed in", Toast.LENGTH_SHORT).show();
            startActivity(intent);
            finish();
         } else {
            _navigateToLogin();
         }
      } else {
         _navigateToLogin();
      }
   }

   private void _navigateToLogin() {
      Intent intent = new Intent(MainActivity.this, LoginActivity.class);
      startActivity(intent);
      finish();
   }

   private void _refreshDatabaseTask() {
      _messageApiHelper.checkCachedMessages(_token, this, currentUser);
      _contactsApiHelper.checkCachedContacts(_token, this, currentUser);
      _conversationApiHelper.checkCachedConversation(_token, this, currentUser);
      _conversationApiHelper.checkCachedConversationParticipant(
         _token,
         this,
         currentUser
      );
   }

   private boolean isCacheExpired(long cacheExpireTimeMillis) {
      if (cacheExpireTimeMillis == -1) {
         return true;
      }

      long currentTimeMillis = System.currentTimeMillis();
      return currentTimeMillis > cacheExpireTimeMillis;
   }

   private void _setPermissions() {
      if (
         ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
         ) !=
         PackageManager.PERMISSION_GRANTED
      ) {
         ActivityCompat.requestPermissions(
            this,
            new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
            READ_PERMISSION_CODE
         );
      }
      if (
         ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
         ) !=
         PackageManager.PERMISSION_GRANTED
      ) {
         ActivityCompat.requestPermissions(
            this,
            new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
            WRITE_PERMISSION_CODE
         );
      }
   }
}
