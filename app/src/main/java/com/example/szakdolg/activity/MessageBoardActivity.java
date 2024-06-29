package com.example.szakdolg.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.szakdolg.DTO.MessageBoard;
import com.example.szakdolg.R;
import com.example.szakdolg.adapter.MessageBoardAdapter;
import com.example.szakdolg.constans.SharedPreferencesConstans;
import com.example.szakdolg.message.MessageApiHelper;
import com.example.szakdolg.user.entity.User;
import com.example.szakdolg.util.SharedPreferencesUtil;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

public class MessageBoardActivity extends AppCompatActivity {

   private static final String TAG = "MessageB";
   private FloatingActionButton contactsButton;
   private RecyclerView messageBoardRecView;
   private MessageBoardAdapter adapter;
   private MaterialToolbar mToolbar;
   private User loggedUser;
   ArrayList<MessageBoard> messageBoard = new ArrayList<>();

   private MessageApiHelper messageApiHelper = new MessageApiHelper();

   private String userToken;

   private Handler handler = new Handler();
   private Runnable runnable;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_message_board_actvitiy);

      _initView();

      loggedUser =
      (User) this.getIntent()
         .getSerializableExtra(SharedPreferencesConstans.CURRENT_USER);
      userToken =
      SharedPreferencesUtil.getStringPreference(
         this,
         SharedPreferencesConstans.USERTOKEN
      );

      mToolbar = (MaterialToolbar) findViewById(R.id.messageBoardToolbar);
      setSupportActionBar(mToolbar);

      messageBoardRecView = findViewById(R.id.messageBoardRecView);

      adapter = new MessageBoardAdapter(this, userToken);
      adapter.setMessageB(messageBoard);
      adapter.setCurrentUser(loggedUser);

      messageBoardRecView.setAdapter(adapter);
      messageBoardRecView.setLayoutManager(new LinearLayoutManager(this));
   }

   @Override
   protected void onStart() {
      super.onStart();
      //  _startRepeatingTask();
      //messageApiHelper.getLatestMessages(adapter,this, userToken, loggedUser);

      contactsButton.setOnClickListener(
         new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(
                  MessageBoardActivity.this,
                  ContactsActivity.class
               );
               intent.putExtra(
                  SharedPreferencesConstans.CURRENT_USER,
                  loggedUser
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
            intent.putExtra(SharedPreferencesConstans.CURRENT_USER, loggedUser);
            startActivity(intent);
            break;
         case R.id.menuContacts:
            intent =
            new Intent(MessageBoardActivity.this, ContactsActivity.class);
            intent.putExtra(SharedPreferencesConstans.CURRENT_USER, loggedUser);
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
      // _stopRepeatingTask();
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
               messageApiHelper.getLatestMessages(
                  adapter,
                  MessageBoardActivity.this,
                  userToken,
                  loggedUser
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
}
