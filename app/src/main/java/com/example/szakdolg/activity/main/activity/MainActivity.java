package com.example.szakdolg.activity.main.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.szakdolg.R;
import com.example.szakdolg.activity.base.BaseActivity;
import com.example.szakdolg.activity.chat.activity.NewChatActivity;
import com.example.szakdolg.activity.main.helper.MainActivityHelper;
import com.example.szakdolg.constans.SharedPreferencesConstants;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends BaseActivity {

   private RecyclerView messageBoardRecView;
   private FloatingActionButton contactsButton;

   private static final int READ_PERMISSION_CODE = 202;
   private static final int WRITE_PERMISSION_CODE = 203;
   private MainActivityHelper _mainActivityHelper;
   private BottomNavigationView bottomNavigationView;

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.nav_menu, menu);
      return super.onCreateOptionsMenu(menu);
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      _initView();
      _setListeners();
   }

   @Override
   protected void onStart() {
      super.onStart();

      this._mainActivityHelper = new MainActivityHelper(this, token, currentUser);

      _mainActivityHelper.setNavMenu(bottomNavigationView);

      //TODO valami gond van itt....
      //_mainActivityHelper.startCacheChecking();

      _mainActivityHelper.setMessageBoard(messageBoardRecView);


   }

   @Override
   public void onRequestPermissionsResult(
      int requestCode,
      @NonNull String[] permissions,
      @NonNull int[] grantResults
   ) {
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

   private void _initView() {
      contactsButton = findViewById(R.id.newMessageButton);
      messageBoardRecView = findViewById(R.id.messageBoardRecView);
      bottomNavigationView = findViewById(
              R.id.bottom_navigation
      );
   }

   private void _setListeners(){
      contactsButton.setOnClickListener(
              new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                    Intent intent = new Intent(
                            MainActivity.this,
                            NewChatActivity.class
                    );
                    intent.putExtra(
                            SharedPreferencesConstants.CURRENT_USER,
                            currentUser
                    );
                    startActivity(intent);
                 }
              }
      );
   }
}
