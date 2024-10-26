package com.example.szakdolg.main.helper;

import android.content.Context;

import com.example.szakdolg.constans.SharedPreferencesConstants;
import com.example.szakdolg.contacts.helper.ContactsApiHelper;
import com.example.szakdolg.model.conversation.ConversationApiHelper;
import com.example.szakdolg.model.message.api.MessageApiHelper;
import com.example.szakdolg.model.user.api.UserApiHelper;
import com.example.szakdolg.model.user.model.User;
import com.example.szakdolg.util.SharedPreferencesUtil;

public class MainActivityHelper {

   private Context _context;
   private UserApiHelper _userApiHelper = new UserApiHelper();
   private MessageApiHelper _messageApiHelper = new MessageApiHelper();
   private ContactsApiHelper _contactsApiHelper = new ContactsApiHelper();
   private ConversationApiHelper _conversationApiHelper =
      new ConversationApiHelper();
   private String token;
   private String userId;


   public MainActivityHelper(Context _context, String token, String userId) {
      this._context = _context;
      this.token = token;
      this.userId = userId;
   }

   public void startCacheChecking(String token, User currentUser) {
      long cacheExpireTimeMillis = SharedPreferencesUtil.getLongPreference(
         _context,
         SharedPreferencesConstants.CACHE_EXPIRE
      );

      if (_isCacheExpired(cacheExpireTimeMillis)) {
         new Thread(
            new Runnable() {
               @Override
               public void run() {
                  _messageApiHelper.checkCachedMessages(token, _context, currentUser);
                  _contactsApiHelper.checkCachedContacts(token, _context, currentUser);
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

   private boolean _isCacheExpired(long cacheExpireTimeMillis) {
      if (cacheExpireTimeMillis == -1) {
         return true;
      }

      long currentTimeMillis = System.currentTimeMillis();
      return currentTimeMillis > cacheExpireTimeMillis;
   }

}
