package com.zen_vy.chat.activity.login;

import android.content.Context;
import android.content.Intent;
import com.google.android.material.textfield.TextInputLayout;
import com.zen_vy.chat.activity.main.MainActivity;
import com.zen_vy.chat.constans.SharedPreferencesConstants;
import com.zen_vy.chat.models.user.entity.User;
import com.zen_vy.chat.models.user.service.UserService;
import com.zen_vy.chat.util.HashUtils;
import com.zen_vy.chat.util.SharedPreferencesUtil;

public class LoginActivityHelper {

   private Context context;
   private UserService userService;

   private TextInputLayout editMailLayout;
   private TextInputLayout editPassLayout;

   public LoginActivityHelper(
      Context context,
      TextInputLayout editMailLayout,
      TextInputLayout editPassLayout
   ) {
      this.context = context;
      this.editMailLayout = editMailLayout;
      this.editPassLayout = editPassLayout;
      this.userService = new UserService(context);
   }

   public void loginUser(String email, String password) {
      String hashPassword = HashUtils.hashPassword(password);

      userService.getTokenByPasswordAndEmail(
         hashPassword,
         email,
         new UserService.LoginCallback<User>() {
            @Override
            public void onSuccess(User user) {
               String token = user.getToken();
               SharedPreferencesUtil.setStringPreference(
                  context,
                  SharedPreferencesConstants.USERTOKEN,
                  token
               );

               SharedPreferencesUtil.setStringPreference(
                  context,
                  SharedPreferencesConstants.UUID,
                  user.getUuid()
               );

               Intent intent = new Intent(context, MainActivity.class);
               context.startActivity(intent);
            }

            @Override
            public void onUserNotFound() {
               editMailLayout.setError(" ");
               editPassLayout.setError("Invalid email or password");
            }

            @Override
            public void onError(Throwable t) {}
         }
      );
   }
}
