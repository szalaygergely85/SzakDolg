package com.example.szakdolg.models.user.util;

import com.example.szakdolg.models.user.entity.User;
import java.util.ArrayList;
import java.util.List;

public class UserUtil {

   /*
@Deprecated
public static User removeCurrentUserFromList(List<User> users, Long id) {
	for (User user : users) {
		if (!user.getUserId().equals(id)) {
			return user;
		}
	}
	return null;
}
*/
   public static List<User> removeCurrentUserFromList(
      List<User> users,
      Long id
   ) {
      List<User> newUserList = new ArrayList<>();
      if(!users.isEmpty()){
      for (User user : users) {
         if (!user.getUserId().equals(id)) {
            newUserList.add(user);
         }
      }
      }
      return newUserList;
   }

   public static void removeCurrentUser(
           List<User> users,
           Long id
   ) {

      if(!users.isEmpty()){
         for (User user : users) {
            if (user.getUserId().equals(id)) {
               users.remove(user);
            }
         }
      }
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
