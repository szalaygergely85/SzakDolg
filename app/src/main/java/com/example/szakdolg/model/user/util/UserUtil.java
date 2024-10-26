package com.example.szakdolg.model.user.util;

import com.example.szakdolg.model.user.model.User;

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
      for (User user : users) {
         if (!user.getUserId().equals(id)) {
            newUserList.add(user);
         }
      }
      return newUserList;
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
