package com.example.szakdolg.models.user.service;

import android.content.Context;
import com.example.szakdolg.db.util.UserDatabaseUtil;
import com.example.szakdolg.models.user.entity.User;
import java.util.List;

public class UserService {

   private Context context;

   public UserService(Context context) {
      this.context = context;
   }

   public User addUser(User user, User currentUser) {
      User newUser = new User();
      newUser.setUserId(user.getUserId());
      newUser.setDisplayName(user.getDisplayName());
      newUser.setEmail(user.getEmail());
      newUser.setPublicKey(user.getPublicKey());
      newUser.setProfilePictureUuid(user.getProfilePictureUuid());
      newUser.setStatus(user.getStatus());
      newUser.setTags(user.getTags());
      newUser.setAuthToken(user.getAuthToken());

      UserDatabaseUtil userDatabaseUtil = new UserDatabaseUtil(
         context,
         currentUser
      );
      userDatabaseUtil.insertUser(newUser);
      return newUser;
   }

   public User getUserByUserId(Long userId, User currentUser) {
      UserDatabaseUtil userDatabaseUtil = new UserDatabaseUtil(
         context,
         currentUser
      );
      return userDatabaseUtil.getUserById(userId);
   }

   public void updateUser(User user, User currentUser) {
      if (user == null || user.getUserId() == null) {
         throw new IllegalArgumentException("User or User ID cannot be null");
      }
      UserDatabaseUtil userDatabaseUtil = new UserDatabaseUtil(
         context,
         currentUser
      );
      userDatabaseUtil.updateUser(user);
   }

   public List<User> getAllUser(User currentUser) {
      UserDatabaseUtil userDatabaseUtil = new UserDatabaseUtil(
         context,
         currentUser
      );
      return userDatabaseUtil.getAllUsers();
   }
}
