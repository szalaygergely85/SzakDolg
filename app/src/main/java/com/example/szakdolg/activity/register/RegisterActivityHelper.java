package com.example.szakdolg.activity.register;

import android.content.Context;
import android.content.Intent;

import com.example.szakdolg.activity.profilepicture.ProfilePictureActivity;
import com.example.szakdolg.constans.SharedPreferencesConstants;
import com.example.szakdolg.models.user.constans.UserConstans;
import com.example.szakdolg.models.user.entity.User;
import com.example.szakdolg.models.user.service.UserService;
import com.example.szakdolg.util.HashUtils;
import com.example.szakdolg.util.KeyStoreUtil;
import com.example.szakdolg.util.SharedPreferencesUtil;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivityHelper {
   private Context context;

   private UserService userService;

   public RegisterActivityHelper(Context context) {
      this.context = context;
      this.userService = new UserService(context);
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

      userService.addUser(user, new UserService.UserCallback<User>() {
         @Override
         public void onSuccess(User data) {
            KeyStoreUtil.writePrivateKeysToFile(
                    context,
                    keyPair.get("Private"),
                    data
            );

            SharedPreferencesUtil.setStringPreference(
                    context,
                    SharedPreferencesConstants.USERTOKEN,
                    data.getToken()
            );
            SharedPreferencesUtil.setStringPreference(
                    context,
                    SharedPreferencesConstants.USER_ID,
                    data.getUserId().toString()
            );
            Intent intent = new Intent(context, ProfilePictureActivity.class);
            context.startActivity(intent);
         }

         @Override
         public void onError(Throwable t) {

         }
      });

   }

   public boolean isEmailValid(String email) {
      String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
      Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
      Matcher matcher = pattern.matcher(email);
      return matcher.matches();
   }
}
