package com.zen_vy.chat.activity.main;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.zen_vy.chat.R;
import com.zen_vy.chat.activity.base.BaseActivity;
import com.zen_vy.chat.activity.contacts.activity.ContactsActivity;
import com.zen_vy.chat.activity.contacts.constans.ContactsConstans;
import com.zen_vy.chat.activity.main.adapter.MainAdapter;
import com.zen_vy.chat.activity.profile.ProfileActivity;
import com.zen_vy.chat.activity.profile.ProfileConstants;
import com.zen_vy.chat.constans.IntentConstants;
import com.zen_vy.chat.constans.SharedPreferencesConstants;
import com.zen_vy.chat.models.contacts.dto.ConversationDTO;
import com.zen_vy.chat.models.conversation.service.ConversationService;
import com.zen_vy.chat.models.device.Device;
import com.zen_vy.chat.models.device.DeviceService;
import com.zen_vy.chat.models.image.util.ImageUtil;
import com.zen_vy.chat.models.message.MessageService;
import com.zen_vy.chat.models.message.entity.MessageEntry;
import com.zen_vy.chat.util.SharedPreferencesUtil;
import com.zen_vy.chat.websocket.WebSocketService;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

public class MainActivity extends BaseActivity {

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.nav_menu, menu);
      return super.onCreateOptionsMenu(menu);
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      if (currentUser == null) {
         navigateToLogin();
         return;
      }
      setContentView(R.layout.activity_main);

      _initView();

      _setBottomNavMenu();

      conversationService = new ConversationService(this, currentUser);

      deviceService = new DeviceService(this, currentUser);

      mainAdapter = new MainAdapter(this, currentUser, messageBoardRecView);

      _startingWebSocketService();

      MessageService messageService = new MessageService(this, currentUser);
      messageService.getPendingMessages();

      LocalBroadcastManager
         .getInstance(this)
         .registerReceiver(
            broadcastReceiver,
            new IntentFilter(
               "com.example.szakdolg.models.message.entity.MessageBroadCast"
            )
         );
   }

   @Override
   protected void onPause() {
      super.onPause();
      LocalBroadcastManager
         .getInstance(this)
         .unregisterReceiver(broadcastReceiver);
   }

   @Override
   protected void onResume() {
      super.onResume();
      LocalBroadcastManager
         .getInstance(this)
         .registerReceiver(
            broadcastReceiver,
            new IntentFilter(
               "com.example.szakdolg.models.message.entity.MessageBroadCast"
            )
         );
   }

   @Override
   public void onRequestPermissionsResult(
      int requestCode,
      @NonNull String[] permissions,
      @NonNull int[] grantResults
   ) {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
   }

   @Override
   protected void onStart() {
      super.onStart();

      _setListeners();

      _setNightMode();

      _sendFCMDeviceToken();

      conversationService.getAllConversations(
         new ConversationService.ConversationCallback<List<ConversationDTO>>() {
            @Override
            public void onSuccess(List<ConversationDTO> conversationList) {
               runOnUiThread(() -> {
                  if (conversationList != null && !conversationList.isEmpty()) {
                     conversationDTOList.addAll(conversationList);
                     _validateConversation(conversationDTOList);
                     emptyLayout.setVisibility(View.GONE);
                     withItemsLayout.setVisibility(View.VISIBLE);
                     mainAdapter.setConversationList(conversationDTOList);
                  } else {
                     emptyLayout.setVisibility(View.VISIBLE);
                     withItemsLayout.setVisibility(View.GONE);
                  }
               });
            }

            @Override
            public void onError(Throwable t) {
               Timber.e(t);
               runOnUiThread(() -> {
                  emptyLayout.setVisibility(View.VISIBLE);
                  withItemsLayout.setVisibility(View.GONE);
               });
            }
         }
      );

      messageBoardRecView.setAdapter(mainAdapter);
      messageBoardRecView.setLayoutManager(
         new LinearLayoutManager(MainActivity.this)
      );
      //set menu text and image logo

      profileTextHeader.setText(currentUser.getDisplayName());

      String imageUrl = ImageUtil.buildProfileImageUrl(currentUser.getUserId());

      if (imageUrl != null) {
         GlideUrl glideUrl = new GlideUrl(
            imageUrl,
            new LazyHeaders.Builder()
               .addHeader("Authorization", currentUser.getToken())
               .build()
         );

         Glide
            .with(this)
            .load(glideUrl)
            .placeholder(R.drawable.ic_blank_profile)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .error(R.drawable.ic_blank_profile)
            .into(profileImageHeader);
      } else {
         profileImageHeader.setImageResource(R.drawable.ic_blank_profile);
      }
   }

   private void _sendFCMDeviceToken() {
      FirebaseMessaging
         .getInstance()
         .getToken()
         .addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
               Timber.w(
                  task.getException(),
                  "Fetching FCM registration token failed"
               );
            }
            String fcmToken = task.getResult();

            Device device = new Device(currentUser.getUserId(), fcmToken);

            deviceService.addDevice(
               device,
               new DeviceService.DeviceCallback<Device>() {
                  @Override
                  public void onSuccess(Device data) {
                     Timber.i("Fetching FCM registration token is success");
                  }

                  @Override
                  public void onError(Throwable t) {}
               }
            );
         });
   }

   private void _setNightMode() {
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
   }

   private void _initView() {
      navigationView = findViewById(R.id.navigation_view_main);
      drawerLayout = findViewById(R.id.drawerLayoutMain);
      topAppBar = findViewById(R.id.topAppBar);
      messageBoardRecView = findViewById(R.id.messageBoardRecView);
      bottomNavigationView = findViewById(R.id.bottom_nav_main);
      emptyLayout = findViewById(R.id.llayoutEmptyMain);
      withItemsLayout = findViewById(R.id.llayoutWithItemsMain);

      newConv = findViewById(R.id.btnNewConv);
      MenuItem themeItem = navigationView
         .getMenu()
         .findItem(R.id.main_dark_theme);
      switchTheme = themeItem.getActionView().findViewById(R.id.switch_item);

      headerView = navigationView.getHeaderView(0); // Get the header view

      profileTextHeader = headerView.findViewById(R.id.profile_name_header);
      profileImageHeader = headerView.findViewById(R.id.profile_image_header);
   }

   public void _setBottomNavMenu() {
      bottomNavigationView.setOnItemSelectedListener(
         new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
               Intent intent;
               switch (item.getItemId()) {
                  case R.id.nav_messages_main:
                     intent = new Intent(MainActivity.this, MainActivity.class);
                     startActivity(intent);
                     break;
                  case R.id.nav_contact_main:
                     intent =
                     new Intent(MainActivity.this, ContactsActivity.class);
                     intent.putExtra(
                        IntentConstants.CONTACTS_ACTION,
                        ContactsConstans.ACTION_VIEW
                     );
                     startActivity(intent);

                     break;
               }
               return false;
            }
         }
      );
   }

   public void _setListeners() {
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

      profileImageHeader.setOnClickListener(
         new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(
                  MainActivity.this,
                  ProfileActivity.class
               );
               intent.putExtra(
                  IntentConstants.PROFILE_ACTION,
                  ProfileConstants.VIEW_PROFILE
               );
               startActivity(intent);
            }
         }
      );
      navigationView.setNavigationItemSelectedListener(
         new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
               switch (item.getItemId()) {
                  case R.id.main_sign_out:
                     SharedPreferencesUtil.deletePreference(
                        MainActivity.this,
                        SharedPreferencesConstants.USERTOKEN
                     );

                     SharedPreferencesUtil.deletePreference(
                        MainActivity.this,
                        SharedPreferencesConstants.USER_ID
                     );
                     MainActivity.this.startActivity(
                           new Intent(MainActivity.this, MainActivity.class)
                        );
                  case R.id.main_dark_theme:
                     AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_NO
                     );
                  default:
                     return false;
               }
            }
         }
      );

      topAppBar.setNavigationOnClickListener(
         new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                  drawerLayout.closeDrawer(GravityCompat.START);
               } else {
                  drawerLayout.openDrawer(GravityCompat.START);
               }
            }
         }
      );

      newConv.setOnClickListener(
         new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(
                  MainActivity.this,
                  ContactsActivity.class
               );
               intent.putExtra(
                  IntentConstants.CONTACTS_ACTION,
                  ContactsConstans.ACTION_SELECT
               );
               MainActivity.this.startActivity(intent);
            }
         }
      );

      topAppBar.setOnMenuItemClickListener(
         new MaterialToolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
               switch (item.getItemId()) {
                  case R.id.menuNewMain:
                     Intent intent = new Intent(
                        MainActivity.this,
                        ContactsActivity.class
                     );
                     intent.putExtra(
                        IntentConstants.CONTACTS_ACTION,
                        ContactsConstans.ACTION_SELECT
                     );
                     MainActivity.this.startActivity(intent);
                     return true; // Indicate that the click was handled
                  default:
                     return false;
               }
            }
         }
      );
   }

   private void _startingWebSocketService() {
      Intent serviceIntent = new Intent(this, WebSocketService.class);
      serviceIntent.putExtra(IntentConstants.CURRENT_USER, currentUser);
      serviceIntent.putExtra(IntentConstants.USER_TOKEN, token);
      startService(serviceIntent);
   }

   private void _validateConversation(List<ConversationDTO> conversationList) {
      conversationList.removeIf(conversationDTO ->
         conversationDTO.getConversation() == null ||
         conversationDTO.getUsers() == null ||
         conversationDTO.getParticipants() == null ||
         conversationDTO.getMessageEntry() == null
      );
   }

   private BottomNavigationView bottomNavigationView;
   private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
         MessageEntry message = (MessageEntry) intent.getSerializableExtra(
            "message"
         );

         conversationService.getConversation(
            message.getConversationId(),
            new ConversationService.ConversationCallback<ConversationDTO>() {
               @Override
               public void onSuccess(ConversationDTO conversation) {
                  conversation.setMessageEntry(message);
                  runOnUiThread(() -> {
                     if (mainAdapter.getItemCount() == 0) {
                        conversationDTOList.add(conversation);
                        _validateConversation(conversationDTOList);

                        emptyLayout.setVisibility(View.GONE);
                        withItemsLayout.setVisibility(View.VISIBLE);

                        mainAdapter.setConversationList(conversationDTOList);
                     } else {
                        mainAdapter.updateConversationDTO(conversation);
                     }
                  });
               }

               @Override
               public void onError(Throwable t) {}
            }
         );
      }
   };

   public void setConversationService(ConversationService service) {
      this.conversationService = service;
   }

   private List<ConversationDTO> conversationDTOList = new ArrayList<>();

   private DeviceService deviceService;
   private DrawerLayout drawerLayout;
   private LinearLayout emptyLayout;
   private MainAdapter mainAdapter;
   private RecyclerView messageBoardRecView;
   private NavigationView navigationView;

   private LinearLayout withItemsLayout;
   private MaterialToolbar topAppBar;
   private ImageView profileImageHeader;
   private TextView profileTextHeader;

   private ConversationService conversationService;

   private FloatingActionButton newConv;

   private View headerView;

   private SwitchCompat switchTheme;
}
