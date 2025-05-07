package com.zen_vy.chat.notification;

import com.zen_vy.chat.retrofit.RetrofitClient;

public class NotificationApiHelper {

   private NotificationApiService notificationApiService = RetrofitClient
      .getRetrofitInstance()
      .create(NotificationApiService.class);
}
