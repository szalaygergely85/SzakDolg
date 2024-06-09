package com.example.szakdolg.conversation;


import java.util.HashMap;
import java.util.List;
import com.example.szakdolg.user.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ConversationApiService {

    @GET("conversation/id/{id}")
    Call<HashMap> getConversation(
            @Path("id") long conversationId);


    @POST("conversation")
    Call <Long> addConversation(
            @Body List<User> participants);

}