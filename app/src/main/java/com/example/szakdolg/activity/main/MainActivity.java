package com.example.szakdolg.activity.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.szakdolg.R;
import com.example.szakdolg.activity.base.BaseActivity;
import com.example.szakdolg.activity.main.adapter.MainAdapter;
import com.example.szakdolg.constans.IntentConstants;
import com.example.szakdolg.constans.SharedPreferencesConstants;
import com.example.szakdolg.models.conversation.entity.Conversation;
import com.example.szakdolg.models.image.util.ImageUtil;
import com.example.szakdolg.models.message.entity.MessageEntry;
import com.example.szakdolg.util.SharedPreferencesUtil;
import com.example.szakdolg.websocket.WebSocketService;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import java.util.List;

public class MainActivity extends BaseActivity {
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

      mainAdapter = new MainAdapter(this, currentUser, messageBoardRecView);
   }
   @Override
   protected void onPause() {
      super.onPause();
      LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
   }
   @Override
   protected void onResume() {
      super.onResume();
      LocalBroadcastManager.getInstance(this).registerReceiver(
              broadcastReceiver, new IntentFilter("com.example.szakdolg.models.message.entity.MessageBroadCast")
      );
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

   @Override
   protected void onStart() {
      super.onStart();

      Intent serviceIntent = new Intent(this, WebSocketService.class);
      serviceIntent.putExtra(IntentConstants.CURRENT_USER, currentUser);
      serviceIntent.putExtra(IntentConstants.USER_TOKEN, token);
      startService(serviceIntent);

      this._mainActivityHelper = new MainActivityHelper(this, currentUser);

      MenuItem themeItem = navigationView
         .getMenu()
         .findItem(R.id.main_dark_theme);
      SwitchCompat switchTheme = themeItem
         .getActionView()
         .findViewById(R.id.switch_item);

      _mainActivityHelper.setBottomNavMenu(bottomNavigationView);
      _mainActivityHelper.setListeners(topAppBar, drawerLayout, navigationView);

      boolean isNightMode = SharedPreferencesUtil.getBooleanPreferences(
         this,
         SharedPreferencesConstants.DARK_MODE
      );

      switchTheme.setChecked(isNightMode);

      // Apply the theme
      if (isNightMode) {
         AppCompatDelegate.setDefaultNightMode(
            AppCompatDelegate.MODE_NIGHT_YES
         );
      } else {
         AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
      }

      switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
         SharedPreferencesUtil.setBoolean(
            this,
            SharedPreferencesConstants.DARK_MODE,
            isChecked
         );

         if (isChecked) {
            AppCompatDelegate.setDefaultNightMode(
               AppCompatDelegate.MODE_NIGHT_YES
            );
         } else {
            AppCompatDelegate.setDefaultNightMode(
               AppCompatDelegate.MODE_NIGHT_NO
            );
         }
      });

      List<Conversation> conversations =
         _mainActivityHelper.getConversationList();

      if (conversations != null) {
         emptyLayout.setVisibility(View.GONE);
         withItemsLayout.setVisibility(View.VISIBLE);

         mainAdapter.setConversationList(conversations);

         messageBoardRecView.setAdapter(mainAdapter);
         messageBoardRecView.setLayoutManager(new LinearLayoutManager(this));
      } else {
         emptyLayout.setVisibility(View.VISIBLE);
         withItemsLayout.setVisibility(View.GONE);
      }

      //set menu text and image logo

      profileTextHeader.setText(currentUser.getDisplayName());

      String imageUrl = ImageUtil.buildProfileImageUrl(currentUser.getUserId());
      if (imageUrl != null) {
         Glide
            .with(this)
            .load(imageUrl)
            .placeholder(R.drawable.ic_blank_profile)
            .error(R.drawable.ic_blank_profile)
            .into(profileImageHeader);
      } else {
         profileImageHeader.setImageResource(R.drawable.ic_blank_profile);
      }
   }


   private void _initView() {
      navigationView = findViewById(R.id.navigation_view_main);
      drawerLayout = findViewById(R.id.drawerLayoutMain);
      topAppBar = findViewById(R.id.topAppBar);
      messageBoardRecView = findViewById(R.id.messageBoardRecView);
      bottomNavigationView = findViewById(R.id.bottom_nav_main);
      emptyLayout = findViewById(R.id.llayoutEmptyMain);
      withItemsLayout = findViewById(R.id.llayoutWithItemsMain);
      View headerView = navigationView.getHeaderView(0);
      profileImageHeader = headerView.findViewById(R.id.profile_image_header);
      profileTextHeader = headerView.findViewById(R.id.profile_name_header);
   }

   private BottomNavigationView bottomNavigationView;
   private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
         MessageEntry message = (MessageEntry) intent.getSerializableExtra("message");

         if (message != null) {

            mainAdapter.notifyDataSetChanged();
         }
      }
   };

   private DrawerLayout drawerLayout;
   private LinearLayout emptyLayout;
   private MainActivityHelper _mainActivityHelper;
   private MainAdapter mainAdapter;
   private RecyclerView messageBoardRecView;
   private NavigationView navigationView;
   private static final int READ_PERMISSION_CODE = 202;
   private static final int WRITE_PERMISSION_CODE = 203;
   private LinearLayout withItemsLayout;
   private MaterialToolbar topAppBar;
   private ImageView profileImageHeader;
   private TextView profileTextHeader;
}
