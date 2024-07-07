package com.example.szakdolg.notification;

import com.example.szakdolg.DTO.ConversationContent;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface NotificationApiService {

    @GET("notification")
    Call<List<Notification>> getActiveNotifications(
            @Header("Authorization") String token
    );
}
