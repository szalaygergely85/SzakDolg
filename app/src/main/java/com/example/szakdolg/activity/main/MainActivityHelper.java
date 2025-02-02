package com.example.szakdolg.activity.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.szakdolg.R;
import com.example.szakdolg.activity.chat.activity.NewChatActivity;
import com.example.szakdolg.activity.contacts.activity.ContactsActivity;
import com.example.szakdolg.activity.main.adapter.MainAdapter;
import com.example.szakdolg.constans.SharedPreferencesConstants;
import com.example.szakdolg.models.conversation.ConversationCoordinatorService;
import com.example.szakdolg.models.conversation.entity.Conversation;
import com.example.szakdolg.models.message.entity.MessageEntry;
import com.example.szakdolg.models.user.entity.User;
import com.example.szakdolg.util.SharedPreferencesUtil;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import java.util.List;

public class MainActivityHelper {

   private final Context _context;

   private final User currentUser;
   private final ConversationCoordinatorService conversationCoordinatorService;

   public MainActivityHelper(Context _context, User currentUser) {
      this._context = _context;
      this.currentUser = currentUser;
      this.conversationCoordinatorService =
      new ConversationCoordinatorService(_context, currentUser);
   }

   public List<Conversation> getConversationList() {
      return conversationCoordinatorService.getAllValidConversations(null);
   }


   public void setBottomNavMenu(BottomNavigationView bottomNavigationView) {
      bottomNavigationView.setOnItemSelectedListener(
         new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
               Intent intent;
               switch (item.getItemId()) {
                  case R.id.nav_messages_main:
                     intent = new Intent(_context, MainActivity.class);
                     _context.startActivity(intent);
                     break;
                  case R.id.nav_contact_main:
                     intent = new Intent(_context, ContactsActivity.class);
                     _context.startActivity(intent);
                     break;
               }
               return false;
            }
         }
      );
   }

   public void setListeners(
      MaterialToolbar topAppBar,
      DrawerLayout drawerLayout,
      NavigationView navigationView
   ) {
      navigationView.setNavigationItemSelectedListener(
         new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
               switch (item.getItemId()) {
                  case R.id.main_sign_out:
                     SharedPreferencesUtil.deletePreference(
                        _context,
                        SharedPreferencesConstants.USERTOKEN
                     );
                     SharedPreferencesUtil.deletePreference(
                        _context,
                        SharedPreferencesConstants.CURRENT_USER
                     );
                     _context.startActivity(
                        new Intent(_context, MainActivity.class)
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

      topAppBar.setOnMenuItemClickListener(
         new MaterialToolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
               switch (item.getItemId()) {
                  case R.id.menuNewMain:
                     _context.startActivity(
                        new Intent(_context, NewChatActivity.class)
                     );
                     return true; // Indicate that the click was handled
                  default:
                     return false;
               }
            }
         }
      );
   }

   public Conversation getConversation(MessageEntry message) {
      return conversationCoordinatorService.getConversation(message.getConversationId());
   }
}
