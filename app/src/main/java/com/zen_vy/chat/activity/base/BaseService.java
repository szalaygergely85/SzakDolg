package com.zen_vy.chat.activity.base;

import android.content.Context;
import com.zen_vy.chat.models.user.entity.User;

public class BaseService {

   protected Context context;
   protected User currentUser;

   public BaseService(Context context, User currentUser) {
      this.context = context;
      this.currentUser = currentUser;
   }
}
