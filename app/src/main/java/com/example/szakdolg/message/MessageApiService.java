package com.example.szakdolg.message;

import com.example.szakdolg.MessageB;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MessageApiService {

    @GET("message/{token}/messageboardentry")
    Call<ArrayList<MessageB>> getLatestMessages(
            @Path("token") String token);

    @POST("message")
    Call<MessageEntry> sendMessage(
            @Body MessageEntry message
    );

    @GET("message/byconversationid/{id}")
    Call<ArrayList<MessageEntry>> getConversationMessages(
            @Path("id") long conversationId);
}
