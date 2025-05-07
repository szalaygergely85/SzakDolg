package com.zen_vy.chat.activity.register;

import android.app.Activity;
import android.content.Context;

import com.zen_vy.chat.models.user.service.UserService;

public class RegisterActivityHelper {

   private Context context;

   private UserService userService;

   private Activity activity;

   public RegisterActivityHelper(Activity activity) {
      this.activity = activity;
      this.userService = new UserService(context);
   }


}
