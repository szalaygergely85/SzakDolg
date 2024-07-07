package com.example.szakdolg.notification;

import com.example.szakdolg.retrofit.RetrofitClient;

public class NotificationApiHelper {

    private NotificationApiService notificationApiService = RetrofitClient
            .getRetrofitInstance()
            .create(NotificationApiService.class);
}
