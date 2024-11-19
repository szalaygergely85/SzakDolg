package com.example.szakdolg.models.user.service;

import android.content.Context;
import com.example.szakdolg.activity.base.BaseService;
import com.example.szakdolg.models.user.api.UserApiHelper;
import com.example.szakdolg.models.user.entity.User;
import com.example.szakdolg.models.user.util.UserUtil;
import java.util.List;

public class UserCoordinatorService extends BaseService {

   private UserService userService;
   private UserApiHelper userApiHelper;

   public UserCoordinatorService(Context context, User currentUser) {
      super(context, currentUser);
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

   public List<User> getContacts() {
      List<User> contacts = UserUtil.removeCurrentUserFromList(
         userService.getAllUser(currentUser),
         currentUser.getUserId()
      );
      return contacts;
   }
}
