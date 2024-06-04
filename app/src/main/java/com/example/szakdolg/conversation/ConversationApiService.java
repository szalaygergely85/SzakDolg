package com.example.szakdolg.conversation;


import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ConversationApiService {

    @GET("conversation/id/{id}")
    Call<HashMap> getConversation(
            @Path("id") long conversationId);
}
