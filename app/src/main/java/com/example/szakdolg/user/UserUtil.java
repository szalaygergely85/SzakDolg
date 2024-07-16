package com.example.szakdolg.user;

import com.example.szakdolg.user.entity.User;
import java.util.ArrayList;
import java.util.List;

public class UserUtil {

   public static User removeCurrentUserFromList(List<User> users, Long id) {
      for (User user : users) {
         if (!user.getUserId().equals(id)) {
            return user;
         }
      }
      return null;
   }

   public static User getUserByID(List<User> users, Long id) {
      for (User user : users) {
         if (user.getUserId().equals(id)) {
            return user;
         }
      }
      return null;
   }

   public static List<String> getDisplayNames(List<User> users) {
      List<String> displayNames = new ArrayList<>();
      for (User user : users) {
         displayNames.add(user.getDisplayName());
      }
      return displayNames;
   }
}
