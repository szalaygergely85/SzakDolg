package com.example.szakdolg.activity.main;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.szakdolg.R;
import com.example.szakdolg.activity.contacts.activity.ContactsActivity;
import com.example.szakdolg.activity.main.adapter.MainAdapter;
import com.example.szakdolg.constans.SharedPreferencesConstants;
import com.example.szakdolg.models.conversation.ConversationCoordinatorService;
import com.example.szakdolg.models.conversation.entity.Conversation;
import com.example.szakdolg.models.user.api.ContactsApiHelper;
import com.example.szakdolg.models.user.entity.User;
import com.example.szakdolg.util.SharedPreferencesUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import java.util.List;

public class MainActivityHelper {

   private Context _context;
   private ContactsApiHelper _contactsApiHelper = new ContactsApiHelper();
   private String token;
   private User currentUser;
   private ConversationCoordinatorService conversationCoordinatorService;

   public MainActivityHelper(Context _context, String token, User currentUser) {
      this._context = _context;
      this.token = token;
      this.currentUser = currentUser;
      this.conversationCoordinatorService =
      new ConversationCoordinatorService(_context, currentUser);
   }

   public void setMessageBoard(RecyclerView messageBoardRecView) {
      MainAdapter mainAdapter = new MainAdapter(_context, currentUser);

      List<Conversation> conversationList =
         conversationCoordinatorService.getAllConversations(null);
      if (conversationList != null) {
         mainAdapter.setConversationList(conversationList);
      }
      messageBoardRecView.setAdapter(mainAdapter);
      messageBoardRecView.setLayoutManager(new LinearLayoutManager(_context));
   }

   public void startCacheChecking() {
      long cacheExpireTimeMillis = SharedPreferencesUtil.getLongPreference(
         _context,
         SharedPreferencesConstants.CACHE_EXPIRE
      );

      if (_isCacheExpired(cacheExpireTimeMillis)) {
         new Thread(
            new Runnable() {
               @Override
               public void run() {
                  /*  _messageApiHelper.checkCachedMessages(
					token,
					_context,
					currentUser
				);*/
                  _contactsApiHelper.checkCachedContacts(
                     token,
                     _context,
                     currentUser
                  );
                  /* _conversationApiHelper.checkCachedConversation(
					token,
					_context,
					currentUser
				);*/
                  /* _conversationApiHelper.checkCachedConversationParticipant(
					token,
					_context,
					currentUser
				);*/
               }
            }
         )
            .start();
      }
   }
/*
   public void setNavMenu(BottomNavigationView bottomNavigationView) {
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
                     intent = new Intent(_context, ContactsActivity.class);
                     _context.startActivity(intent);
                     break;
               }
               return false;
            }
         }
      );
   }
*/
   private boolean _isCacheExpired(long cacheExpireTimeMillis) {
      if (cacheExpireTimeMillis == -1) {
         return true;
      }

      long currentTimeMillis = System.currentTimeMillis();
      return currentTimeMillis > cacheExpireTimeMillis;
   }
}
