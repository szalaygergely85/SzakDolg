package com.example.szakdolg.activity.register;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.EditText;

import com.example.szakdolg.activity.profile.ProfileActivity;
import com.example.szakdolg.activity.profile.ProfileConstants;
import com.example.szakdolg.constans.IntentConstants;
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

   private Activity activity;

   public RegisterActivityHelper(Activity activity) {
      this.activity = activity;
      this.userService = new UserService(context);
   }


}
