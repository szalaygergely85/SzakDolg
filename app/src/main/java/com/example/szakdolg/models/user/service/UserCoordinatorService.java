package com.example.szakdolg.models.user.service;

import android.content.Context;
import android.content.Intent;

import com.example.szakdolg.activity.base.BaseService;
import com.example.szakdolg.activity.profilepicture.ProfilePictureActivity;
import com.example.szakdolg.activity.register.RegisterCallBack;
import com.example.szakdolg.constans.SharedPreferencesConstants;
import com.example.szakdolg.models.conversation.entity.ConversationParticipant;
import com.example.szakdolg.models.user.api.UserApiHelper;
import com.example.szakdolg.models.user.entity.User;
import com.example.szakdolg.models.user.entity.UserToken;
import com.example.szakdolg.models.user.util.UserUtil;
import com.example.szakdolg.util.KeyStoreUtil;
import com.example.szakdolg.util.SharedPreferencesUtil;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserCoordinatorService{

   private UserService userService;
   private UserApiHelper userApiHelper;

   private Context context;

   public UserCoordinatorService(Context context) {
       this.context = context;
      this.userService = new UserService(context);
      this.userApiHelper = new UserApiHelper();
   }

   public void registerUser(User user, HashMap<String, String> keyPair){
      userApiHelper.addUser(
              context,
              user,
              userToken -> {
                 user.setAuthToken(userToken.getToken());
                 user.setUserId(userToken.getUserId());

                 userService.addUser(user, user);

                 KeyStoreUtil.writePrivateKeysToFile(
                         context,
                         keyPair.get("Private"),
                         user
                 );

                 SharedPreferencesUtil.setStringPreference(
                         context,
                         SharedPreferencesConstants.USERTOKEN,
                         user.getAuthToken()
                 );
                 SharedPreferencesUtil.setStringPreference(
                         context,
                         SharedPreferencesConstants.USER_ID,
                         user.getUserId().toString()
                 );
                  Intent intent = new Intent(
                          context,
                          ProfilePictureActivity.class
                  );
                  context.startActivity(intent);

              }
      );
   }

   public User getUserByUserId(Long userId, User currentUser) {
      User user = userService.getUserByUserId(userId, currentUser);
      if (user != null) {
         return user;
      } else {
         userApiHelper.getUserByUserID(
            userId,
            currentUser.getAuthToken(),
            user1 -> userService.addUser(user1, currentUser)
         );
         return null;
      }
   }
    public List<User> getConversationParticipantUser(List<ConversationParticipant> conversationParticipants, User currentUser){
       List<User> users = new ArrayList<>();
       for (ConversationParticipant conversationParticipant:conversationParticipants){
           User user = getUserByUserId(conversationParticipant.getUserId(), currentUser);
               if(user!=null){
                   users.add(user);
               }
           }
       return users;
    }


   public List<User> getContacts(User currentUser) {
      List<User> contacts = UserUtil.removeCurrentUserFromList(
         userService.getAllUser(currentUser),
         currentUser.getUserId()
      );
      return contacts;
   }
}
