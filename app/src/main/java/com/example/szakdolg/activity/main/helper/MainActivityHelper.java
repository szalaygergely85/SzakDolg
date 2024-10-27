package com.example.szakdolg.activity.main.helper;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.szakdolg.R;
import com.example.szakdolg.constans.SharedPreferencesConstants;
import com.example.szakdolg.contacts.activity.ContactsActivity;
import com.example.szakdolg.contacts.helper.ContactsApiHelper;
import com.example.szakdolg.db.util.ConversationDatabaseUtil;
import com.example.szakdolg.messageboard.activity.MessageBoardActivity;
import com.example.szakdolg.messageboard.adapter.MessageBoardAdapter;
import com.example.szakdolg.model.conversation.ConversationApiHelper;
import com.example.szakdolg.model.conversation.entity.Conversation;
import com.example.szakdolg.model.conversation.service.ConversationService;
import com.example.szakdolg.model.message.api.MessageApiHelper;
import com.example.szakdolg.model.user.api.UserApiHelper;
import com.example.szakdolg.model.user.entity.User;
import com.example.szakdolg.util.SharedPreferencesUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class MainActivityHelper {

   private Context _context;
   private UserApiHelper _userApiHelper = new UserApiHelper();
   private MessageApiHelper _messageApiHelper = new MessageApiHelper();
   private ContactsApiHelper _contactsApiHelper = new ContactsApiHelper();
   private ConversationApiHelper _conversationApiHelper =
      new ConversationApiHelper();
   private String token;
   private User currentUser;
   private ConversationService conversationService;
   public MainActivityHelper(Context _context, String token, User currentUser) {
      this._context = _context;
      this.token = token;
      this.currentUser = currentUser;
      this.conversationService =
              new ConversationService(_context, currentUser);
   }

   public void setMessageBoard(RecyclerView messageBoardRecView){

      MessageBoardAdapter messageBoardAdapter =
              new MessageBoardAdapter(_context, token, currentUser);

      List<Conversation> conversationList = conversationService.getAllConversations();

      messageBoardAdapter.setConversationList(conversationList);
      messageBoardRecView.setAdapter(messageBoardAdapter);
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
                  _messageApiHelper.checkCachedMessages(
                     token,
                     _context,
                     currentUser
                  );
                  _contactsApiHelper.checkCachedContacts(
                     token,
                     _context,
                     currentUser
                  );
                  _conversationApiHelper.checkCachedConversation(
                     token,
                     _context,
                     currentUser
                  );
                  _conversationApiHelper.checkCachedConversationParticipant(
                     token,
                     _context,
                     currentUser
                  );
               }
            }
         )
            .start();
      }
   }

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
                          intent =
                                  new Intent(
                                          _context,
                                          ContactsActivity.class
                                  );
                          _context.startActivity(intent);
                          break;
                    }
                    return false;
                 }
              }
      );
   }

   private boolean _isCacheExpired(long cacheExpireTimeMillis) {
      if (cacheExpireTimeMillis == -1) {
         return true;
      }

      long currentTimeMillis = System.currentTimeMillis();
      return currentTimeMillis > cacheExpireTimeMillis;
   }
}
