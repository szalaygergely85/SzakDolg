package com.example.szakdolg.activity.login;

import android.content.Context;
import android.content.Intent;
import com.example.szakdolg.activity.main.MainActivity;
import com.example.szakdolg.constans.SharedPreferencesConstants;
import com.example.szakdolg.models.user.api.UserApiHelper;
import com.example.szakdolg.models.user.entity.User;
import com.example.szakdolg.models.user.entity.UserToken;
import com.example.szakdolg.models.user.service.UserService;
import com.example.szakdolg.util.HashUtils;
import com.example.szakdolg.util.SharedPreferencesUtil;

public class LoginActivityHelper {
   private Context context;
   private UserService userService;
   public LoginActivityHelper(Context context) {
      this.context = context;
      this.userService = new UserService(context);
   }

   public void loginUser(String email, String password) {
      String hashPassword = HashUtils.hashPassword(password);

      userService.getTokenByPasswordAndEmail(hashPassword,
              email, new UserService.UserCallback<UserToken>() {
                  @Override
                  public void onSuccess(UserToken data) {
                      String token = data.getToken();
                      SharedPreferencesUtil.setStringPreference(
                              context,
                              SharedPreferencesConstants.USERTOKEN,
                              token
                      );

                      userService.getUserByToken(token, new UserService.UserCallback<User>() {
                          @Override
                          public void onSuccess(User data) {
                              SharedPreferencesUtil.setStringPreference(
                                      context,
                                      SharedPreferencesConstants.USER_ID,
                                      data.getUserId().toString()
                              );
                              Intent intent = new Intent(context, MainActivity.class);
                              context.startActivity(intent);
                          }

                          @Override
                          public void onError(Throwable t) {

                          }
                      });
                  }

                  @Override
                  public void onError(Throwable t) {

                  }
              });

   }
}
