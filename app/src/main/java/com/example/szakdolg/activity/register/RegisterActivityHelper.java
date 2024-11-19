package com.example.szakdolg.activity.register;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import com.example.szakdolg.activity.profilepicture.ProfilePictureActivity;
import com.example.szakdolg.constans.SharedPreferencesConstants;
import com.example.szakdolg.models.user.api.UserApiHelper;
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

   private UserApiHelper userApiHelper = new UserApiHelper();
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
      String displayName,
      TextView txtRegError
   ) {
      if (_isUserDataValid(email, pass, pass2, displayName, txtRegError)) {
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

         userApiHelper.addUser(
            context,
            user,
            userToken -> {
               user.setAuthToken(userToken.getToken());
               user.setUserId(userToken.getUserId());

               userService.addUser(user, user);

               KeyStoreUtil.writePrivateKeysToFile(
                  context,
                  keyPair.get("Private"),
                  user
               );

               SharedPreferencesUtil.setStringPreference(
                  context,
                  SharedPreferencesConstants.USERTOKEN,
                  user.getAuthToken()
               );
               SharedPreferencesUtil.setStringPreference(
                  context,
                  SharedPreferencesConstants.USER_ID,
                  user.getUserId().toString()
               );

               Intent intent = new Intent(
                  context,
                  ProfilePictureActivity.class
               );
               context.startActivity(intent);
            }
         );
      }
   }

   private boolean _isEmailValid(String email) {
      String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
      Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
      Matcher matcher = pattern.matcher(email);
      return matcher.matches();
   }

   private boolean _isUserDataValid(
      String email,
      String pass,
      String pass2,
      String displayName,
      TextView txtRegError
   ) {
      if (
         email.isEmpty() ||
         pass.isEmpty() ||
         pass2.isEmpty() ||
         displayName.isEmpty()
      ) {
         txtRegError.setVisibility(View.VISIBLE);
         txtRegError.setText("Please fill in all fields.");
         return false;
      }
      if (!pass.equals(pass2)) {
         txtRegError.setVisibility(View.VISIBLE);
         txtRegError.setText("Passwords do not match. Please try again.");
         return false;
      }
      if (pass.length() < 6) {
         txtRegError.setVisibility(View.VISIBLE);
         txtRegError.setText("Password must be at least 6 characters long.");
         return false;
      }
      if (!_isEmailValid(email)) {
         txtRegError.setVisibility(View.VISIBLE);
         txtRegError.setText("Please enter a valid email address.");
         return false;
      }
      return true;
   }
}
