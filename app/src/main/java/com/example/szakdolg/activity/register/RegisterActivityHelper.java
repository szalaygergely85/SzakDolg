package com.example.szakdolg.activity.register;

import android.content.Context;
import com.example.szakdolg.models.user.api.UserApiHelper;
import com.example.szakdolg.models.user.constans.UserConstans;
import com.example.szakdolg.models.user.entity.User;
import com.example.szakdolg.models.user.service.UserCoordinatorService;
import com.example.szakdolg.models.user.service.UserService;
import com.example.szakdolg.util.HashUtils;
import com.example.szakdolg.util.KeyStoreUtil;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivityHelper {

   private UserApiHelper userApiHelper = new UserApiHelper();
   private Context context;

   private UserService userService;
   private UserCoordinatorService userCoordinatorService;

   public RegisterActivityHelper(Context context) {
      this.context = context;
      this.userService = new UserService(context);
      this.userCoordinatorService = new UserCoordinatorService(context);
   }

   public void registerUser(
      String email,
      String pass,
      String pass2,
      String displayName
   ) {
      String hashPass = HashUtils.hashPassword(pass);
      HashMap<String, String> keyPair = KeyStoreUtil.generateKeyPair();

      User user = new User(
         displayName,
         email,
         hashPass,
         keyPair.get("Public"),
         UserConstans.STATUS_ACTIVE,
         UserConstans.TAG_PENDING
      );

      userCoordinatorService.registerUser(user, keyPair);
   }

   public boolean isEmailValid(String email) {
      String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
      Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
      Matcher matcher = pattern.matcher(email);
      return matcher.matches();
   }
}
