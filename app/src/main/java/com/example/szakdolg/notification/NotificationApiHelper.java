package com.example.szakdolg.notification;

import com.example.szakdolg.db.retrofit.RetrofitClient;

public class NotificationApiHelper {

   private NotificationApiService notificationApiService = RetrofitClient
      .getRetrofitInstance()
      .create(NotificationApiService.class);
}
