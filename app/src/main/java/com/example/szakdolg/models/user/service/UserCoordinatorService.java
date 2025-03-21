package com.example.szakdolg.models.user.service;

import android.content.Context;
import android.content.Intent;
import com.example.szakdolg.activity.profilepicture.ProfilePictureActivity;
import com.example.szakdolg.constans.SharedPreferencesConstants;
import com.example.szakdolg.models.conversation.entity.ConversationParticipant;
import com.example.szakdolg.models.user.api.UserApiHelper;
import com.example.szakdolg.models.user.entity.User;
import com.example.szakdolg.models.user.util.UserUtil;
import com.example.szakdolg.util.KeyStoreUtil;
import com.example.szakdolg.util.SharedPreferencesUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserCoordinatorService {

   private UserService userService;
   private UserApiHelper userApiHelper;

   private Context context;

   public UserCoordinatorService(Context context) {
      this.context = context;
      this.userService = new UserService(context);
      this.userApiHelper = new UserApiHelper();
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

   public List<User> getConversationParticipantUser(
      List<ConversationParticipant> conversationParticipants,
      User currentUser
   ) {
      List<User> users = new ArrayList<>();
      if (conversationParticipants != null) {
         for (ConversationParticipant conversationParticipant : conversationParticipants) {
            User user = getUserByUserId(
               conversationParticipant.getUserId(),
               currentUser
            );
            if (user != null) {
               users.add(user);
            }
         }
         return users;
      } else {
         return null;
      }
   }

   public List<User> getContacts(User currentUser) {
      List<User> contacts = UserUtil.removeCurrentUserFromList(
         userService.getAllUser(currentUser),
         currentUser.getUserId()
      );
      return contacts;
   }
}
