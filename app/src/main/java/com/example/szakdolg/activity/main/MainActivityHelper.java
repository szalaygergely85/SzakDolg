package com.example.szakdolg.activity.main;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import com.example.szakdolg.R;
import com.example.szakdolg.activity.contacts.activity.ContactsActivity;
import com.example.szakdolg.models.conversation.ConversationCoordinatorService;
import com.example.szakdolg.models.conversation.entity.Conversation;
import com.example.szakdolg.models.user.entity.User;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
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
      return conversationCoordinatorService.getAllConversations(null);
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

   public void setTopBarMenu(MaterialToolbar topAppBar) {
      topAppBar.setOnMenuItemClickListener(
         new MaterialToolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
               switch (item.getItemId()) {
                  case R.id.menuNewMain:
                     _context.startActivity(
                        new Intent(_context, ContactsActivity.class)
                     );
                     return true; // Indicate that the click was handled
                  default:
                     return false;
               }
            }
         }
      );
   }
}
