package com.example.szakdolg.activity.login;

import android.content.Context;
import android.content.Intent;
import com.example.szakdolg.constans.SharedPreferencesConstants;
import com.example.szakdolg.activity.main.activity.MainActivity;
import com.example.szakdolg.model.user.api.UserApiHelper;
import com.example.szakdolg.model.user.entity.User;
import com.example.szakdolg.model.user.service.UserService;
import com.example.szakdolg.util.HashUtils;
import com.example.szakdolg.util.SharedPreferencesUtil;

public class LoginActivityHelper {

   private UserApiHelper userApiHelper = new UserApiHelper();
   private Context context;

   private UserService userService;

   public LoginActivityHelper(Context context) {
      this.context = context;
      this.userService = new UserService(context);
   }

   public void loginUser(String email, String password) {
      String hashPassword = HashUtils.hashPassword(password);

      userApiHelper.getTokenByPasswordAndEmail(
         context,
         hashPassword,
         email,
         userToken -> {
            String token = userToken.getToken();
            SharedPreferencesUtil.setStringPreference(
               context,
               SharedPreferencesConstants.USERTOKEN,
               token
            );
            userApiHelper.getUserByToken(
               context,
               user -> {
                  String userId = user.getUserId().toString();

                  User localUser = userService.getUserByUserId(
                     user.getUserId(),
                     user
                  );
                  if (localUser == null) {
                     user.setAuthToken(token);
                     userService.addUser(user, user);
                  }

                  SharedPreferencesUtil.setStringPreference(
                     context,
                     SharedPreferencesConstants.USER_ID,
                     userId
                  );
                  Intent intent = new Intent(context, MainActivity.class);
                  context.startActivity(intent);
               },
               token
            );
         }
      );
   }
}
