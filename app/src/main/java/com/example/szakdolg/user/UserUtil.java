package com.example.szakdolg.user;

import com.example.szakdolg.user.entity.User;
import java.util.List;

public class UserUtil {

   public static User removeCurrentUserFromList(List<User> users, Long id) {
      for (User user : users) {
         if (!user.getUserId().equals(id)) {
            return user;
         }
      }
      return null; // or throw an exception, or return an Optional<User>
   }
}
