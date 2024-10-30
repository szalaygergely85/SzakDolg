package com.example.szakdolg.model.user.service;

import android.content.Context;
import com.example.szakdolg.model.user.entity.User;

public class ContactsService {

   private Context context;

   private User currentUser;

   public ContactsService(Context context, User currentUser) {
      this.context = context;
      this.currentUser = currentUser;
   }
}
