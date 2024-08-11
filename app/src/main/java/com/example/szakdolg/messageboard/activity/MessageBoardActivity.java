package com.example.szakdolg.messageboard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import com.example.szakdolg.R;
import com.example.szakdolg.activity.ContactsActivity;
import com.example.szakdolg.activity.ProfileActivity;
import com.example.szakdolg.chat.activity.NewChatActivity;
import com.example.szakdolg.constans.IntentConstans;
import com.example.szakdolg.constans.SharedPreferencesConstans;
import com.example.szakdolg.conversation.entity.Conversation;
import com.example.szakdolg.db.util.ConversationDatabaseUtil;
import com.example.szakdolg.db.util.ProfileDatabaseUtil;
import com.example.szakdolg.message.MessageApiHelper;
import com.example.szakdolg.messageboard.DTO.MessageBoard;
import com.example.szakdolg.messageboard.adapter.MessageBoardAdapter;
import com.example.szakdolg.notification.MessageWorker;
import com.example.szakdolg.user.entity.User;
import com.example.szakdolg.util.SharedPreferencesUtil;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MessageBoardActivity extends AppCompatActivity {

   private static final String TAG = "MessageB";
   private FloatingActionButton contactsButton;
   private RecyclerView messageBoardRecView;
   private MessageBoardAdapter messageBoardAdapter;
   private MaterialToolbar mToolbar;
   private User _currentUser;
   ArrayList<MessageBoard> messageBoard = new ArrayList<>();

   private MessageApiHelper messageApiHelper = new MessageApiHelper();

   private String userToken;

   private Handler handler = new Handler();
   private Runnable runnable;

   private Gson gson = new Gson();
   List<Conversation> conversationList = new ArrayList<>();

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_message_board_actvitiy);

      _initView();

      setNavMenu();

      userToken =
      SharedPreferencesUtil.getStringPreference(
         this,
         SharedPreferencesConstans.USERTOKEN
      );

      ProfileDatabaseUtil profileDatabaseUtil = new ProfileDatabaseUtil(this);
      _currentUser =
              profileDatabaseUtil.getCurrentUserByToken(userToken);

      _scheduleMessageWorker();

      mToolbar = (MaterialToolbar) findViewById(R.id.messageBoardToolbar);
      setSupportActionBar(mToolbar);

      messageBoardRecView = findViewById(R.id.messageBoardRecView);

      messageBoardAdapter = new MessageBoardAdapter(this, userToken);

      ConversationDatabaseUtil conversationDatabaseUtil = new ConversationDatabaseUtil(this);

      conversationList = conversationDatabaseUtil.getAllConversations();

      messageBoardAdapter.setConversationList(conversationList);

      messageBoardAdapter.setCurrentUser(_currentUser);

      messageBoardRecView.setAdapter(messageBoardAdapter);
      messageBoardRecView.setLayoutManager(new LinearLayoutManager(this));


   }

   @Override
   protected void onStart() {
      super.onStart();
       _startRepeatingTask();

      contactsButton.setOnClickListener(
         new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(
                  MessageBoardActivity.this,
                  NewChatActivity.class
               );
               intent.putExtra(
                  SharedPreferencesConstans.CURRENT_USER,
                  _currentUser
               );
               startActivity(intent);
            }
         }
      );
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.nav_menu, menu);
      return super.onCreateOptionsMenu(menu);
   }

   @Override
   public boolean onOptionsItemSelected(@NonNull MenuItem item) {
      Intent intent;
      switch (item.getItemId()) {
         case R.id.menuProfile:
            intent =
            new Intent(MessageBoardActivity.this, ProfileActivity.class);
            intent.putExtra(
               SharedPreferencesConstans.CURRENT_USER,
               _currentUser
            );
            startActivity(intent);
            break;
         case R.id.menuContacts:
            intent =
            new Intent(MessageBoardActivity.this, ContactsActivity.class);
            intent.putExtra(
               SharedPreferencesConstans.CURRENT_USER,
               _currentUser
            );
            startActivity(intent);
            break;
         case R.id.menuSingOut:
         default:
            break;
      }
      return false;
   }

   @Override
   protected void onDestroy() {
      super.onDestroy();
       _stopRepeatingTask();
   }

   private void _initView() {
      contactsButton = findViewById(R.id.btnMesBrdNew);
   }

   private void _startRepeatingTask() {
      runnable =
      new Runnable() {
         @Override
         public void run() {
            try {
               messageApiHelper.getNewMessages(MessageBoardActivity.this, userToken
               );
            } finally {
               handler.postDelayed(runnable, 15000);
            }
         }
      };

      runnable.run();
   }

   private void _stopRepeatingTask() {
      handler.removeCallbacks(runnable);
   }

   private void setNavMenu() {
      BottomNavigationView bottomNavigationView = findViewById(
         R.id.bottom_navigation
      );

      bottomNavigationView.setOnItemSelectedListener(
         new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
               Intent intent;
               switch (item.getItemId()) {
                  case R.id.navigation_messages:
                     // Handle home navigation
                     return true;
                  case R.id.navigation_group:
                     // Handle dashboard navigation
                     return true;
                  case R.id.navigation_chat:
                     // Handle notifications navigation
                     return true;
                  case R.id.navigation_contact:
                     Log.e("Ajaj", "ajaj");
                     intent =
                     new Intent(
                        MessageBoardActivity.this,
                        ContactsActivity.class
                     );
                     intent.putExtra(
                        SharedPreferencesConstans.CURRENT_USER,
                        _currentUser
                     );
                     startActivity(intent);
                     break;
               }
               return false;
            }
         }
      );
   }

   private void _scheduleMessageWorker() {
      Constraints constraints = new Constraints.Builder()
         .setRequiredNetworkType(NetworkType.CONNECTED)
         .setRequiresCharging(false)
         .build();

      String currentUserJson = gson.toJson(_currentUser);

      Data inputData = new Data.Builder()
         .putString(IntentConstans.CURRENT_USER, currentUserJson)
         .build();

      PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
         MessageWorker.class,
         16,
         TimeUnit.MINUTES
      )
         .setInputData(inputData)
         .setConstraints(constraints)
         .build();

      WorkManager
         .getInstance(this)
         .enqueueUniquePeriodicWork(
            "MessageWorker",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
         );
   }
}
